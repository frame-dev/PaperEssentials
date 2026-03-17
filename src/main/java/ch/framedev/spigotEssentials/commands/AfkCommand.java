package ch.framedev.spigotEssentials.commands;

import ch.framedev.spigotEssentials.PaperEssentials;
import ch.framedev.spigotEssentials.utils.MessageConfig;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Command to toggle AFK status with auto-AFK detection
 */
public class AfkCommand extends AbstractCommand implements Listener {

    private final Set<Player> afkPlayers = new HashSet<>();
    private final Map<Player, Long> lastActivity = new HashMap<>();
    private final PaperEssentials plugin;
    private static final long AUTO_AFK_TIME = 300000; // 5 minutes in milliseconds

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
        if (afkPlayers.contains(player)) {
            afkPlayers.remove(player);
            lastActivity.put(player, System.currentTimeMillis());
            sendMessage(player, MessageConfig.AFK_DISABLED);
            Bukkit.broadcastMessage(MessageConfig.format(MessageConfig.AFK_BROADCAST_DISABLED, player.getName()));
        } else {
            afkPlayers.add(player);
            sendMessage(player, MessageConfig.AFK_ENABLED);
            if (auto) {
                Bukkit.broadcastMessage(MessageConfig.format(MessageConfig.AFK_AUTO, player.getName()));
            } else {
                Bukkit.broadcastMessage(MessageConfig.format(MessageConfig.AFK_BROADCAST_ENABLED, player.getName()));
            }
        }
    }

    private void updateActivity(Player player) {
        lastActivity.put(player, System.currentTimeMillis());
        if (afkPlayers.contains(player)) {
            afkPlayers.remove(player);
            Bukkit.broadcastMessage(MessageConfig.format(MessageConfig.AFK_BROADCAST_DISABLED, player.getName()));
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
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        updateActivity(event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        afkPlayers.remove(player);
        lastActivity.remove(player);
    }

    private void startAutoAfkChecker() {
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            if (!plugin.getConfig().getBoolean("auto-afk-enabled", true)) {
                return;
            }

            long currentTime = System.currentTimeMillis();
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (afkPlayers.contains(player)) continue;

                Long lastActive = lastActivity.get(player);
                if (lastActive == null) {
                    lastActivity.put(player, currentTime);
                    continue;
                }

                if (currentTime - lastActive > AUTO_AFK_TIME) {
                    toggleAFK(player, true);
                }
            }
        }, 20L * 30, 20L * 30); // Check every 30 seconds
    }

    public boolean isAfk(Player player) {
        return afkPlayers.contains(player);
    }
}
