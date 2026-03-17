package ch.framedev.spigotEssentials.commands;

import ch.framedev.spigotEssentials.utils.MessageConfig;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Command to set world weather to rain
 */
public class RainCommand extends AbstractCommand {

    @Override
    protected boolean execute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (!checkPermission(sender, "spigotessentials.weather", MessageConfig.NO_PERMISSION_SELF)) {
            return true;
        }

        World world;
        if (sender instanceof Player player) {
            world = player.getWorld();
        } else {
            if (args.length == 0) {
                sendMessage(sender, "§cUsage from console: /rain <world>");
                return false;
            }
            world = sender.getServer().getWorld(args[0]);
            if (world == null) {
                sendMessage(sender, "§cWorld not found: " + args[0]);
                return true;
            }
        }

        world.setStorm(true);
        world.setThundering(false);
        world.setWeatherDuration(6000); // 5 minutes
        sendMessage(sender, MessageConfig.WEATHER_SET_RAIN);
        return true;
    }
}
