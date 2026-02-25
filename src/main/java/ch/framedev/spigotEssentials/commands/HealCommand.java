package ch.framedev.spigotEssentials.commands;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HealCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (args.length == 0) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage("This command can only be used by players.");
                return true;
            }
            if (!sender.hasPermission("spigotessentials.heal.self")) {
                sender.sendMessage("You do not have permission to heal yourself.");
                return true;
            }
            return healPlayer(player, player);
        } else if (args.length == 1) {
            if (!sender.hasPermission("spigotessentials.heal.others")) {
                sender.sendMessage("You do not have permission to heal others.");
                return true;
            }
            Player target = sender.getServer().getPlayerExact(args[0]);
            if (target == null) {
                sender.sendMessage("Player not found.");
                return true;
            }
            return healPlayer(sender, target);
        } else {
            sender.sendMessage("Usage: /heal [player]");
            return false;
        }
    }

    private boolean healPlayer(CommandSender sender, Player target) {
        @Nullable AttributeInstance maxHealth = target.getAttribute(Attribute.MAX_HEALTH);
        if (maxHealth == null) {
            sender.sendMessage("Could not retrieve max health attribute.");
            return true;
        }

        target.setHealth(maxHealth.getBaseValue());

        if (sender.equals(target)) {
            target.sendMessage("§aYou have been healed.");
        } else {
            sender.sendMessage("§aHealed " + target.getName() + ".");
            target.sendMessage("§aYou have been healed.");
        }
        return true;
    }
}