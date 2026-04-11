package ch.framedev.spigotEssentials.commands;

import ch.framedev.spigotEssentials.PaperEssentials;
import ch.framedev.spigotEssentials.utils.MessageConfig;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

/**
 * Command to set world weather to thunder
 */
public class ThunderCommand extends AbstractCommand {

    @Override
    protected boolean execute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
            @NotNull String @NotNull [] args) {
        if (!checkPermission(sender, "spigotessentials.weather", MessageConfig.NO_PERMISSION_SELF)) {
            return true;
        }

        World world;
        if (sender instanceof Player player) {
            world = player.getWorld();
        } else {
            if (args.length == 0) {
                sendMessage(sender, MessageConfig.WORLD_COMMAND_CONSOLE_USAGE, "thunder");
                return false;
            }
            world = sender.getServer().getWorld(args[0]);
            if (world == null) {
                sendMessage(sender, MessageConfig.WORLD_NOT_FOUND, args[0]);
                return true;
            }
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                world.setStorm(true);
                world.setThundering(true);
                world.setWeatherDuration(6000); // 5 minutes
                sendMessage(sender, MessageConfig.WEATHER_SET_THUNDER);
            }
        }.runTaskLater(PaperEssentials.getInstance(), 60L); // Delay to ensure weather change is applied
                                                            // after command execution
        return true;
    }
}
