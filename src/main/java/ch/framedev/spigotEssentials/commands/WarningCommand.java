package ch.framedev.spigotEssentials.commands;

import ch.framedev.spigotEssentials.PaperEssentials;
import ch.framedev.spigotEssentials.managers.ModerationManager;
import ch.framedev.spigotEssentials.utils.MessageConfig;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Command handler for warn and warnings commands.
 */
public class WarningCommand extends AbstractCommand {
    private final PaperEssentials plugin;

    public WarningCommand(PaperEssentials plugin) {
        this.plugin = plugin;
    }

    @Override
    protected boolean execute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return switch (command.getName().toLowerCase(Locale.ROOT)) {
            case "warn" -> handleWarn(sender, args);
            case "warnings" -> handleWarnings(sender, args);
            default -> false;
        };
    }

    private boolean handleWarn(CommandSender sender, String[] args) {
        if (!checkPermission(sender, "spigotessentials.warn", MessageConfig.NO_PERMISSION_SELF)) {
            return true;
        }

        if (args.length < 1) {
            sendMessage(sender, MessageConfig.WARN_USAGE);
            return true;
        }

        OfflinePlayer target = resolveTarget(sender, args[0]);
        if (target == null) {
            return true;
        }

        String targetName = getTargetName(target, args[0]);
        String reason = args.length > 1
                ? String.join(" ", Arrays.copyOfRange(args, 1, args.length))
                : MessageConfig.DEFAULT_REASON;

        plugin.getModerationManager().addWarning(target.getUniqueId(), targetName, reason, sender.getName());
        sendMessage(sender, MessageConfig.WARN_ADDED, targetName, reason);
        if (target.isOnline() && target.getPlayer() != null) {
            sendMessage(target.getPlayer(), MessageConfig.WARN_TARGET, reason);
        }
        return true;
    }

    private boolean handleWarnings(CommandSender sender, String[] args) {
        if (!checkPermission(sender, "spigotessentials.warnings", MessageConfig.NO_PERMISSION_SELF)) {
            return true;
        }

        if (args.length != 1) {
            sendMessage(sender, MessageConfig.WARNINGS_USAGE);
            return true;
        }

        OfflinePlayer target = resolveTarget(sender, args[0]);
        if (target == null) {
            return true;
        }

        String targetName = getTargetName(target, args[0]);
        List<ModerationManager.WarningEntry> warningEntries = plugin.getModerationManager().getWarnings(target.getUniqueId());
        if (warningEntries.isEmpty()) {
            sendMessage(sender, MessageConfig.WARNINGS_NONE, targetName);
            return true;
        }

        sendMessage(sender, MessageConfig.WARNINGS_HEADER, targetName, warningEntries.size());
        for (int index = 0; index < warningEntries.size(); index++) {
            ModerationManager.WarningEntry entry = warningEntries.get(index);
            sendMessage(
                    sender,
                    MessageConfig.WARNINGS_ENTRY,
                    index + 1,
                    ModerationManager.formatTimestamp(entry.createdAt()),
                    entry.reason(),
                    entry.actor()
            );
        }
        return true;
    }

    private OfflinePlayer resolveTarget(CommandSender sender, String input) {
        var onlineTarget = plugin.getServer().getPlayerExact(input);
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
