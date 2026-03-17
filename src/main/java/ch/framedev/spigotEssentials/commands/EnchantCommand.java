package ch.framedev.spigotEssentials.commands;

import ch.framedev.spigotEssentials.utils.MessageConfig;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Command to open enchanting table interface
 */
public class EnchantCommand extends AbstractCommand {

    @Override
    protected boolean execute(CommandSender sender, Command command, String label, String[] args) {
        Player player = asPlayer(sender);
        if (player == null) return true;

        if (!checkPermission(sender, "spigotessentials.enchant", MessageConfig.NO_PERMISSION_SELF)) {
            return true;
        }

        player.openEnchanting(null, true);
        sendMessage(sender, MessageConfig.ENCHANT_OPENED);
        return true;
    }
}
