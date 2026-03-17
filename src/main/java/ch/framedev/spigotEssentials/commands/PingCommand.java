package ch.framedev.spigotEssentials.commands;

import ch.framedev.spigotEssentials.utils.MessageConfig;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Command to check player latency/ping
 */
public class PingCommand extends AbstractCommand {

    @Override
    protected boolean execute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (args.length == 0) {
            Player player = asPlayer(sender);
            if (player == null) {
                sendMessage(sender, MessageConfig.PLAYER_ONLY);
                return true;
            }
            if (!checkPermission(sender, "spigotessentials.ping.self", MessageConfig.NO_PERMISSION_SELF)) {
                return true;
            }
            int ping = player.getPing();
            sendMessage(player, MessageConfig.PING_SELF, String.valueOf(ping));
            return true;
        } else if (args.length == 1) {
            if (!checkPermission(sender, "spigotessentials.ping.others", MessageConfig.NO_PERMISSION_OTHERS)) {
                return true;
            }
            Player target = getPlayer(sender, args[0]);
            if (target == null) {
                return true;
            }
            int ping = target.getPing();
            sendMessage(sender, MessageConfig.PING_OTHER, target.getName(), String.valueOf(ping));
            return true;
        } else {
            sendMessage(sender, MessageConfig.INVALID_USAGE, "/ping [player]");
            return false;
        }
    }
}
