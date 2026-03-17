package ch.framedev.spigotEssentials.commands;

import ch.framedev.spigotEssentials.LocationManager;
import ch.framedev.spigotEssentials.utils.MessageConfig;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

/**
 * Command handler for home-related commands
 */
public class HomeCommand extends AbstractCommand {

    @Override
    protected boolean execute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        String cmd = command.getName().toLowerCase(Locale.ROOT);
        return switch (cmd) {
            case "sethome" -> handleSetHome(sender, args);
            case "home" -> handleHome(sender, args);
            case "delhome" -> handleDelHome(sender, args);
            default -> false;
        };
    }

    private boolean handleSetHome(@NotNull CommandSender sender, @NotNull String[] args) {
        Player player = asPlayer(sender);
        if (player == null) {
            sendMessage(sender, MessageConfig.PLAYER_ONLY);
            return true;
        }

        if (!checkPermission(sender, "spigotessentials.home.set", MessageConfig.NO_PERMISSION_SELF)) {
            return true;
        }

        if (args.length == 0) {
            LocationManager lm = new LocationManager(player.getName() + ".home");
            if (lm.saveLocation(player.getLocation())) {
                sendMessage(player, MessageConfig.HOME_SET);
            } else {
                sendMessage(player, "§cFailed to save home location.");
            }
            return true;
        } else if (args.length == 1) {
            LocationManager lm = new LocationManager(player.getName() + "." + args[0]);
            if (lm.saveLocation(player.getLocation())) {
                sendMessage(player, MessageConfig.HOME_SET_NAMED, args[0]);
            } else {
                sendMessage(player, "§cFailed to save home location.");
            }
            return true;
        } else {
            sendMessage(player, MessageConfig.INVALID_USAGE, "/sethome [name]");
            return false;
        }
    }

    private boolean handleHome(@NotNull CommandSender sender, @NotNull String[] args) {
        Player player = asPlayer(sender);
        if (player == null) {
            sendMessage(sender, MessageConfig.PLAYER_ONLY);
            return true;
        }

        if (!checkPermission(sender, "spigotessentials.home.teleport", MessageConfig.NO_PERMISSION_SELF)) {
            return true;
        }

        if (args.length == 0) {
            LocationManager lm = new LocationManager(player.getName() + ".home");
            if (!lm.hasLocation()) {
                sendMessage(player, MessageConfig.HOME_NOT_SET);
                return true;
            }
            Location homeLocation = lm.getLocation();
            if (homeLocation == null) {
                sendMessage(player, MessageConfig.HOME_NOT_SET);
                return true;
            }
            player.teleport(homeLocation);
            sendMessage(player, MessageConfig.HOME_TELEPORTED);
            return true;
        } else if (args.length == 1) {
            LocationManager lm = new LocationManager(player.getName() + "." + args[0]);
            if (!lm.hasLocation()) {
                sendMessage(player, MessageConfig.HOME_NOT_SET_NAMED, args[0], args[0]);
                return true;
            }
            Location homeLocation = lm.getLocation();
            if (homeLocation == null) {
                sendMessage(player, MessageConfig.HOME_NOT_SET_NAMED, args[0], args[0]);
                return true;
            }
            player.teleport(homeLocation);
            sendMessage(player, MessageConfig.HOME_TELEPORTED_NAMED, args[0]);
            return true;
        } else {
            sendMessage(player, MessageConfig.INVALID_USAGE, "/home [name]");
            return false;
        }
    }

    private boolean handleDelHome(@NotNull CommandSender sender, @NotNull String[] args) {
        Player player = asPlayer(sender);
        if (player == null) {
            sendMessage(sender, MessageConfig.PLAYER_ONLY);
            return true;
        }

        if (!checkPermission(sender, "spigotessentials.home.delete", MessageConfig.NO_PERMISSION_SELF)) {
            return true;
        }

        if (args.length == 0) {
            LocationManager lm = new LocationManager(player.getName() + ".home");
            if (!lm.hasLocation()) {
                sendMessage(player, "§cHome not set.");
                return true;
            }
            if (lm.deleteLocation()) {
                sendMessage(player, MessageConfig.HOME_DELETED);
            } else {
                sendMessage(player, "§cFailed to delete home location.");
            }
            return true;
        } else if (args.length == 1) {
            LocationManager lm = new LocationManager(player.getName() + "." + args[0]);
            if (!lm.hasLocation()) {
                sendMessage(player, "§cHome '%s' not set.", args[0]);
                return true;
            }
            if (lm.deleteLocation()) {
                sendMessage(player, MessageConfig.HOME_DELETED_NAMED, args[0]);
            } else {
                sendMessage(player, "§cFailed to delete home location.");
            }
            return true;
        } else {
            sendMessage(player, MessageConfig.INVALID_USAGE, "/delhome [name]");
            return false;
        }
    }
}