package ch.framedev.spigotEssentials.commands;

import ch.framedev.spigotEssentials.LocationManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("SameReturnValue")
public class SpawnCommand implements CommandExecutor, Listener {

    private static final String SPAWN_KEY = "spawn";
    private final LocationManager locationManager = new LocationManager(SPAWN_KEY);

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        String cmd = command.getName().toLowerCase();
        return switch (cmd) {
            case "setspawn" -> handleSetSpawn(player);
            case "spawn" -> handleSpawn(player);
            default -> false;
        };
    }

    private boolean handleSetSpawn(Player player) {
        if (!player.hasPermission("spigotessentials.setspawn")) {
            player.sendMessage("You do not have permission to use this command.");
            return true;
        }

        locationManager.saveLocation(player.getLocation());
        player.sendMessage("Spawn point set!");
        return true;
    }

    private boolean handleSpawn(Player player) {
        if (locationManager.locationExists()) {
            player.teleport(locationManager.getLocation());
            player.sendMessage("Teleported to spawn point!");
        } else {
            player.sendMessage("Spawn point is not set.");
        }
        return true;
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        if (!locationManager.locationExists()) return;

        Player player = event.getPlayer();
        if (player.getRespawnLocation() == null) {
            event.setRespawnLocation(locationManager.getLocation());
            player.sendMessage("Teleported to spawn point on respawn!");
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (player.hasPlayedBefore() || !locationManager.locationExists()) return;

        player.teleport(locationManager.getLocation());
        player.sendMessage("Teleported to spawn point on join!");
    }
}