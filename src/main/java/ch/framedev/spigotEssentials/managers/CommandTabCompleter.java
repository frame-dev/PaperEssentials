package ch.framedev.spigotEssentials.managers;

import ch.framedev.spigotEssentials.LocationManager;
import ch.framedev.spigotEssentials.PaperEssentials;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Shared tab completer for commands with straightforward argument suggestions.
 */
public class CommandTabCompleter implements TabCompleter {
    private static final List<String> SPEED_VALUES = IntStream.rangeClosed(0, 10)
            .mapToObj(Integer::toString)
            .toList();
    private static final List<String> SPEED_FIRST_ARGUMENTS = Stream.concat(Stream.of("walk", "fly"), SPEED_VALUES.stream())
            .toList();
    private static final List<String> PLAYER_WEATHER_OPTIONS = List.of("clear", "rain", "reset", "sun", "sunny", "storm", "downfall");
    private static final List<String> PLAYER_TIME_OPTIONS = List.of(
            "day", "night", "noon", "midnight", "sunrise", "sunset", "morning", "midday", "evening",
            "0", "1000", "6000", "12000", "13000", "18000", "23000", "reset"
    );
    private static final List<String> TOGGLE_OPTIONS = List.of("on", "off");
    private static final List<String> REPAIR_OPTIONS = List.of("all");
    private static final List<String> TEMPMUTE_DURATIONS = List.of("10m", "30m", "1h", "6h", "1d", "1w");

    private final PaperEssentials plugin;

    public CommandTabCompleter(PaperEssentials plugin) {
        this.plugin = plugin;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        String commandName = command.getName().toLowerCase(Locale.ROOT);

        return switch (commandName) {
            case "fly", "heal", "feed", "god", "clear", "ping", "enderchest", "backpack" ->
                    args.length == 1 ? onlinePlayers(args[0]) : List.of();

            case "invsee", "freeze", "tpa", "tpahere", "msg", "ignore" ->
                    args.length == 1 ? onlinePlayers(args[0]) : List.of();

            case "mute", "unmute", "warn", "warnings", "muteinfo", "clearwarnings" ->
                    args.length == 1 ? onlinePlayers(args[0]) : List.of();

            case "tempmute" -> {
                if (args.length == 1) {
                    yield onlinePlayers(args[0]);
                }
                if (args.length == 2) {
                    yield matching(TEMPMUTE_DURATIONS, args[1]);
                }
                yield List.of();
            }

            case "speed" -> {
                if (args.length == 1) {
                    yield matching(SPEED_FIRST_ARGUMENTS, args[0]);
                }
                if (args.length == 2 && isSpeedType(args[0])) {
                    yield matching(SPEED_VALUES, args[1]);
                }
                if (args.length == 3) {
                    yield onlinePlayers(args[2]);
                }
                yield List.of();
            }

            case "playertime" -> {
                if (args.length == 1) {
                    yield matching(PLAYER_TIME_OPTIONS, args[0]);
                }
                if (args.length == 2) {
                    yield onlinePlayers(args[1]);
                }
                yield List.of();
            }

            case "playerweather" -> {
                if (args.length == 1) {
                    yield matching(PLAYER_WEATHER_OPTIONS, args[0]);
                }
                if (args.length == 2) {
                    yield onlinePlayers(args[1]);
                }
                yield List.of();
            }

            case "day", "night", "sun", "rain", "thunder" ->
                    args.length == 1 ? worldNames(args[0]) : List.of();

            case "repair" ->
                    args.length == 1 ? matching(REPAIR_OPTIONS, args[0]) : List.of();

            case "chatmute" ->
                    args.length == 1 ? matching(TOGGLE_OPTIONS, args[0]) : List.of();

            case "sethome", "home", "delhome" ->
                    args.length == 1 ? playerHomeNames(sender, args[0]) : List.of();

            default -> List.of();
        };
    }

    private boolean isSpeedType(String input) {
        String lowered = input.toLowerCase(Locale.ROOT);
        return lowered.equals("walk") || lowered.equals("fly");
    }

    private List<String> onlinePlayers(String prefix) {
        return plugin.getServer().getOnlinePlayers().stream()
                .map(Player::getName)
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .filter(name -> startsWithIgnoreCase(name, prefix))
                .toList();
    }

    private List<String> worldNames(String prefix) {
        return plugin.getServer().getWorlds().stream()
                .map(World::getName)
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .filter(name -> startsWithIgnoreCase(name, prefix))
                .toList();
    }

    private List<String> playerHomeNames(CommandSender sender, String prefix) {
        if (!(sender instanceof Player player)) {
            return List.of();
        }

        return matching(LocationManager.getLocationNames(player.getName()), prefix);
    }

    private List<String> matching(Collection<String> values, String prefix) {
        return values.stream()
                .filter(value -> startsWithIgnoreCase(value, prefix))
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .toList();
    }

    private boolean startsWithIgnoreCase(String value, String prefix) {
        return value.toLowerCase(Locale.ROOT).startsWith(prefix.toLowerCase(Locale.ROOT));
    }
}
