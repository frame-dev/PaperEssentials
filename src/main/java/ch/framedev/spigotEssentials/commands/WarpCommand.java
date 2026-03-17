package ch.framedev.spigotEssentials.commands;

import ch.framedev.spigotEssentials.PaperEssentials;
import ch.framedev.spigotEssentials.utils.MessageConfig;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Command to manage and teleport to warp points
 */
public class WarpCommand extends AbstractCommand {

    private final File warpsFile;
    private FileConfiguration warpsConfig;

    public WarpCommand() {
        this.warpsFile = new File(PaperEssentials.getInstance().getDataFolder(), "warps.yml");
        loadWarps();
    }

    private void loadWarps() {
        if (!warpsFile.exists()) {
            try {
                warpsFile.getParentFile().mkdirs();
                warpsFile.createNewFile();
            } catch (IOException e) {
                PaperEssentials.getInstance().getLogger().severe("Could not create warps.yml: " + e.getMessage());
            }
        }
        warpsConfig = YamlConfiguration.loadConfiguration(warpsFile);
    }

    private void saveWarps() {
        try {
            warpsConfig.save(warpsFile);
        } catch (IOException e) {
            PaperEssentials.getInstance().getLogger().severe("Could not save warps.yml: " + e.getMessage());
        }
    }

    @Override
    protected boolean execute(CommandSender sender, Command command, String label, String[] args) {
        String cmd = label.toLowerCase();

        switch (cmd) {
            case "warp":
                return handleWarp(sender, args);
            case "setwarp":
                return handleSetWarp(sender, args);
            case "delwarp":
                return handleDelWarp(sender, args);
            default:
                return false;
        }
    }

    private boolean handleWarp(CommandSender sender, String[] args) {
        Player player = asPlayer(sender);
        if (player == null) return true;

        if (args.length == 0) {
            List<String> warpNames = new ArrayList<>(warpsConfig.getKeys(false));
            if (warpNames.isEmpty()) {
                sendMessage(sender, MessageConfig.WARP_LIST_EMPTY);
            } else {
                sendMessage(sender, MessageConfig.WARP_LIST, String.join(", ", warpNames));
            }
            return true;
        }

        if (!checkPermission(sender, "spigotessentials.warp", MessageConfig.NO_PERMISSION_SELF)) {
            return true;
        }

        String warpName = args[0].toLowerCase();
        if (!warpsConfig.contains(warpName)) {
            sendMessage(sender, MessageConfig.WARP_NOT_FOUND, warpName);
            return true;
        }

        Location location = getWarpLocation(warpName);
        if (location == null || location.getWorld() == null) {
            sendMessage(sender, MessageConfig.WARP_INVALID, warpName);
            return true;
        }

        player.teleport(location);
        sendMessage(sender, MessageConfig.WARP_TELEPORTED, warpName);
        return true;
    }

    private boolean handleSetWarp(CommandSender sender, String[] args) {
        Player player = asPlayer(sender);
        if (player == null) return true;

        if (!checkPermission(sender, "spigotessentials.setwarp", MessageConfig.NO_PERMISSION_SELF)) {
            return true;
        }

        if (args.length == 0) {
            sendMessage(sender, MessageConfig.WARP_USAGE_SET);
            return true;
        }

        String warpName = args[0].toLowerCase();
        Location location = player.getLocation();

        warpsConfig.set(warpName + ".world", location.getWorld().getName());
        warpsConfig.set(warpName + ".x", location.getX());
        warpsConfig.set(warpName + ".y", location.getY());
        warpsConfig.set(warpName + ".z", location.getZ());
        warpsConfig.set(warpName + ".yaw", location.getYaw());
        warpsConfig.set(warpName + ".pitch", location.getPitch());
        saveWarps();

        sendMessage(sender, MessageConfig.WARP_CREATED, warpName);
        return true;
    }

    private boolean handleDelWarp(CommandSender sender, String[] args) {
        if (!checkPermission(sender, "spigotessentials.delwarp", MessageConfig.NO_PERMISSION_SELF)) {
            return true;
        }

        if (args.length == 0) {
            sendMessage(sender, MessageConfig.WARP_USAGE_DELETE);
            return true;
        }

        String warpName = args[0].toLowerCase();
        if (!warpsConfig.contains(warpName)) {
            sendMessage(sender, MessageConfig.WARP_NOT_FOUND, warpName);
            return true;
        }

        warpsConfig.set(warpName, null);
        saveWarps();

        sendMessage(sender, MessageConfig.WARP_DELETED, warpName);
        return true;
    }

    private Location getWarpLocation(String warpName) {
        String worldName = warpsConfig.getString(warpName + ".world");
        if (worldName == null) return null;

        var world = PaperEssentials.getInstance().getServer().getWorld(worldName);
        if (world == null) return null;

        double x = warpsConfig.getDouble(warpName + ".x");
        double y = warpsConfig.getDouble(warpName + ".y");
        double z = warpsConfig.getDouble(warpName + ".z");
        float yaw = (float) warpsConfig.getDouble(warpName + ".yaw");
        float pitch = (float) warpsConfig.getDouble(warpName + ".pitch");

        return new Location(world, x, y, z, yaw, pitch);
    }

    public List<String> getWarpNames() {
        return new ArrayList<>(warpsConfig.getKeys(false));
    }
}
