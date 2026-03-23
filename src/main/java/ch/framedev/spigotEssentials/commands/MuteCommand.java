package ch.framedev.spigotEssentials.commands;

import ch.framedev.spigotEssentials.PaperEssentials;
import ch.framedev.spigotEssentials.managers.ModerationManager;
import ch.framedev.spigotEssentials.utils.MessageConfig;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Command handler for mute, tempmute, and unmute commands.
 */
public class MuteCommand extends AbstractCommand {
    private static final Pattern DURATION_PATTERN = Pattern.compile("(?i)^(\\d+)([smhdw])$");

    private final PaperEssentials plugin;

    public MuteCommand(PaperEssentials plugin) {
        this.plugin = plugin;
    }

    @Override
    protected boolean execute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return switch (command.getName().toLowerCase(Locale.ROOT)) {
            case "mute" -> handleMute(sender, args);
            case "tempmute" -> handleTempMute(sender, args);
            case "unmute" -> handleUnmute(sender, args);
            default -> false;
        };
    }

    private boolean handleMute(CommandSender sender, String[] args) {
        if (!checkPermission(sender, "spigotessentials.mute", MessageConfig.NO_PERMISSION_SELF)) {
            return true;
        }

        if (args.length < 1) {
            sendMessage(sender, MessageConfig.MUTE_USAGE);
            return true;
        }

        OfflinePlayer target = resolveTarget(sender, args[0]);
        if (target == null) {
            return true;
        }

        String targetName = getTargetName(target, args[0]);
        String reason = joinReason(args, 1);

        plugin.getModerationManager().mute(target.getUniqueId(), targetName, reason, sender.getName());
        sendMessage(sender, MessageConfig.MUTE_SET, targetName, reason);
        if (target.isOnline() && target.getPlayer() != null) {
            sendMessage(target.getPlayer(), MessageConfig.MUTE_TARGET, reason);
        }
        return true;
    }

    private boolean handleTempMute(CommandSender sender, String[] args) {
        if (!checkPermission(sender, "spigotessentials.tempmute", MessageConfig.NO_PERMISSION_SELF)) {
            return true;
        }

        if (args.length < 2) {
            sendMessage(sender, MessageConfig.TEMPMUTE_USAGE);
            return true;
        }

        long durationMillis = parseDurationMillis(args[1]);
        if (durationMillis <= 0L) {
            sendMessage(sender, MessageConfig.DURATION_INVALID);
            return true;
        }

        OfflinePlayer target = resolveTarget(sender, args[0]);
        if (target == null) {
            return true;
        }

        String targetName = getTargetName(target, args[0]);
        String durationText = ModerationManager.formatDuration(durationMillis);
        String reason = joinReason(args, 2);

        plugin.getModerationManager().tempMute(target.getUniqueId(), targetName, reason, sender.getName(), durationMillis);
        sendMessage(sender, MessageConfig.TEMPMUTE_SET, targetName, durationText, reason);
        if (target.isOnline() && target.getPlayer() != null) {
            sendMessage(target.getPlayer(), MessageConfig.TEMPMUTE_TARGET, durationText, reason);
        }
        return true;
    }

    private boolean handleUnmute(CommandSender sender, String[] args) {
        if (!checkPermission(sender, "spigotessentials.unmute", MessageConfig.NO_PERMISSION_SELF)) {
            return true;
        }

        if (args.length != 1) {
            sendMessage(sender, MessageConfig.UNMUTE_USAGE);
            return true;
        }

        OfflinePlayer target = resolveTarget(sender, args[0]);
        if (target == null) {
            return true;
        }

        String targetName = getTargetName(target, args[0]);
        ModerationManager.MuteRecord removedMute = plugin.getModerationManager().unmute(target.getUniqueId());
        if (removedMute == null) {
            sendMessage(sender, MessageConfig.UNMUTE_NOT_MUTED, targetName);
            return true;
        }

        sendMessage(sender, MessageConfig.UNMUTE_SUCCESS, targetName);
        if (target.isOnline() && target.getPlayer() != null) {
            sendMessage(target.getPlayer(), MessageConfig.UNMUTE_TARGET);
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

    private String joinReason(String[] args, int startIndex) {
        if (args.length <= startIndex) {
            return MessageConfig.DEFAULT_REASON;
        }

        return String.join(" ", Arrays.copyOfRange(args, startIndex, args.length));
    }

    private long parseDurationMillis(String rawDuration) {
        Matcher matcher = DURATION_PATTERN.matcher(rawDuration);
        if (!matcher.matches()) {
            return -1L;
        }

        long amount;
        try {
            amount = Long.parseLong(matcher.group(1));
        } catch (NumberFormatException exception) {
            return -1L;
        }

        if (amount <= 0L) {
            return -1L;
        }

        long multiplier = switch (matcher.group(2).toLowerCase(Locale.ROOT)) {
            case "s" -> TimeUnit.SECONDS.toMillis(1L);
            case "m" -> TimeUnit.MINUTES.toMillis(1L);
            case "h" -> TimeUnit.HOURS.toMillis(1L);
            case "d" -> TimeUnit.DAYS.toMillis(1L);
            case "w" -> TimeUnit.DAYS.toMillis(7L);
            default -> -1L;
        };

        if (multiplier <= 0L) {
            return -1L;
        }

        try {
            return Math.multiplyExact(amount, multiplier);
        } catch (ArithmeticException exception) {
            return -1L;
        }
    }
}
