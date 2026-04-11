package ch.framedev.spigotEssentials.commands;

import ch.framedev.spigotEssentials.utils.MessageConfig;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Command to clear player inventories
 */
public class ClearCommand extends AbstractCommand {

    @Override
    protected boolean execute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (args.length == 0) {
            Player player = asPlayer(sender);
            if (player == null) {
                sendMessage(sender, MessageConfig.PLAYER_ONLY);
                return true;
            }
            if (!checkPermission(sender, "spigotessentials.clear.self", MessageConfig.NO_PERMISSION_SELF)) {
                return true;
            }
            player.getInventory().clear();
            sendMessage(player, MessageConfig.CLEAR_SELF);
            return true;
        } else if (args.length == 1) {
            if (!checkPermission(sender, "spigotessentials.clear.others", MessageConfig.NO_PERMISSION_OTHERS)) {
                return true;
            }
            Player target = getPlayer(sender, args[0]);
            if (target == null) {
                return true;
            }
            target.getInventory().clear();
            sendMessage(sender, MessageConfig.CLEAR_OTHER, target.getName());
            if (!sender.equals(target)) {
                sendMessage(target, MessageConfig.CLEAR_TARGET, sender.getName());
            }
            return true;
        } else {
            sendMessage(sender, MessageConfig.CLEAR_USAGE);
            return false;
        }
    }
}
