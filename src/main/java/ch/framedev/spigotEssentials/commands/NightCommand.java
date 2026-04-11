package ch.framedev.spigotEssentials.commands;

import ch.framedev.spigotEssentials.utils.MessageConfig;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Command to set world time to night
 */
public class NightCommand extends AbstractCommand {

    @Override
    protected boolean execute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (!checkPermission(sender, "spigotessentials.time", MessageConfig.NO_PERMISSION_SELF)) {
            return true;
        }

        World world;
        if (sender instanceof Player player) {
            world = player.getWorld();
        } else {
            if (args.length == 0) {
                sendMessage(sender, MessageConfig.WORLD_COMMAND_CONSOLE_USAGE, "night");
                return false;
            }
            world = sender.getServer().getWorld(args[0]);
            if (world == null) {
                sendMessage(sender, MessageConfig.WORLD_NOT_FOUND, args[0]);
                return true;
            }
        }

        world.setTime(13000L); // Set to night
        sendMessage(sender, MessageConfig.TIME_SET_NIGHT);
        return true;
    }
}
