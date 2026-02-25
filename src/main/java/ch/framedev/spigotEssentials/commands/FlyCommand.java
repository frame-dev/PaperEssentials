package ch.framedev.spigotEssentials.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class FlyCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (args.length == 0) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage("This command can only be used by players.");
                return true;
            }
            if (!sender.hasPermission("spigotessentials.fly.self")) {
                sender.sendMessage("You do not have permission to toggle your own fly mode.");
                return true;
            }
            toggleFly(player, player);
            return true;
        } else if (args.length == 1) {
            if (!sender.hasPermission("spigotessentials.fly.others")) {
                sender.sendMessage("You do not have permission to toggle others' fly mode.");
                return true;
            }
            Player target = sender.getServer().getPlayerExact(args[0]);
            if (target == null) {
                sender.sendMessage("Player not found.");
                return true;
            }
            toggleFly(sender, target);
            return true;
        } else {
            sender.sendMessage("Usage: /fly [player]");
            return false;
        }
    }

    private void toggleFly(CommandSender sender, Player target) {
        boolean newState = !target.getAllowFlight();
        target.setAllowFlight(newState);
        String status = newState ? "enabled" : "disabled";

        if (sender.equals(target)) {
            target.sendMessage("§aYour fly mode has been " + status + ".");
        } else {
            sender.sendMessage("§a" + target.getName() + "'s fly mode has been " + status + ".");
            target.sendMessage("§aYour fly mode has been " + status + ".");
        }
    }
}
