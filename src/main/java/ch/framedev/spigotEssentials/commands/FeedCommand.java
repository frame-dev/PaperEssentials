package ch.framedev.spigotEssentials.commands;

import ch.framedev.spigotEssentials.utils.MessageConfig;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class FeedCommand extends AbstractCommand {

    @Override
    protected boolean execute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (args.length == 0) {
            Player player = asPlayer(sender);
            if (player == null) {
                sendMessage(sender, MessageConfig.PLAYER_ONLY);
                return true;
            }
            if (!checkPermission(sender, "spigotessentials.feed.self", MessageConfig.NO_PERMISSION_SELF)) {
                return true;
            }
            feedPlayer(player, player);
            return true;
        } else if (args.length == 1) {
            if (!checkPermission(sender, "spigotessentials.feed.others", MessageConfig.NO_PERMISSION_OTHERS)) {
                return true;
            }
            Player target = getPlayer(sender, args[0]);
            if (target == null) {
                return true;
            }
            feedPlayer(sender, target);
            return true;
        } else {
            sendMessage(sender, MessageConfig.FEED_USAGE);
            return false;
        }
    }

    private void feedPlayer(CommandSender sender, Player target) {
        target.setFoodLevel(20);
        target.setSaturation(20);

        if (sender.equals(target)) {
            sendMessage(target, MessageConfig.FEED_SELF);
        } else {
            sendMessage(sender, MessageConfig.FEED_OTHER, target.getName());
            sendMessage(target, MessageConfig.FEED_TARGET, sender.getName());
        }
    }
}
