package ch.framedev.spigotEssentials.commands;

import ch.framedev.spigotEssentials.utils.MessageConfig;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Command to view another player's inventory
 */
public class InvseeCommand extends AbstractCommand {

    @Override
    protected boolean execute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        Player player = asPlayer(sender);
        if (player == null) {
            sendMessage(sender, MessageConfig.PLAYER_ONLY);
            return true;
        }

        if (!checkPermission(sender, "spigotessentials.invsee", MessageConfig.NO_PERMISSION_SELF)) {
            return true;
        }

        if (args.length != 1) {
            sendMessage(sender, MessageConfig.INVSEE_USAGE);
            return false;
        }

        Player target = getPlayer(sender, args[0]);
        if (target == null) {
            return true;
        }

        player.openInventory(target.getInventory());
        sendMessage(player, MessageConfig.INVSEE_OPENED, target.getName());
        return true;
    }
}
