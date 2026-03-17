package ch.framedev.spigotEssentials.commands;

import ch.framedev.spigotEssentials.utils.MessageConfig;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class HealCommand extends AbstractCommand {

    @Override
    protected boolean execute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (args.length == 0) {
            Player player = asPlayer(sender);
            if (player == null) {
                sendMessage(sender, MessageConfig.PLAYER_ONLY);
                return true;
            }
            if (!checkPermission(sender, "spigotessentials.heal.self", MessageConfig.NO_PERMISSION_SELF)) {
                return true;
            }
            return healPlayer(player, player);
        } else if (args.length == 1) {
            if (!checkPermission(sender, "spigotessentials.heal.others", MessageConfig.NO_PERMISSION_OTHERS)) {
                return true;
            }
            Player target = getPlayer(sender, args[0]);
            if (target == null) {
                return true;
            }
            return healPlayer(sender, target);
        } else {
            sendMessage(sender, MessageConfig.INVALID_USAGE, "/heal [player]");
            return false;
        }
    }

    private boolean healPlayer(CommandSender sender, Player target) {
        AttributeInstance maxHealth = target.getAttribute(Attribute.MAX_HEALTH);
        if (maxHealth == null) {
            sendMessage(sender, "§cCould not retrieve max health attribute.");
            return true;
        }

        target.setHealth(maxHealth.getBaseValue());

        if (sender.equals(target)) {
            sendMessage(target, MessageConfig.HEAL_SELF);
        } else {
            sendMessage(sender, MessageConfig.HEAL_OTHER, target.getName());
            sendMessage(target, MessageConfig.HEAL_TARGET, sender.getName());
        }
        return true;
    }
}