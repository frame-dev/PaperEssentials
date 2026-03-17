package ch.framedev.spigotEssentials.commands;

import ch.framedev.spigotEssentials.utils.MessageConfig;
import org.bukkit.WeatherType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Command to set personal weather for players
 */
public class PlayerWeatherCommand extends AbstractCommand {

    @Override
    protected boolean execute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (args.length == 0) {
            sendMessage(sender, MessageConfig.INVALID_USAGE, "/playerweather <clear|rain|reset> [player]");
            return false;
        }

        Player player = asPlayer(sender);
        if (player == null) {
            sendMessage(sender, MessageConfig.PLAYER_ONLY);
            return true;
        }

        if (args.length == 1) {
            if (!checkPermission(sender, "spigotessentials.playerweather.self", MessageConfig.NO_PERMISSION_SELF)) {
                return true;
            }
            return setWeather(sender, player, args[0]);
        } else if (args.length == 2) {
            if (!checkPermission(sender, "spigotessentials.playerweather.others", MessageConfig.NO_PERMISSION_OTHERS)) {
                return true;
            }
            Player target = getPlayer(sender, args[1]);
            if (target == null) {
                return true;
            }
            return setWeather(sender, target, args[0]);
        } else {
            sendMessage(sender, MessageConfig.INVALID_USAGE, "/playerweather <clear|rain|reset> [player]");
            return false;
        }
    }

    private boolean setWeather(CommandSender sender, Player target, String weatherArg) {
        String weather = weatherArg.toLowerCase();

        if (weather.equals("reset")) {
            target.resetPlayerWeather();
            if (sender.equals(target)) {
                sendMessage(sender, MessageConfig.PWEATHER_RESET);
            } else {
                sendMessage(sender, MessageConfig.PWEATHER_RESET_OTHER, target.getName());
            }
            return true;
        }

        WeatherType weatherType = parseWeather(weather);
        if (weatherType == null) {
            sendMessage(sender, MessageConfig.PWEATHER_INVALID);
            return false;
        }

        target.setPlayerWeather(weatherType);
        String weatherName = weatherType == WeatherType.CLEAR ? "clear" : "rain";

        if (sender.equals(target)) {
            sendMessage(sender, MessageConfig.PWEATHER_SET, weatherName);
        } else {
            sendMessage(sender, MessageConfig.PWEATHER_SET_OTHER, target.getName(), weatherName);
        }
        return true;
    }

    private WeatherType parseWeather(String weather) {
        return switch (weather) {
            case "clear", "sun", "sunny" -> WeatherType.CLEAR;
            case "rain", "storm", "downfall" -> WeatherType.DOWNFALL;
            default -> null;
        };
    }
}
