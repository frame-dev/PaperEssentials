package ch.framedev.spigotEssentials.commands;

import ch.framedev.spigotEssentials.PaperEssentials;
import ch.framedev.spigotEssentials.utils.MessageConfig;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Command to send one-off staff messages or toggle staff chat mode.
 */
public class StaffChatCommand extends AbstractCommand {
    private final PaperEssentials plugin;

    public StaffChatCommand(PaperEssentials plugin) {
        this.plugin = plugin;
    }

    @Override
    protected boolean execute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!checkPermission(sender, "spigotessentials.staffchat", MessageConfig.NO_PERMISSION_SELF)) {
            return true;
        }

        if (args.length == 0) {
            Player player = asPlayer(sender);
            if (player == null) {
                sendMessage(sender, MessageConfig.INVALID_USAGE, "/staffchat <message>");
                return true;
            }

            boolean enabled = plugin.getModerationManager().toggleStaffChat(player.getUniqueId());
            sendMessage(sender, enabled ? MessageConfig.STAFFCHAT_TOGGLE_ON : MessageConfig.STAFFCHAT_TOGGLE_OFF);
            return true;
        }

        plugin.getModerationManager().sendStaffChat(sender.getName(), String.join(" ", args));
        return true;
    }
}
