package ch.framedev.spigotEssentials.commands;

import ch.framedev.spigotEssentials.utils.MessageConfig;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class GodModeCommand extends AbstractCommand {

    @Override
    protected boolean execute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (args.length == 0) {
            Player player = asPlayer(sender);
            if (player == null) {
                sendMessage(sender, MessageConfig.PLAYER_ONLY);
                return true;
            }
            if (!checkPermission(sender, "spigotessentials.godmode.self", MessageConfig.NO_PERMISSION_SELF)) {
                return true;
            }
            toggleGodMode(player, player);
            return true;
        } else if (args.length == 1) {
            if (!checkPermission(sender, "spigotessentials.godmode.others", MessageConfig.NO_PERMISSION_OTHERS)) {
                return true;
            }
            Player target = getPlayer(sender, args[0]);
            if (target == null) {
                return true;
            }
            toggleGodMode(sender, target);
            return true;
        } else {
            sendMessage(sender, MessageConfig.GOD_USAGE);
            return false;
        }
    }

    private void toggleGodMode(CommandSender sender, Player target) {
        boolean newState = !target.isInvulnerable();
        target.setInvulnerable(newState);

        if (sender.equals(target)) {
            sendMessage(target, newState ? MessageConfig.GOD_ENABLED_SELF : MessageConfig.GOD_DISABLED_SELF);
        } else {
            sendMessage(sender, newState ? MessageConfig.GOD_ENABLED_OTHER : MessageConfig.GOD_DISABLED_OTHER, target.getName());
            sendMessage(target, newState ? MessageConfig.GOD_ENABLED_TARGET : MessageConfig.GOD_DISABLED_TARGET);
        }
    }
}
