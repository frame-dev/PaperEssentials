package ch.framedev.spigotEssentials.commands;

import ch.framedev.spigotEssentials.LocationManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public class HomeCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        String cmd = command.getName().toLowerCase(Locale.ROOT);
        return switch (cmd) {
            case "sethome" -> handleSetHome(sender, args);
            case "home" -> handleHome(sender, args);
            case "delhome" -> handleDelHome(sender, args);
            default -> false;
        };
    }

    private Player requirePlayer(@NotNull CommandSender sender) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return null;
        }
        return player;
    }

    private boolean handleSetHome(@NotNull CommandSender sender, @NotNull String[] args) {
        Player player = requirePlayer(sender);
        if (player == null) return true;

        if (args.length == 0) {
            LocationManager lm = new LocationManager(player.getName() + ".home");
            lm.saveLocation(player.getLocation());
            player.sendMessage("§aHome set successfully.");
            return true;
        } else if (args.length == 1) {
            LocationManager lm = new LocationManager(player.getName() + "." + args[0]);
            lm.saveLocation(player.getLocation());
            player.sendMessage("§aHome '" + args[0] + "' set successfully.");
            return true;
        } else {
            player.sendMessage("§cUsage: /sethome [name]");
            return false;
        }
    }

    private boolean handleHome(@NotNull CommandSender sender, @NotNull String[] args) {
        Player player = requirePlayer(sender);
        if (player == null) return true;

        if (args.length == 0) {
            LocationManager lm = new LocationManager(player.getName() + ".home");
            if (!lm.locationExists()) {
                player.sendMessage("§cHome not set. Use /sethome to set your home location.");
                return true;
            }
            player.teleport(lm.getLocation());
            player.sendMessage("§aTeleported to home.");
            return true;
        } else if (args.length == 1) {
            LocationManager lm = new LocationManager(player.getName() + "." + args[0]);
            if (!lm.locationExists()) {
                player.sendMessage("§cHome '" + args[0] + "' not set. Use /sethome " + args[0] + " to set your home location.");
                return true;
            }
            player.teleport(lm.getLocation());
            player.sendMessage("§aTeleported to home '" + args[0] + "'.");
            return true;
        } else {
            player.sendMessage("§cUsage: /home [name]");
            return false;
        }
    }

    private boolean handleDelHome(@NotNull CommandSender sender, @NotNull String[] args) {
        Player player = requirePlayer(sender);
        if (player == null) return true;

        if (args.length == 0) {
            LocationManager lm = new LocationManager(player.getName() + ".home");
            if (!lm.locationExists()) {
                player.sendMessage("§cHome not set.");
                return true;
            }
            lm.deleteLocation();
            player.sendMessage("§aHome deleted successfully.");
            return true;
        } else if (args.length == 1) {
            LocationManager lm = new LocationManager(player.getName() + "." + args[0]);
            if (!lm.locationExists()) {
                player.sendMessage("§cHome '" + args[0] + "' not set.");
                return true;
            }
            lm.deleteLocation();
            player.sendMessage("§aHome '" + args[0] + "' deleted successfully.");
            return true;
        } else {
            player.sendMessage("§cUsage: /delhome [name]");
            return false;
        }
    }
}