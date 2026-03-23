package ch.framedev.spigotEssentials.commands;

import ch.framedev.spigotEssentials.PaperEssentials;
import ch.framedev.spigotEssentials.managers.ModerationManager;
import ch.framedev.spigotEssentials.utils.MessageConfig;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Command handler for utility moderation commands such as mute inspection, warning cleanup, and staff listing.
 */
public class ModerationUtilityCommand extends AbstractCommand {
    private final PaperEssentials plugin;

    public ModerationUtilityCommand(PaperEssentials plugin) {
        this.plugin = plugin;
    }

    @Override
    protected boolean execute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return switch (command.getName().toLowerCase(Locale.ROOT)) {
            case "muteinfo" -> handleMuteInfo(sender, args);
            case "clearwarnings" -> handleClearWarnings(sender, args);
            case "stafflist" -> handleStaffList(sender, args);
            default -> false;
        };
    }

    private boolean handleMuteInfo(CommandSender sender, String[] args) {
        if (!checkPermission(sender, "spigotessentials.muteinfo", MessageConfig.NO_PERMISSION_SELF)) {
            return true;
        }

        if (args.length != 1) {
            sendMessage(sender, MessageConfig.MUTEINFO_USAGE);
            return true;
        }

        OfflinePlayer target = resolveTarget(sender, args[0]);
        if (target == null) {
            return true;
        }

        String targetName = getTargetName(target, args[0]);
        ModerationManager.MuteRecord muteRecord = plugin.getModerationManager().getActiveMute(target.getUniqueId());
        if (muteRecord == null) {
            sendMessage(sender, MessageConfig.MUTEINFO_NONE, targetName);
            return true;
        }

        if (muteRecord.isTemporary()) {
            sendMessage(
                    sender,
                    MessageConfig.MUTEINFO_TEMP,
                    targetName,
                    ModerationManager.formatDuration(muteRecord.getRemainingMillis(System.currentTimeMillis())),
                    muteRecord.reason(),
                    muteRecord.actor(),
                    ModerationManager.formatTimestamp(muteRecord.createdAt())
            );
        } else {
            sendMessage(
                    sender,
                    MessageConfig.MUTEINFO_PERMANENT,
                    targetName,
                    muteRecord.reason(),
                    muteRecord.actor(),
                    ModerationManager.formatTimestamp(muteRecord.createdAt())
            );
        }
        return true;
    }

    private boolean handleClearWarnings(CommandSender sender, String[] args) {
        if (!checkPermission(sender, "spigotessentials.clearwarnings", MessageConfig.NO_PERMISSION_SELF)) {
            return true;
        }

        if (args.length != 1) {
            sendMessage(sender, MessageConfig.CLEARWARNINGS_USAGE);
            return true;
        }

        OfflinePlayer target = resolveTarget(sender, args[0]);
        if (target == null) {
            return true;
        }

        String targetName = getTargetName(target, args[0]);
        int clearedWarnings = plugin.getModerationManager().clearWarnings(target.getUniqueId());
        if (clearedWarnings == 0) {
            sendMessage(sender, MessageConfig.CLEARWARNINGS_NONE, targetName);
            return true;
        }

        sendMessage(sender, MessageConfig.CLEARWARNINGS_SUCCESS, clearedWarnings, targetName);
        if (target.isOnline() && target.getPlayer() != null) {
            sendMessage(target.getPlayer(), MessageConfig.CLEARWARNINGS_TARGET, sender.getName(), clearedWarnings);
        }
        return true;
    }

    private boolean handleStaffList(CommandSender sender, String[] args) {
        if (!checkPermission(sender, "spigotessentials.stafflist", MessageConfig.NO_PERMISSION_SELF)) {
            return true;
        }

        if (args.length != 0) {
            sendMessage(sender, MessageConfig.STAFFLIST_USAGE);
            return true;
        }

        boolean canSeeVanished = sender.hasPermission("spigotessentials.vanish.see");
        List<String> visibleStaff = new ArrayList<>();

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (!onlinePlayer.hasPermission("spigotessentials.staffchat")) {
                continue;
            }

            boolean vanished = VanishCommand.isVanished(onlinePlayer.getUniqueId());
            if (vanished && !canSeeVanished) {
                continue;
            }

            String displayName = vanished ? onlinePlayer.getName() + " (vanished)" : onlinePlayer.getName();
            visibleStaff.add(displayName);
        }

        if (visibleStaff.isEmpty()) {
            sendMessage(sender, MessageConfig.STAFFLIST_EMPTY);
            return true;
        }

        sendMessage(sender, MessageConfig.STAFFLIST_FORMAT, visibleStaff.size(), String.join(", ", visibleStaff));
        return true;
    }

    private OfflinePlayer resolveTarget(CommandSender sender, String input) {
        Player onlineTarget = plugin.getServer().getPlayerExact(input);
        if (onlineTarget != null) {
            return onlineTarget;
        }

        OfflinePlayer offlineTarget = plugin.getServer().getOfflinePlayer(input);
        if (!offlineTarget.hasPlayedBefore() && !offlineTarget.isOnline()) {
            sendMessage(sender, MessageConfig.PLAYER_NOT_FOUND);
            return null;
        }

        return offlineTarget;
    }

    private String getTargetName(OfflinePlayer target, String fallback) {
        return target.getName() != null ? target.getName() : fallback;
    }
}
