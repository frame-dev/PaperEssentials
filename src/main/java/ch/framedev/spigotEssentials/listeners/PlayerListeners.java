package ch.framedev.spigotEssentials.listeners;

import ch.framedev.spigotEssentials.SpigotEssentials;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListeners implements Listener {

    private final SpigotEssentials plugin;

    public PlayerListeners(SpigotEssentials plugin) {
        this.plugin = plugin;
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        String message = plugin.getConfig().getString("join-message");
        if (message != null && !message.isEmpty()) {
            event.getPlayer().sendMessage(message.replace("&", "§"));
        }
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        String message = plugin.getConfig().getString("leave-message");
        if (message != null && !message.isEmpty()) {
            event.getPlayer().sendMessage(message.replace("&", "§"));
        }
    }
}
