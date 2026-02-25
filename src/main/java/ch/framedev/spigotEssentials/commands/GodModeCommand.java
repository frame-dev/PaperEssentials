package ch.framedev.spigotEssentials.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class GodModeCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (args.length == 0) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage("Only players can toggle their own god mode.");
                return true;
            }
            if (!sender.hasPermission("spigotessentials.godmode.self")) {
                sender.sendMessage("You do not have permission to toggle your own god mode.");
                return true;
            }
            toggleGodMode(player, player);
            return true;
        } else if (args.length == 1) {
            if (!sender.hasPermission("spigotessentials.godmode.others")) {
                sender.sendMessage("You do not have permission to toggle god mode for others.");
                return true;
            }
            Player target = sender.getServer().getPlayerExact(args[0]);
            if (target == null) {
                sender.sendMessage("Player not found.");
                return true;
            }
            toggleGodMode(sender, target);
            return true;
        } else {
            sender.sendMessage("Usage: /god [player]");
            return false;
        }
    }

    private void toggleGodMode(CommandSender sender, Player target) {
        boolean newState = !target.isInvulnerable();
        target.setInvulnerable(newState);
        String status = newState ? "enabled" : "disabled";

        if (sender.equals(target)) {
            target.sendMessage("§aGod mode " + status + ".");
        } else {
            sender.sendMessage("§aGod mode for " + target.getName() + " has been " + status + ".");
            target.sendMessage("§aYour god mode has been " + status + ".");
        }
    }
}
