package ch.framedev.spigotEssentials.commands;

import ch.framedev.spigotEssentials.utils.MessageConfig;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Abstract base class for commands to reduce code duplication
 */
public abstract class AbstractCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        return execute(sender, command, label, args);
    }

    /**
     * Execute the command
     * @param sender The command sender
     * @param command The command
     * @param label The command label
     * @param args The command arguments
     * @return true if the command was executed successfully
     */
    protected abstract boolean execute(CommandSender sender, Command command, String label, String[] args);

    /**
     * Check if the sender is a player
     * @param sender The command sender
     * @return true if the sender is a player
     */
    protected boolean isPlayer(CommandSender sender) {
        return sender instanceof Player;
    }

    /**
     * Get the sender as a player
     * @param sender The command sender
     * @return The player, or null if the sender is not a player
     */
    protected Player asPlayer(CommandSender sender) {
        return sender instanceof Player ? (Player) sender : null;
    }

    /**
     * Check if the sender has permission
     * @param sender The command sender
     * @param permission The permission node
     * @return true if the sender has permission
     */
    protected boolean hasPermission(CommandSender sender, String permission) {
        return sender.hasPermission(permission);
    }

    /**
     * Check if the sender has permission, and send a message if not
     * @param sender The command sender
     * @param permission The permission node
     * @param message The message to send if the sender does not have permission
     * @return true if the sender has permission
     */
    protected boolean checkPermission(CommandSender sender, String permission, String message) {
        if (!hasPermission(sender, permission)) {
            MessageConfig.send(sender, message);
            return false;
        }
        return true;
    }

    /**
     * Get a player by name
     * @param sender The command sender (for sending error messages)
     * @param name The player name
     * @return The player, or null if not found
     */
    protected Player getPlayer(CommandSender sender, String name) {
        Player player = sender.getServer().getPlayerExact(name);
        if (player == null) {
            MessageConfig.send(sender, MessageConfig.PLAYER_NOT_FOUND);
        }
        return player;
    }

    /**
     * Send a message to the sender
     * @param sender The command sender
     * @param message The message to send
     * @param args Arguments to format the message with
     */
    protected void sendMessage(CommandSender sender, String message, Object... args) {
        MessageConfig.send(sender, message, args);
    }
}
