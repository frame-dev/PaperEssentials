package ch.framedev.spigotEssentials.commands;

import ch.framedev.spigotEssentials.utils.MessageConfig;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Command to adjust player movement speed
 */
public class SpeedCommand extends AbstractCommand {

    @Override
    protected boolean execute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (args.length == 0 || args.length > 3) {
            sendMessage(sender, MessageConfig.SPEED_USAGE);
            return false;
        }

        Player player = asPlayer(sender);
        if (player == null) {
            sendMessage(sender, MessageConfig.PLAYER_ONLY);
            return true;
        }

        if (args.length == 1) {
            // /speed <0-10> - set walk speed
            return setSpeed(sender, player, "walk", args[0]);
        } else if (args.length == 2) {
            // /speed <walk|fly> <0-10>
            return setSpeed(sender, player, args[0], args[1]);
        } else {
            // /speed <walk|fly> <0-10> <player>
            if (!checkPermission(sender, "spigotessentials.speed.others", MessageConfig.NO_PERMISSION_OTHERS)) {
                return true;
            }
            Player target = getPlayer(sender, args[2]);
            if (target == null) {
                return true;
            }
            return setSpeed(sender, target, args[0], args[1]);
        }
    }

    private boolean setSpeed(CommandSender sender, Player target, String type, String speedStr) {
        if (!checkPermission(sender, "spigotessentials.speed", MessageConfig.NO_PERMISSION_SELF)) {
            return true;
        }

        float speed;
        try {
            speed = Float.parseFloat(speedStr);
            if (speed < 0 || speed > 10) {
                sendMessage(sender, MessageConfig.SPEED_INVALID);
                return false;
            }
        } catch (NumberFormatException e) {
            sendMessage(sender, MessageConfig.SPEED_INVALID);
            return false;
        }

        // Convert 0-10 to Minecraft's -1 to 1 scale
        float minecraftSpeed = speed / 10.0f;

        String speedType;
        if (type.equalsIgnoreCase("fly")) {
            target.setFlySpeed(minecraftSpeed);
            speedType = "fly";
        } else if (type.equalsIgnoreCase("walk")) {
            target.setWalkSpeed(minecraftSpeed);
            speedType = "walk";
        } else {
            sendMessage(sender, MessageConfig.SPEED_USAGE);
            return false;
        }

        if (sender.equals(target)) {
            sendMessage(sender, MessageConfig.SPEED_SET, speedType, speedStr);
        } else {
            sendMessage(sender, MessageConfig.SPEED_SET_OTHER, target.getName(), speedType, speedStr);
        }
        return true;
    }
}
