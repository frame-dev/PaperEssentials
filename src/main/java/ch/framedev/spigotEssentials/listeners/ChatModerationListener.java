package ch.framedev.spigotEssentials.listeners;

import ch.framedev.spigotEssentials.PaperEssentials;
import ch.framedev.spigotEssentials.managers.ModerationManager;
import ch.framedev.spigotEssentials.utils.MessageConfig;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Handles staff chat routing and public chat restrictions.
 */
public class ChatModerationListener implements Listener {
    private static final PlainTextComponentSerializer PLAIN_TEXT_SERIALIZER = PlainTextComponentSerializer.plainText();

    private final PaperEssentials plugin;

    public ChatModerationListener(PaperEssentials plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onAsyncChat(AsyncChatEvent event) {
        Player player = event.getPlayer();
        ModerationManager moderationManager = plugin.getModerationManager();

        if (moderationManager.isStaffChatEnabled(player.getUniqueId())) {
            if (!player.hasPermission("spigotessentials.staffchat")) {
                moderationManager.setStaffChatEnabled(player.getUniqueId(), false);
            } else {
                String message = PLAIN_TEXT_SERIALIZER.serialize(event.message());
                event.setCancelled(true);
                Bukkit.getScheduler().runTask(plugin, () -> moderationManager.sendStaffChat(player.getName(), message));
                return;
            }
        }

        ModerationManager.MuteRecord muteRecord = moderationManager.getActiveMute(player.getUniqueId());
        if (muteRecord != null) {
            String blockedMessage = muteRecord.isTemporary()
                    ? MessageConfig.format(
                            MessageConfig.MUTE_BLOCKED_TEMP,
                            ModerationManager.formatDuration(muteRecord.getRemainingMillis(System.currentTimeMillis())),
                            muteRecord.reason()
                    )
                    : MessageConfig.format(MessageConfig.MUTE_BLOCKED_PERMANENT, muteRecord.reason());

            event.setCancelled(true);
            Bukkit.getScheduler().runTask(plugin, () -> MessageConfig.send(player, blockedMessage));
            return;
        }

        if (moderationManager.isGlobalChatMuted() && !player.hasPermission("spigotessentials.chatmute.bypass")) {
            event.setCancelled(true);
            Bukkit.getScheduler().runTask(plugin, () -> MessageConfig.send(player, MessageConfig.CHATMUTE_BLOCKED));
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        plugin.getModerationManager().clearSessionState(event.getPlayer().getUniqueId());
    }
}
