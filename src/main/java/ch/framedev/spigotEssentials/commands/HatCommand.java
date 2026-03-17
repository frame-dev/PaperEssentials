package ch.framedev.spigotEssentials.commands;

import ch.framedev.spigotEssentials.utils.MessageConfig;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;

/**
 * Command to wear items as a hat
 */
public class HatCommand extends AbstractCommand {

    @Override
    protected boolean execute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        Player player = asPlayer(sender);
        if (player == null) {
            sendMessage(sender, MessageConfig.PLAYER_ONLY);
            return true;
        }

        if (!checkPermission(sender, "spigotessentials.hat", MessageConfig.NO_PERMISSION_SELF)) {
            return true;
        }

        PlayerInventory inventory = player.getInventory();
        ItemStack itemInHand = inventory.getItemInMainHand();

        if (itemInHand == null || itemInHand.getType() == Material.AIR) {
            sendMessage(player, MessageConfig.HAT_NOTHING);
            return true;
        }

        ItemStack helmet = inventory.getHelmet();
        inventory.setHelmet(itemInHand);
        inventory.setItemInMainHand(helmet);

        String itemName = itemInHand.getType().name().toLowerCase().replace("_", " ");
        sendMessage(player, MessageConfig.HAT_SET, itemName);
        return true;
    }
}
