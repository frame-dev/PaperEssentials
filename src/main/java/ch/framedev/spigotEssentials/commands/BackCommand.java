package ch.framedev.spigotEssentials.commands;

import ch.framedev.spigotEssentials.SpigotEssentials;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
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

public class BackCommand implements CommandExecutor, Listener {

    private final Map<Player, Location> lastLocations = new HashMap<>();

    private final SpigotEssentials plugin;

    public BackCommand(SpigotEssentials plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (!plugin.getConfig().getBoolean("back-command")) {
            sender.sendMessage("The back command is disabled in the config.");
            return true;
        }
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }
        if (!sender.hasPermission("spigotessentials.back")) {
            sender.sendMessage("You do not have permission to use this command.");
            return true;
        }
        if (command.getName().equalsIgnoreCase("back")) {
            if (lastLocations.containsKey(player)) {
                player.teleport(lastLocations.get(player));
                lastLocations.remove(player);
                player.sendMessage("Teleported back to your last location!");
            } else {
                player.sendMessage("No previous location found.");
            }
            return true;
        }
        return false;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (!plugin.getConfig().getBoolean("back-command")) return;
        Player player = event.getEntity();
        if (!player.hasPermission("spigotessentials.back")) return;
        lastLocations.put(player, player.getLocation());
        player.sendMessage("Use /back to return to your last location.");
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        if (!plugin.getConfig().getBoolean("back-command")) return;
        Player player = event.getPlayer();
        if (!player.hasPermission("spigotessentials.back")) return;
        // Store the location before teleport
        lastLocations.put(player, event.getFrom());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        // Clean up memory when the player leaves
        lastLocations.remove(event.getPlayer());
    }
}
