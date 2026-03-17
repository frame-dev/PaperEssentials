package ch.framedev.spigotEssentials.commands;

import ch.framedev.spigotEssentials.utils.MessageConfig;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Command to open virtual ender chest
 */
public class EnderChestCommand extends AbstractCommand {

    @Override
    protected boolean execute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        Player player = asPlayer(sender);
        if (player == null) {
            sendMessage(sender, MessageConfig.PLAYER_ONLY);
            return true;
        }

        if (args.length == 0) {
            if (!checkPermission(sender, "spigotessentials.enderchest.self", MessageConfig.NO_PERMISSION_SELF)) {
                return true;
            }
            player.openInventory(player.getEnderChest());
            sendMessage(player, MessageConfig.ENDERCHEST_OPENED);
            return true;
        } else if (args.length == 1) {
            if (!checkPermission(sender, "spigotessentials.enderchest.others", MessageConfig.NO_PERMISSION_OTHERS)) {
                return true;
            }
            Player target = getPlayer(sender, args[0]);
            if (target == null) {
                return true;
            }
            player.openInventory(target.getEnderChest());
            sendMessage(player, MessageConfig.ENDERCHEST_OPENED_OTHER, target.getName());
            return true;
        } else {
            sendMessage(sender, MessageConfig.INVALID_USAGE, "/enderchest [player]");
            return false;
        }
    }
}
