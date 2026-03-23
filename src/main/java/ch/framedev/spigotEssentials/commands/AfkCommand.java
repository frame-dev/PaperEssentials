package ch.framedev.spigotEssentials.commands;

import ch.framedev.spigotEssentials.PaperEssentials;
import ch.framedev.spigotEssentials.utils.MessageConfig;
import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Command to toggle AFK status with auto-AFK detection
 */
public class AfkCommand extends AbstractCommand implements Listener {

    private static final long MILLIS_PER_SECOND = 1000L;
    private static final long AUTO_AFK_CHECK_INTERVAL_TICKS = 20L * 30;

    private final Set<UUID> afkPlayers = new HashSet<>();
    private final Map<UUID, Long> lastActivity = new HashMap<>();
    private final PaperEssentials plugin;

    public AfkCommand(PaperEssentials plugin) {
        this.plugin = plugin;
        startAutoAfkChecker();
    }

    @Override
    protected boolean execute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        Player player = asPlayer(sender);
        if (player == null) {
            sendMessage(sender, MessageConfig.PLAYER_ONLY);
            return true;
        }

        if (!checkPermission(sender, "spigotessentials.afk", MessageConfig.NO_PERMISSION_SELF)) {
            return true;
        }

        toggleAFK(player, false);
        return true;
    }

    private void toggleAFK(Player player, boolean auto) {
        UUID playerId = player.getUniqueId();
        if (afkPlayers.contains(playerId)) {
            afkPlayers.remove(playerId);
            lastActivity.put(playerId, System.currentTimeMillis());
            sendMessage(player, MessageConfig.AFK_DISABLED);
            MessageConfig.broadcast(MessageConfig.AFK_BROADCAST_DISABLED, player.getName());
        } else {
            afkPlayers.add(playerId);
            sendMessage(player, MessageConfig.AFK_ENABLED);
            if (auto) {
                MessageConfig.broadcast(MessageConfig.AFK_AUTO, player.getName());
            } else {
                MessageConfig.broadcast(MessageConfig.AFK_BROADCAST_ENABLED, player.getName());
            }
        }
    }

    private void updateActivity(Player player) {
        UUID playerId = player.getUniqueId();
        lastActivity.put(playerId, System.currentTimeMillis());
        if (afkPlayers.contains(playerId)) {
            afkPlayers.remove(playerId);
            sendMessage(player, MessageConfig.AFK_DISABLED);
            MessageConfig.broadcast(MessageConfig.AFK_BROADCAST_DISABLED, player.getName());
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.getFrom().getBlockX() != event.getTo().getBlockX() ||
            event.getFrom().getBlockY() != event.getTo().getBlockY() ||
            event.getFrom().getBlockZ() != event.getTo().getBlockZ()) {
            updateActivity(event.getPlayer());
        }
    }

    @EventHandler
    public void onPlayerChat(AsyncChatEvent event) {
        Bukkit.getScheduler().runTask(plugin, () -> updateActivity(event.getPlayer()));
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();
        afkPlayers.remove(playerId);
        lastActivity.remove(playerId);
    }

    private long getAutoAfkTimeMillis() {
        long autoAfkTimeSeconds = plugin.getConfig().getLong("auto-afk-time", 300L);
        return Math.max(1L, autoAfkTimeSeconds) * MILLIS_PER_SECOND;
    }

    private void startAutoAfkChecker() {
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            if (!plugin.getConfig().getBoolean("auto-afk-enabled", true)) {
                return;
            }

            long currentTime = System.currentTimeMillis();
            long autoAfkTimeMillis = getAutoAfkTimeMillis();
            for (Player player : Bukkit.getOnlinePlayers()) {
                UUID playerId = player.getUniqueId();
                if (afkPlayers.contains(playerId)) {
                    continue;
                }

                Long lastActive = lastActivity.get(playerId);
                if (lastActive == null) {
                    lastActivity.put(playerId, currentTime);
                    continue;
                }

                if (currentTime - lastActive > autoAfkTimeMillis) {
                    toggleAFK(player, true);
                }
            }
        }, AUTO_AFK_CHECK_INTERVAL_TICKS, AUTO_AFK_CHECK_INTERVAL_TICKS);
    }

    public boolean isAfk(Player player) {
        return afkPlayers.contains(player.getUniqueId());
    }
}
