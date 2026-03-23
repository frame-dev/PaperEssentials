package ch.framedev.spigotEssentials.commands;

import ch.framedev.spigotEssentials.PaperEssentials;
import ch.framedev.spigotEssentials.utils.MessageConfig;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

/**
 * Command to toggle or explicitly set the global chat mute state.
 */
public class ChatMuteCommand extends AbstractCommand {
    private final PaperEssentials plugin;

    public ChatMuteCommand(PaperEssentials plugin) {
        this.plugin = plugin;
    }

    @Override
    protected boolean execute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!checkPermission(sender, "spigotessentials.chatmute", MessageConfig.NO_PERMISSION_SELF)) {
            return true;
        }

        boolean currentState = plugin.getModerationManager().isGlobalChatMuted();
        boolean targetState;

        if (args.length == 0) {
            targetState = !currentState;
        } else if (args.length == 1) {
            targetState = switch (args[0].toLowerCase(Locale.ROOT)) {
                case "on" -> true;
                case "off" -> false;
                default -> {
                    sendMessage(sender, MessageConfig.CHATMUTE_USAGE);
                    yield currentState;
                }
            };
        } else {
            sendMessage(sender, MessageConfig.CHATMUTE_USAGE);
            return true;
        }

        if (args.length == 1 && !args[0].equalsIgnoreCase("on") && !args[0].equalsIgnoreCase("off")) {
            return true;
        }

        if (!plugin.getModerationManager().setGlobalChatMuted(targetState, sender.getName())) {
            sendMessage(sender, targetState ? MessageConfig.CHATMUTE_ALREADY_ENABLED : MessageConfig.CHATMUTE_ALREADY_DISABLED);
            return true;
        }

        MessageConfig.broadcast(targetState ? MessageConfig.CHATMUTE_ENABLED : MessageConfig.CHATMUTE_DISABLED, sender.getName());
        return true;
    }
}
