package ch.framedev.spigotEssentials.commands;

import ch.framedev.spigotEssentials.utils.MessageConfig;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 * Command to broadcast messages to all players
 */
public class BroadcastCommand extends AbstractCommand {

    @Override
    protected boolean execute(CommandSender sender, Command command, String label, String[] args) {
        if (!checkPermission(sender, "spigotessentials.broadcast", MessageConfig.NO_PERMISSION_SELF)) {
            return true;
        }

        if (args.length == 0) {
            sendMessage(sender, MessageConfig.BROADCAST_USAGE);
            return true;
        }

        String message = String.join(" ", args);
        String formattedMessage = MessageConfig.BROADCAST_FORMAT.replace("%message%", message);

        sender.getServer().broadcastMessage(formattedMessage);

        return true;
    }
}
