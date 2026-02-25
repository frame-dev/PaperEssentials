package ch.framedev.spigotEssentials.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("SameReturnValue")
public class TeleportCommand implements CommandExecutor, Listener {

    // Store teleport requests with timestamps for expiration
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
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cOnly players can use this command.");
            return true;
        }

        String cmd = command.getName().toLowerCase();
        return switch (cmd) {
            case "tpa" -> handleTeleportRequest(player, args, tpaTeleportRequests, "tpa", "/tpaaccept", "/tpadeny");
            case "tpahere" ->
                    handleTeleportRequest(player, args, tpaHereTeleportRequests, "tpahere", "/tpahereaccept", "/tpaheredeny");
            case "tpaaccept" -> handleAcceptRequest(player, tpaTeleportRequests, true, "teleport");
            case "tpahereaccept" -> handleAcceptRequest(player, tpaHereTeleportRequests, false, "teleport here");
            case "tpadeny" -> handleDenyRequest(player, tpaTeleportRequests, "teleport");
            case "tpaheredeny" -> handleDenyRequest(player, tpaHereTeleportRequests, "teleport here");
            default -> false;
        };
    }

    private boolean handleTeleportRequest(Player player, String[] args, Map<Player, TeleportRequest> requests,
                                          String cmdType, String acceptCmd, String denyCmd) {
        if (args.length != 1) {
            player.sendMessage("§cUsage: §a/" + cmdType + " <player>");
            return true;
        }

        Player target = player.getServer().getPlayerExact(args[0]);
        if (target == null) {
            player.sendMessage("§cPlayer not found.");
            return true;
        }
        if (target.equals(player)) {
            player.sendMessage("§cYou cannot teleport to yourself.");
            return true;
        }

        requests.put(player, new TeleportRequest(target));
        player.sendMessage("Teleport request sent to " + target.getName() + ".");
        target.sendMessage(player.getName() + " has requested to teleport " +
                          (cmdType.equals("tpa") ? "to you" : "you to them") +
                          ". Type " + acceptCmd + " to accept or " + denyCmd + " to deny.");
        return true;
    }

    private boolean handleAcceptRequest(Player player, Map<Player, TeleportRequest> requests,
                                       boolean requesterToPlayer, String requestType) {
        Player requester = findRequester(player, requests);
        if (requester == null) {
            player.sendMessage("§cNo " + requestType + " requests found.");
            return true;
        }

        TeleportRequest request = requests.get(requester);
        if (request.isExpired()) {
            requests.remove(requester);
            player.sendMessage("§c" + capitalizeFirst(requestType) + " request has expired.");
            return true;
        }

        if (!requester.isOnline()) {
            requests.remove(requester);
            player.sendMessage("§cPlayer is no longer online.");
            return true;
        }

        if (requesterToPlayer) {
            requester.teleport(player);
            requester.sendMessage("§aYou have been teleported to " + player.getName() + ".");
        } else {
            player.teleport(requester);
            requester.sendMessage("§a" + player.getName() + " has teleported to you.");
        }

        player.sendMessage("§a" + capitalizeFirst(requestType) + " request accepted.");
        requests.remove(requester);
        return true;
    }

    private boolean handleDenyRequest(Player player, Map<Player, TeleportRequest> requests, String requestType) {
        Player requester = findRequester(player, requests);
        if (requester == null) {
            player.sendMessage("§cNo " + requestType + " requests found.");
            return true;
        }

        requests.remove(requester);
        player.sendMessage("§c" + capitalizeFirst(requestType) + " request from " + requester.getName() + " denied.");
        if (requester.isOnline()) {
            requester.sendMessage("§cYour " + requestType + " request to " + player.getName() + " was denied.");
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

    private String capitalizeFirst(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
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
