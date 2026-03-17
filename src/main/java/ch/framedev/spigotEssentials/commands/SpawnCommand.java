package ch.framedev.spigotEssentials.commands;

import ch.framedev.spigotEssentials.LocationManager;
import ch.framedev.spigotEssentials.utils.MessageConfig;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Command handler for spawn-related commands
 */
public class SpawnCommand extends AbstractCommand implements Listener {

    private static final String SPAWN_KEY = "spawn";
    private final LocationManager locationManager = new LocationManager(SPAWN_KEY);

    @Override
    protected boolean execute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        Player player = asPlayer(sender);
        if (player == null) {
            sendMessage(sender, MessageConfig.PLAYER_ONLY);
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
        if (!checkPermission(player, "spigotessentials.spawn.set", MessageConfig.NO_PERMISSION_SELF)) {
            return true;
        }

        if (locationManager.saveLocation(player.getLocation())) {
            sendMessage(player, MessageConfig.SPAWN_SET);
        } else {
            sendMessage(player, "§cFailed to save spawn location.");
        }
        return true;
    }

    private boolean handleSpawn(Player player) {
        if (!locationManager.hasLocation()) {
            sendMessage(player, MessageConfig.SPAWN_NOT_SET);
            return true;
        }

        Location spawnLocation = locationManager.getLocation();
        if (spawnLocation == null) {
            sendMessage(player, MessageConfig.SPAWN_NOT_SET);
            return true;
        }

        player.teleport(spawnLocation);
        sendMessage(player, MessageConfig.SPAWN_TELEPORTED);
        return true;
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        if (!locationManager.hasLocation()) return;

        Location spawnLocation = locationManager.getLocation();
        if (spawnLocation == null) return;

        Player player = event.getPlayer();
        if (player.getRespawnLocation() == null) {
            event.setRespawnLocation(spawnLocation);
            sendMessage(player, MessageConfig.SPAWN_TELEPORTED_RESPAWN);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (player.hasPlayedBefore() || !locationManager.hasLocation()) return;

        Location spawnLocation = locationManager.getLocation();
        if (spawnLocation == null) return;

        player.teleport(spawnLocation);
        sendMessage(player, MessageConfig.SPAWN_TELEPORTED_JOIN);
    }
}