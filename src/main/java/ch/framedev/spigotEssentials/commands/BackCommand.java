package ch.framedev.spigotEssentials.commands;

import ch.framedev.spigotEssentials.PaperEssentials;
import ch.framedev.spigotEssentials.utils.MessageConfig;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Command handler for the back command
 * Allows players to teleport to their previous location
 */
public class BackCommand extends AbstractCommand implements Listener {

    private final Map<Player, Location> lastLocations = new HashMap<>();
    private final PaperEssentials plugin;

    public BackCommand(PaperEssentials plugin) {
        this.plugin = plugin;
    }

    @Override
    protected boolean execute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (!plugin.getConfig().getBoolean("back-command", true)) {
            sendMessage(sender, MessageConfig.BACK_DISABLED);
            return true;
        }

        Player player = asPlayer(sender);
        if (player == null) {
            sendMessage(sender, MessageConfig.PLAYER_ONLY);
            return true;
        }

        if (!checkPermission(sender, "spigotessentials.back", MessageConfig.NO_PERMISSION_SELF)) {
            return true;
        }

        if (lastLocations.containsKey(player)) {
            Location lastLocation = lastLocations.get(player);
            if (lastLocation != null && lastLocation.getWorld() != null) {
                player.teleport(lastLocation);
                lastLocations.remove(player);
                sendMessage(player, MessageConfig.BACK_TELEPORTED);
            } else {
                lastLocations.remove(player);
                sendMessage(player, MessageConfig.BACK_NO_LOCATION);
            }
        } else {
            sendMessage(player, MessageConfig.BACK_NO_LOCATION);
        }
        return true;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (!plugin.getConfig().getBoolean("back-command", true)) return;

        Player player = event.getEntity();
        if (!player.hasPermission("spigotessentials.back")) return;

        Location deathLocation = player.getLocation();
        if (deathLocation != null && deathLocation.getWorld() != null) {
            lastLocations.put(player, deathLocation);
            sendMessage(player, MessageConfig.BACK_USE_COMMAND);
        }
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        if (!plugin.getConfig().getBoolean("back-command", true)) return;

        Player player = event.getPlayer();
        if (!player.hasPermission("spigotessentials.back")) return;

        Location fromLocation = event.getFrom();
        if (fromLocation != null && fromLocation.getWorld() != null) {
            lastLocations.put(player, fromLocation);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        // Clean up memory when the player leaves
        lastLocations.remove(event.getPlayer());
    }
}
