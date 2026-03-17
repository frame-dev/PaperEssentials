package ch.framedev.spigotEssentials.listeners;

import ch.framedev.spigotEssentials.PaperEssentials;
import ch.framedev.spigotEssentials.utils.MessageConfig;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Listener for player-related events
 */
public class PlayerListeners implements Listener {

    private final PaperEssentials plugin;

    public PlayerListeners(PaperEssentials plugin) {
        this.plugin = plugin;
    }

    /**
     * Handle player join events
     * @param event The player join event
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        // First join broadcast
        if (!player.hasPlayedBefore() && plugin.getConfig().getBoolean("first-join-broadcast", true)) {
            String firstJoinMessage = plugin.getConfig().getString("first-join-message");
            if (firstJoinMessage != null && !firstJoinMessage.isEmpty()) {
                String formatted = MessageConfig.colorize(firstJoinMessage.replace("%player%", player.getName()));
                plugin.getServer().broadcastMessage(formatted);
            } else {
                plugin.getServer().broadcastMessage(MessageConfig.format(MessageConfig.FIRST_JOIN_BROADCAST, player.getName()));
            }
        }

        // Regular join message
        String message = plugin.getConfig().getString("join-message");
        if (message != null && !message.isEmpty()) {
            String formattedMessage = MessageConfig.colorize(message.replace("%player%", player.getName()));
            player.sendMessage(formattedMessage);
        }
    }

    /**
     * Handle player quit events
     * @param event The player quit event
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        String message = plugin.getConfig().getString("leave-message");
        if (message != null && !message.isEmpty()) {
            Player player = event.getPlayer();
            String formattedMessage = MessageConfig.colorize(message.replace("%player%", player.getName()));
            player.sendMessage(formattedMessage);
        }
    }
}
