package ch.framedev.spigotEssentials.commands;

import ch.framedev.spigotEssentials.utils.MessageConfig;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Command handler for private messaging features.
 */
public class PrivateMessageCommand extends AbstractCommand implements Listener {
    private final Map<UUID, UUID> lastMessaged = new HashMap<>();
    private final Set<UUID> messageToggleDisabled = new HashSet<>();
    private final Map<UUID, Set<UUID>> ignoredPlayers = new HashMap<>();

    @Override
    protected boolean execute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return switch (command.getName().toLowerCase(Locale.ROOT)) {
            case "msg" -> handleMessage(sender, args);
            case "reply" -> handleReply(sender, args);
            case "ignore" -> handleIgnore(sender, args);
            case "msgtoggle" -> handleMessageToggle(sender, args);
            default -> false;
        };
    }

    private boolean handleMessage(CommandSender sender, String[] args) {
        Player player = asPlayer(sender);
        if (player == null) {
            sendMessage(sender, MessageConfig.PLAYER_ONLY);
            return true;
        }

        if (!checkPermission(sender, "spigotessentials.msg", MessageConfig.NO_PERMISSION_SELF)) {
            return true;
        }

        if (args.length < 2) {
            sendMessage(sender, MessageConfig.MSG_USAGE);
            return true;
        }

        Player target = getPlayer(sender, args[0]);
        if (target == null) {
            return true;
        }

        String message = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        return sendPrivateMessage(player, target, message);
    }

    private boolean handleReply(CommandSender sender, String[] args) {
        Player player = asPlayer(sender);
        if (player == null) {
            sendMessage(sender, MessageConfig.PLAYER_ONLY);
            return true;
        }

        if (!checkPermission(sender, "spigotessentials.reply", MessageConfig.NO_PERMISSION_SELF)) {
            return true;
        }

        if (args.length == 0) {
            sendMessage(sender, MessageConfig.REPLY_USAGE);
            return true;
        }

        UUID targetId = lastMessaged.get(player.getUniqueId());
        if (targetId == null) {
            sendMessage(sender, MessageConfig.REPLY_NO_TARGET);
            return true;
        }

        Player target = player.getServer().getPlayer(targetId);
        if (target == null) {
            lastMessaged.remove(player.getUniqueId());
            sendMessage(sender, MessageConfig.REPLY_PLAYER_OFFLINE);
            return true;
        }

        String message = String.join(" ", args);
        return sendPrivateMessage(player, target, message);
    }

    private boolean handleIgnore(CommandSender sender, String[] args) {
        Player player = asPlayer(sender);
        if (player == null) {
            sendMessage(sender, MessageConfig.PLAYER_ONLY);
            return true;
        }

        if (!checkPermission(sender, "spigotessentials.ignore", MessageConfig.NO_PERMISSION_SELF)) {
            return true;
        }

        if (args.length != 1) {
            sendMessage(sender, MessageConfig.IGNORE_USAGE);
            return true;
        }

        OfflinePlayer target = resolveOfflineTarget(args[0]);
        if (target == null) {
            sendMessage(sender, MessageConfig.PLAYER_NOT_FOUND);
            return true;
        }

        if (target.getUniqueId().equals(player.getUniqueId())) {
            sendMessage(sender, MessageConfig.MSG_CANNOT_SELF);
            return true;
        }

        Set<UUID> ignoredTargets = ignoredPlayers.computeIfAbsent(player.getUniqueId(), ignored -> new HashSet<>());
        String targetName = target.getName() != null ? target.getName() : args[0];
        if (ignoredTargets.contains(target.getUniqueId())) {
            ignoredTargets.remove(target.getUniqueId());
            sendMessage(sender, MessageConfig.IGNORE_DISABLED, targetName);
        } else {
            ignoredTargets.add(target.getUniqueId());
            sendMessage(sender, MessageConfig.IGNORE_ENABLED, targetName);
        }

        if (ignoredTargets.isEmpty()) {
            ignoredPlayers.remove(player.getUniqueId());
        }
        return true;
    }

    private boolean handleMessageToggle(CommandSender sender, String[] args) {
        Player player = asPlayer(sender);
        if (player == null) {
            sendMessage(sender, MessageConfig.PLAYER_ONLY);
            return true;
        }

        if (!checkPermission(sender, "spigotessentials.msgtoggle", MessageConfig.NO_PERMISSION_SELF)) {
            return true;
        }

        if (args.length != 0) {
            sendMessage(sender, MessageConfig.MSGTOGGLE_USAGE);
            return true;
        }

        UUID playerId = player.getUniqueId();
        if (messageToggleDisabled.contains(playerId)) {
            messageToggleDisabled.remove(playerId);
            sendMessage(sender, MessageConfig.MSGTOGGLE_DISABLED);
        } else {
            messageToggleDisabled.add(playerId);
            sendMessage(sender, MessageConfig.MSGTOGGLE_ENABLED);
        }
        return true;
    }

    private boolean sendPrivateMessage(Player sender, Player target, String message) {
        if (sender.getUniqueId().equals(target.getUniqueId())) {
            sendMessage(sender, MessageConfig.MSG_CANNOT_SELF);
            return true;
        }

        if (messageToggleDisabled.contains(target.getUniqueId())) {
            sendMessage(sender, MessageConfig.MSG_TARGET_DISABLED, target.getName());
            return true;
        }

        Set<UUID> ignoredTargets = ignoredPlayers.get(target.getUniqueId());
        if (ignoredTargets != null && ignoredTargets.contains(sender.getUniqueId())) {
            sendMessage(sender, MessageConfig.MSG_IGNORED, target.getName());
            return true;
        }

        sendMessage(sender, MessageConfig.MSG_SENT, target.getName(), message);
        sendMessage(target, MessageConfig.MSG_RECEIVED, sender.getName(), message);
        lastMessaged.put(sender.getUniqueId(), target.getUniqueId());
        lastMessaged.put(target.getUniqueId(), sender.getUniqueId());
        return true;
    }

    private OfflinePlayer resolveOfflineTarget(String playerName) {
        Player onlinePlayer = org.bukkit.Bukkit.getPlayerExact(playerName);
        if (onlinePlayer != null) {
            return onlinePlayer;
        }

        OfflinePlayer offlinePlayer = org.bukkit.Bukkit.getOfflinePlayer(playerName);
        if (!offlinePlayer.hasPlayedBefore() && !offlinePlayer.isOnline()) {
            return null;
        }

        return offlinePlayer;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        UUID playerId = event.getPlayer().getUniqueId();
        lastMessaged.remove(playerId);
        lastMessaged.entrySet().removeIf(entry -> entry.getValue().equals(playerId));
    }
}
