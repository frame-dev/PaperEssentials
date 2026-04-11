package ch.framedev.spigotEssentials.commands;

import ch.framedev.spigotEssentials.utils.MessageConfig;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Command to set personal time for players
 */
public class PlayerTimeCommand extends AbstractCommand {

    @Override
    protected boolean execute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (args.length == 0) {
            sendMessage(sender, MessageConfig.PTIME_USAGE);
            return false;
        }

        Player player = asPlayer(sender);
        if (player == null) {
            sendMessage(sender, MessageConfig.PLAYER_ONLY);
            return true;
        }

        if (args.length == 1) {
            if (!checkPermission(sender, "spigotessentials.playertime.self", MessageConfig.NO_PERMISSION_SELF)) {
                return true;
            }
            return setTime(sender, player, args[0]);
        } else if (args.length == 2) {
            if (!checkPermission(sender, "spigotessentials.playertime.others", MessageConfig.NO_PERMISSION_OTHERS)) {
                return true;
            }
            Player target = getPlayer(sender, args[1]);
            if (target == null) {
                return true;
            }
            return setTime(sender, target, args[0]);
        } else {
            sendMessage(sender, MessageConfig.PTIME_USAGE);
            return false;
        }
    }

    private boolean setTime(CommandSender sender, Player target, String timeArg) {
        String timeLower = timeArg.toLowerCase();

        if (timeLower.equals("reset")) {
            target.resetPlayerTime();
            if (sender.equals(target)) {
                sendMessage(sender, MessageConfig.PTIME_RESET);
            } else {
                sendMessage(sender, MessageConfig.PTIME_RESET_OTHER, target.getName());
            }
            return true;
        }

        Long time = parseTime(timeLower);
        if (time == null) {
            sendMessage(sender, MessageConfig.PTIME_INVALID);
            return false;
        }

        target.setPlayerTime(time, false);
        String timeName = getTimeName(time);

        if (sender.equals(target)) {
            sendMessage(sender, MessageConfig.PTIME_SET, timeName);
        } else {
            sendMessage(sender, MessageConfig.PTIME_SET_OTHER, target.getName(), timeName);
        }
        return true;
    }

    private Long parseTime(String time) {
        try {
            // Try to parse as number
            long value = Long.parseLong(time);
            if (value >= 0 && value <= 24000) {
                return value;
            }
            return null;
        } catch (NumberFormatException e) {
            // Parse as time name
            return switch (time) {
                case "day", "morning" -> 1000L;
                case "noon", "midday" -> 6000L;
                case "night", "evening" -> 13000L;
                case "midnight" -> 18000L;
                case "sunrise" -> 23000L;
                case "sunset" -> 12000L;
                default -> null;
            };
        }
    }

    private String getTimeName(long time) {
        if (time >= 0 && time < 2000) return "sunrise";
        if (time >= 2000 && time < 9000) return "day";
        if (time >= 9000 && time < 11000) return "noon";
        if (time >= 11000 && time < 13000) return "sunset";
        if (time >= 13000 && time < 22000) return "night";
        if (time >= 22000 && time <= 24000) return "midnight";
        return String.valueOf(time);
    }
}
