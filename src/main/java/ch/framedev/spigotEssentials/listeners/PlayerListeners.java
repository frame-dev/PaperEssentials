package ch.framedev.spigotEssentials.listeners;

import ch.framedev.spigotEssentials.PaperEssentials;
import ch.framedev.spigotEssentials.utils.MessageConfig;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Listener for player-related events
 */
public class PlayerListeners implements Listener {
    private static final PlainTextComponentSerializer PLAIN_TEXT_SERIALIZER = PlainTextComponentSerializer.plainText();

    private final PaperEssentials plugin;

    public PlayerListeners(PaperEssentials plugin) {
        this.plugin = plugin;
    }

    /**
     * Handle player join events
     * @param event The player joins the event
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        event.joinMessage(getConfiguredMessage("join-message", player));

        // First join broadcast
        if (!player.hasPlayedBefore() && plugin.getConfig().getBoolean("first-join-broadcast", true)) {
            String firstJoinMessage = plugin.getConfig().getString("first-join-message");
            if (firstJoinMessage != null && !firstJoinMessage.isEmpty()) {
                MessageConfig.broadcast(firstJoinMessage.replace("%player%", player.getName()));
            } else {
                MessageConfig.broadcast(MessageConfig.FIRST_JOIN_BROADCAST, player.getName());
            }
        }

    }

    /**
     * Handle player quit events
     * @param event The player quit the event
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        event.quitMessage(getConfiguredMessage("leave-message", event.getPlayer()));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSignChange(SignChangeEvent event) {
        if (!plugin.getConfig().getBoolean("sign-change-formatting", true)) {
            return;
        }

        for (int i = 0; i < 4; i++) {
            Component line = event.line(i);
            if (line == null) {
                continue;
            }

            String rawLine = PLAIN_TEXT_SERIALIZER.serialize(line);
            if (rawLine.contains("&") || rawLine.contains("§")) {
                event.line(i, MessageConfig.component(rawLine));
            }
        }
    }

    private Component getConfiguredMessage(String configPath, Player player) {
        String message = plugin.getConfig().getString(configPath);
        if (message == null || message.isEmpty()) {
            return null;
        }

        return MessageConfig.component(message.replace("%player%", player.getName()));
    }
}
