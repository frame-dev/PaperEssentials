package ch.framedev.spigotEssentials.commands;

import ch.framedev.spigotEssentials.PaperEssentials;
import ch.framedev.spigotEssentials.utils.MessageConfig;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Command to freeze/unfreeze players, preventing movement
 */
public class FreezeCommand extends AbstractCommand implements Listener {

    private static final Set<UUID> frozenPlayers = new HashSet<>();

    @Override
    protected boolean execute(CommandSender sender, Command command, String label, String[] args) {
        if (!checkPermission(sender, "spigotessentials.freeze", MessageConfig.NO_PERMISSION_SELF)) {
            return true;
        }

        if (args.length == 0) {
            sendMessage(sender, MessageConfig.FREEZE_USAGE);
            return true;
        }

        Player target = getPlayer(sender, args[0]);
        if (target == null) return true;

        UUID targetUUID = target.getUniqueId();

        if (frozenPlayers.contains(targetUUID)) {
            frozenPlayers.remove(targetUUID);
            sendMessage(sender, MessageConfig.FREEZE_UNFROZEN, target.getName());
            sendMessage(target, MessageConfig.FREEZE_UNFROZEN_TARGET);
        } else {
            frozenPlayers.add(targetUUID);
            sendMessage(sender, MessageConfig.FREEZE_FROZEN, target.getName());
            sendMessage(target, MessageConfig.FREEZE_FROZEN_TARGET);
        }

        return true;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (frozenPlayers.contains(player.getUniqueId())) {
            // Only cancel if player actually moved (not just head movement)
            if (event.getFrom().getX() != event.getTo().getX() ||
                event.getFrom().getY() != event.getTo().getY() ||
                event.getFrom().getZ() != event.getTo().getZ()) {
                event.setCancelled(true);
                sendMessage(player, MessageConfig.FREEZE_CANNOT_MOVE);
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        // Clean up frozen players when they log out
        frozenPlayers.remove(event.getPlayer().getUniqueId());
    }

    public static boolean isFrozen(UUID playerUUID) {
        return frozenPlayers.contains(playerUUID);
    }

    public static void unfreeze(UUID playerUUID) {
        frozenPlayers.remove(playerUUID);
    }
}
