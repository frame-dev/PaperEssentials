package ch.framedev.spigotEssentials.commands;

import ch.framedev.spigotEssentials.utils.MessageConfig;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Stream;

/**
 * Command handler for game mode commands
 */
public class GameModeCommand extends AbstractCommand implements TabCompleter {

    @Override
    protected boolean execute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (args.length == 1) {
            Player player = asPlayer(sender);
            if (player == null) {
                sendMessage(sender, MessageConfig.PLAYER_ONLY);
                return true;
            }
            if (!checkPermission(sender, "spigotessentials.gamemode.self", MessageConfig.NO_PERMISSION_SELF)) {
                return true;
            }
            return setGameMode(player, args[0], player);
        } else if (args.length == 2) {
            if (!checkPermission(sender, "spigotessentials.gamemode.others", MessageConfig.NO_PERMISSION_OTHERS)) {
                return true;
            }
            Player target = getPlayer(sender, args[1]);
            if (target == null) {
                return true;
            }
            boolean result = setGameMode(sender, args[0], target);
            if (result && target != sender) {
                GameMode mode = parseGameMode(args[0]);
                if (mode != null) {
                    sendMessage(target, MessageConfig.GAMEMODE_CHANGED_TARGET, mode.name());
                }
            }
            return result;
        } else {
            sendMessage(sender, MessageConfig.GAMEMODE_USAGE);
            return false;
        }
    }

    private boolean setGameMode(CommandSender sender, String modeArg, Player target) {
        GameMode mode = parseGameMode(modeArg);
        if (mode == null) {
            sendMessage(sender, MessageConfig.GAMEMODE_INVALID);
            return false;
        }

        target.setGameMode(mode);
        if (sender.equals(target)) {
            sendMessage(sender, MessageConfig.GAMEMODE_CHANGED_SELF, mode.name());
        } else {
            sendMessage(sender, MessageConfig.GAMEMODE_CHANGED_OTHER, target.getName(), mode.name());
        }
        return true;
    }

    private GameMode parseGameMode(String arg) {
        return switch (arg.toLowerCase()) {
            case "survival", "0" -> GameMode.SURVIVAL;
            case "creative", "1" -> GameMode.CREATIVE;
            case "adventure", "2" -> GameMode.ADVENTURE;
            case "spectator", "3" -> GameMode.SPECTATOR;
            default -> null;
        };
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if(!command.getName().equalsIgnoreCase("gamemode")) {
            return null;
        }
        if (args.length == 1) {
            String prefix = args[0];
            var modes = Stream.of("survival", "creative", "adventure", "spectator", "0", "1", "2", "3");
            if (prefix.isEmpty()) {
                return modes.toList();
            }
            return modes
                    .filter(mode -> mode.toLowerCase().startsWith(prefix.toLowerCase()))
                    .toList();
        } else if (args.length == 2) {
            String prefix = args[1];
            if (prefix.isEmpty()) {
                return sender.getServer().getOnlinePlayers().stream()
                        .map(org.bukkit.entity.Player::getName)
                        .toList();
            }
            return sender.getServer().getOnlinePlayers().stream()
                    .map(org.bukkit.entity.Player::getName)
                    .filter(name -> name.toLowerCase().startsWith(prefix.toLowerCase()))
                    .toList();
        }
        return List.of();
    }
}
