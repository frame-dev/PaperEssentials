package ch.framedev.spigotEssentials.commands;

import ch.framedev.spigotEssentials.utils.MessageConfig;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Command to open a disposable trash inventory.
 */
public class TrashCommand extends AbstractCommand {
    private static final int TRASH_SIZE = 54;

    @Override
    protected boolean execute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player player = asPlayer(sender);
        if (player == null) {
            sendMessage(sender, MessageConfig.PLAYER_ONLY);
            return true;
        }

        if (!checkPermission(sender, "spigotessentials.trash", MessageConfig.NO_PERMISSION_SELF)) {
            return true;
        }

        if (args.length != 0) {
            sendMessage(sender, MessageConfig.INVALID_USAGE, "/trash");
            return true;
        }

        player.openInventory(Bukkit.createInventory(null, TRASH_SIZE, MessageConfig.component(MessageConfig.TRASH_TITLE)));
        sendMessage(player, MessageConfig.TRASH_OPENED);
        return true;
    }
}
