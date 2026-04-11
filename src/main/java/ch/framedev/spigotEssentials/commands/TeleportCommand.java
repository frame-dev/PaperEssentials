package ch.framedev.spigotEssentials.commands;

import ch.framedev.spigotEssentials.utils.MessageConfig;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Command handler for teleport request commands (TPA/TPAHere)
 */
public class TeleportCommand extends AbstractCommand implements Listener {

    private final Map<Player, TeleportRequest> tpaTeleportRequests = new HashMap<>();
    private final Map<Player, TeleportRequest> tpaHereTeleportRequests = new HashMap<>();

    private static final long REQUEST_TIMEOUT = 60000; // 60 seconds

    private static class TeleportRequest {
        final Player target;
        final long timestamp;

        TeleportRequest(Player target) {
            this.target = target;
            this.timestamp = System.currentTimeMillis();
        }

        boolean isExpired() {
            return System.currentTimeMillis() - timestamp > REQUEST_TIMEOUT;
        }
    }

    @Override
    protected boolean execute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        Player player = asPlayer(sender);
        if (player == null) {
            sendMessage(sender, MessageConfig.PLAYER_ONLY);
            return true;
        }

        String cmd = command.getName().toLowerCase();
        return switch (cmd) {
            case "tpa" -> handleTeleportRequest(player, args, tpaTeleportRequests, "tpa", "/tpaaccept", "/tpadeny");
            case "tpahere" ->
                    handleTeleportRequest(player, args, tpaHereTeleportRequests, "tpahere", "/tpahereaccept", "/tpaheredeny");
            case "tpaaccept" -> handleAcceptRequest(player, tpaTeleportRequests, true);
            case "tpahereaccept" -> handleAcceptRequest(player, tpaHereTeleportRequests, false);
            case "tpadeny" -> handleDenyRequest(player, tpaTeleportRequests);
            case "tpaheredeny" -> handleDenyRequest(player, tpaHereTeleportRequests);
            default -> false;
        };
    }

    private boolean handleTeleportRequest(Player player, String[] args, Map<Player, TeleportRequest> requests,
                                          String cmdType, String acceptCmd, String denyCmd) {
        if (args.length != 1) {
            sendMessage(player, MessageConfig.TELEPORT_REQUEST_USAGE, cmdType);
            return true;
        }

        Player target = getPlayer(player, args[0]);
        if (target == null) {
            return true;
        }
        if (target.equals(player)) {
            sendMessage(player, MessageConfig.TPA_CANNOT_SELF);
            return true;
        }

        requests.put(player, new TeleportRequest(target));
        sendMessage(player, MessageConfig.TPA_SENT, target.getName());

        if (cmdType.equals("tpa")) {
            sendMessage(target, MessageConfig.TPA_RECEIVED, player.getName(), acceptCmd, denyCmd);
        } else {
            sendMessage(target, MessageConfig.TPAHERE_RECEIVED, player.getName(), acceptCmd, denyCmd);
        }
        return true;
    }

    private boolean handleAcceptRequest(Player player, Map<Player, TeleportRequest> requests,
                                       boolean requesterToPlayer) {
        Player requester = findRequester(player, requests);
        if (requester == null) {
            sendMessage(player, MessageConfig.TPA_NO_REQUEST);
            return true;
        }

        TeleportRequest request = requests.get(requester);
        if (request.isExpired()) {
            requests.remove(requester);
            sendMessage(player, MessageConfig.TPA_EXPIRED);
            return true;
        }

        if (!requester.isOnline()) {
            requests.remove(requester);
            sendMessage(player, MessageConfig.TPA_PLAYER_OFFLINE);
            return true;
        }

        if (requesterToPlayer) {
            requester.teleport(player);
            sendMessage(requester, MessageConfig.TPA_ACCEPTED_SENDER, player.getName());
        } else {
            player.teleport(requester);
            sendMessage(requester, MessageConfig.TPAHERE_ACCEPTED_SENDER, player.getName());
        }

        sendMessage(player, MessageConfig.TPA_ACCEPTED_TARGET);
        requests.remove(requester);
        return true;
    }

    private boolean handleDenyRequest(Player player, Map<Player, TeleportRequest> requests) {
        Player requester = findRequester(player, requests);
        if (requester == null) {
            sendMessage(player, MessageConfig.TPA_NO_REQUEST);
            return true;
        }

        requests.remove(requester);
        sendMessage(player, MessageConfig.TPA_DENIED_TARGET, requester.getName());
        if (requester.isOnline()) {
            sendMessage(requester, MessageConfig.TPA_DENIED_SENDER, player.getName());
        }
        return true;
    }

    private Player findRequester(Player target, Map<Player, TeleportRequest> requests) {
        return requests.entrySet().stream()
                .filter(entry -> entry.getValue().target.equals(target))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        // Clean up any requests involving the disconnecting player
        tpaTeleportRequests.remove(player);
        tpaHereTeleportRequests.remove(player);

        // Remove requests where this player is the target
        tpaTeleportRequests.entrySet().removeIf(entry -> entry.getValue().target.equals(player));
        tpaHereTeleportRequests.entrySet().removeIf(entry -> entry.getValue().target.equals(player));
    }
}
