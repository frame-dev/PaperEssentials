package ch.framedev.spigotEssentials.commands;

import ch.framedev.spigotEssentials.utils.MessageConfig;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Command to repair items in hand or entire inventory
 */
public class RepairCommand extends AbstractCommand {

    @Override
    protected boolean execute(CommandSender sender, Command command, String label, String[] args) {
        Player player = asPlayer(sender);
        if (player == null) return true;

        if (!checkPermission(sender, "spigotessentials.repair", MessageConfig.NO_PERMISSION_SELF)) {
            return true;
        }

        // /repair all - repairs entire inventory
        if (args.length > 0 && args[0].equalsIgnoreCase("all")) {
            if (!checkPermission(sender, "spigotessentials.repair.all", MessageConfig.NO_PERMISSION_SELF)) {
                return true;
            }

            int repairedCount = 0;
            for (ItemStack item : player.getInventory().getContents()) {
                if (repairItem(item)) {
                    repairedCount++;
                }
            }

            if (repairedCount > 0) {
                sendMessage(sender, MessageConfig.REPAIR_ALL_SUCCESS, repairedCount);
            } else {
                sendMessage(sender, MessageConfig.REPAIR_NOTHING_TO_REPAIR);
            }
            return true;
        }

        // /repair - repairs item in hand
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item.getType().isAir()) {
            sendMessage(sender, MessageConfig.REPAIR_NO_ITEM);
            return true;
        }

        if (repairItem(item)) {
            sendMessage(sender, MessageConfig.REPAIR_SUCCESS);
        } else {
            sendMessage(sender, MessageConfig.REPAIR_CANNOT_REPAIR);
        }

        return true;
    }

    private boolean repairItem(ItemStack item) {
        if (item == null || item.getType().isAir()) {
            return false;
        }

        ItemMeta meta = item.getItemMeta();
        if (meta instanceof Damageable) {
            Damageable damageable = (Damageable) meta;
            if (damageable.hasDamage()) {
                damageable.setDamage(0);
                item.setItemMeta(meta);
                return true;
            }
        }
        return false;
    }
}
