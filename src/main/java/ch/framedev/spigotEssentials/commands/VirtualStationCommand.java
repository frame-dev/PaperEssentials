package ch.framedev.spigotEssentials.commands;

import ch.framedev.spigotEssentials.utils.MessageConfig;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.MenuType;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

/**
 * Command handler for virtual utility station interfaces.
 */
public class VirtualStationCommand extends AbstractCommand {

    @Override
    protected boolean execute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player player = asPlayer(sender);
        if (player == null) {
            sendMessage(sender, MessageConfig.PLAYER_ONLY);
            return true;
        }

        if (args.length != 0) {
            sendMessage(sender, MessageConfig.INVALID_USAGE, "/" + command.getName());
            return true;
        }

        StationDefinition stationDefinition = getStationDefinition(command.getName().toLowerCase(Locale.ROOT));
        if (stationDefinition == null) {
            return false;
        }

        if (!checkPermission(sender, stationDefinition.permission(), MessageConfig.NO_PERMISSION_SELF)) {
            return true;
        }

        player.openInventory(stationDefinition.menuType().create(player, MessageConfig.component("")));
        sendMessage(player, MessageConfig.VIRTUAL_STATION_OPENED, stationDefinition.displayName());
        return true;
    }

    private StationDefinition getStationDefinition(String commandName) {
        return switch (commandName) {
            case "anvil" -> new StationDefinition(MenuType.ANVIL, "spigotessentials.anvil", "anvil");
            case "grindstone" -> new StationDefinition(MenuType.GRINDSTONE, "spigotessentials.grindstone", "grindstone");
            case "loom" -> new StationDefinition(MenuType.LOOM, "spigotessentials.loom", "loom");
            case "stonecutter" -> new StationDefinition(MenuType.STONECUTTER, "spigotessentials.stonecutter", "stonecutter");
            case "cartographytable" -> new StationDefinition(MenuType.CARTOGRAPHY_TABLE, "spigotessentials.cartographytable", "cartography table");
            case "smithingtable" -> new StationDefinition(MenuType.SMITHING, "spigotessentials.smithingtable", "smithing table");
            default -> null;
        };
    }

    private record StationDefinition(MenuType menuType, String permission, String displayName) { }
}
