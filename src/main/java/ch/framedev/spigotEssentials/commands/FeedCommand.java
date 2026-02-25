package ch.framedev.spigotEssentials.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class FeedCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (args.length == 0) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage("This command can only be used by players.");
                return true;
            }
            if (!sender.hasPermission("spigotessentials.feed.self")) {
                sender.sendMessage("You do not have permission to feed yourself.");
                return true;
            }
            feedPlayer(player, player);
            return true;
        } else if (args.length == 1) {
            if (!sender.hasPermission("spigotessentials.feed.others")) {
                sender.sendMessage("You do not have permission to feed others.");
                return true;
            }
            Player target = sender.getServer().getPlayerExact(args[0]);
            if (target == null) {
                sender.sendMessage("Player not found.");
                return true;
            }
            feedPlayer(sender, target);
            return true;
        } else {
            sender.sendMessage("Usage: /feed [player]");
            return false;
        }
    }

    private void feedPlayer(CommandSender sender, Player target) {
        target.setFoodLevel(20);
        target.setSaturation(20);

        if (sender.equals(target)) {
            target.sendMessage("§aYou have been fed.");
        } else {
            sender.sendMessage("§aFed " + target.getName() + ".");
            target.sendMessage("§aYou have been fed.");
        }
    }
}
