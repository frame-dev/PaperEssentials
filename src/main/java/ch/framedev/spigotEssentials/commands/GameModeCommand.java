package ch.framedev.spigotEssentials.commands;

import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Stream;

public class GameModeCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (args.length == 1) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage("This command can only be used by players.");
                return true;
            }
            if (!sender.hasPermission("spigotessentials.gamemode.self")) {
                sender.sendMessage("You do not have permission to change your own game mode.");
                return true;
            }
            return setGameMode(player, args[0], player);
        } else if (args.length == 2) {
            if (!sender.hasPermission("spigotessentials.gamemode.others")) {
                sender.sendMessage("You do not have permission to change others' game modes.");
                return true;
            }
            Player target = sender.getServer().getPlayerExact(args[1]);
            if (target == null) {
                sender.sendMessage("Player not found.");
                return true;
            }
            boolean result = setGameMode(sender, args[0], target);
            if (result && target != sender) {
                target.sendMessage("§aYour game mode has been changed to " + parseGameMode(args[0]).name() + ".");
            }
            return result;
        } else {
            sender.sendMessage("Usage: /gamemode <mode> [player]");
            return false;
        }
    }

    private boolean setGameMode(CommandSender sender, String modeArg, Player target) {
        GameMode mode = parseGameMode(modeArg);
        if (mode == null) {
            sender.sendMessage("Invalid game mode. Use survival, creative, adventure, or spectator.");
            return false;
        }

        target.setGameMode(mode);
        if (sender.equals(target)) {
            sender.sendMessage("§aYour game mode has been changed to " + mode.name() + ".");
        } else {
            sender.sendMessage("§a" + target.getName() + "'s game mode has been changed to " + mode.name() + ".");
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
