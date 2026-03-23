package ch.framedev.spigotEssentials.commands;

import ch.framedev.spigotEssentials.utils.MessageConfig;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Command to clear chat for all online players
 */
public class ChatClearCommand extends AbstractCommand {
    private static final int CHAT_CLEAR_LINES = 100;

    @Override
    protected boolean execute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (!checkPermission(sender, "spigotessentials.chatclear", MessageConfig.NO_PERMISSION_SELF)) {
            return true;
        }

        if (args.length != 0) {
            sendMessage(sender, MessageConfig.CHATCLEAR_USAGE);
            return true;
        }

        clearChatForOnlinePlayers();
        MessageConfig.broadcast(MessageConfig.CHATCLEAR_BROADCAST, sender.getName());
        return true;
    }

    private void clearChatForOnlinePlayers() {
        Component emptyLine = Component.empty();
        for (Player player : Bukkit.getOnlinePlayers()) {
            for (int i = 0; i < CHAT_CLEAR_LINES; i++) {
                player.sendMessage(emptyLine);
            }
        }
    }
}
