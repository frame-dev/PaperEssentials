package ch.framedev.spigotEssentials.commands;

import ch.framedev.spigotEssentials.PaperEssentials;
import ch.framedev.spigotEssentials.utils.MessageConfig;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Command to manage and teleport to warp points
 */
public class WarpCommand extends AbstractCommand implements TabCompleter {
    private static final Pattern VALID_WARP_NAME = Pattern.compile("[a-z0-9_-]+");

    private final PaperEssentials plugin;
    private final File warpsFile;
    private FileConfiguration warpsConfig;

    public WarpCommand(PaperEssentials plugin) {
        this.plugin = plugin;
        this.warpsFile = new File(plugin.getDataFolder(), "warps.yml");
        loadWarps();
    }

    private void loadWarps() {
        if (!warpsFile.exists()) {
            try {
                warpsFile.getParentFile().mkdirs();
                warpsFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Could not create warps.yml: " + e.getMessage());
            }
        }
        warpsConfig = YamlConfiguration.loadConfiguration(warpsFile);
    }

    private boolean saveWarps() {
        try {
            warpsConfig.save(warpsFile);
            return true;
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save warps.yml: " + e.getMessage());
            return false;
        }
    }

    @Override
    protected boolean execute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        String cmd = command.getName().toLowerCase(Locale.ROOT);

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
        if (!checkPermission(sender, "spigotessentials.warp", MessageConfig.NO_PERMISSION_SELF)) {
            return true;
        }

        if (args.length == 0) {
            List<String> warpNames = getWarpNames();
            if (warpNames.isEmpty()) {
                sendMessage(sender, MessageConfig.WARP_LIST_EMPTY);
            } else {
                sendMessage(sender, MessageConfig.WARP_LIST, String.join(", ", warpNames));
            }
            return true;
        }

        Player player = asPlayer(sender);
        if (player == null) {
            sendMessage(sender, MessageConfig.PLAYER_ONLY);
            return true;
        }

        String warpName = normalizeWarpName(args[0]);
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
        if (player == null) {
            sendMessage(sender, MessageConfig.PLAYER_ONLY);
            return true;
        }

        if (!checkPermission(sender, "spigotessentials.setwarp", MessageConfig.NO_PERMISSION_SELF)) {
            return true;
        }

        if (args.length != 1) {
            sendMessage(sender, MessageConfig.WARP_USAGE_SET);
            return true;
        }

        String warpName = normalizeWarpName(args[0]);
        if (!isValidWarpName(warpName)) {
            sendMessage(sender, MessageConfig.WARP_INVALID_NAME, args[0]);
            return true;
        }

        Location location = player.getLocation();
        if (location.getWorld() == null) {
            sendMessage(sender, MessageConfig.WARP_INVALID, warpName);
            return true;
        }

        String warpPath = getWarpPath(warpName);
        warpsConfig.set(warpPath + ".world", location.getWorld().getName());
        warpsConfig.set(warpPath + ".x", location.getX());
        warpsConfig.set(warpPath + ".y", location.getY());
        warpsConfig.set(warpPath + ".z", location.getZ());
        warpsConfig.set(warpPath + ".yaw", location.getYaw());
        warpsConfig.set(warpPath + ".pitch", location.getPitch());
        if (!saveWarps()) {
            sendMessage(sender, MessageConfig.WARP_SAVE_FAILED);
            return true;
        }

        sendMessage(sender, MessageConfig.WARP_CREATED, warpName);
        return true;
    }

    private boolean handleDelWarp(CommandSender sender, String[] args) {
        if (!checkPermission(sender, "spigotessentials.delwarp", MessageConfig.NO_PERMISSION_SELF)) {
            return true;
        }

        if (args.length != 1) {
            sendMessage(sender, MessageConfig.WARP_USAGE_DELETE);
            return true;
        }

        String warpName = normalizeWarpName(args[0]);
        if (!warpsConfig.contains(warpName)) {
            sendMessage(sender, MessageConfig.WARP_NOT_FOUND, warpName);
            return true;
        }

        warpsConfig.set(warpName, null);
        if (!saveWarps()) {
            sendMessage(sender, MessageConfig.WARP_SAVE_FAILED);
            return true;
        }

        sendMessage(sender, MessageConfig.WARP_DELETED, warpName);
        return true;
    }

    private @Nullable Location getWarpLocation(String warpName) {
        String warpPath = getWarpPath(warpName);
        String worldName = warpsConfig.getString(warpPath + ".world");
        if (worldName == null) return null;

        var world = plugin.getServer().getWorld(worldName);
        if (world == null) return null;

        double x = warpsConfig.getDouble(warpPath + ".x");
        double y = warpsConfig.getDouble(warpPath + ".y");
        double z = warpsConfig.getDouble(warpPath + ".z");
        float yaw = (float) warpsConfig.getDouble(warpPath + ".yaw");
        float pitch = (float) warpsConfig.getDouble(warpPath + ".pitch");

        return new Location(world, x, y, z, yaw, pitch);
    }

    public List<String> getWarpNames() {
        return warpsConfig.getKeys(false).stream()
                .sorted(Comparator.naturalOrder())
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length != 1) {
            return List.of();
        }

        String commandName = command.getName().toLowerCase(Locale.ROOT);
        if ("setwarp".equals(commandName) || !hasPermission(sender, getPermissionForCommand(commandName))) {
            return List.of();
        }

        String prefix = normalizeWarpName(args[0]);
        return getWarpNames().stream()
                .filter(warpName -> warpName.startsWith(prefix))
                .collect(Collectors.toList());
    }

    private String getPermissionForCommand(String commandName) {
        return switch (commandName) {
            case "warp" -> "spigotessentials.warp";
            case "delwarp" -> "spigotessentials.delwarp";
            default -> "";
        };
    }

    private boolean isValidWarpName(String warpName) {
        return VALID_WARP_NAME.matcher(warpName).matches();
    }

    private String normalizeWarpName(String warpName) {
        return warpName.toLowerCase(Locale.ROOT);
    }

    private String getWarpPath(String warpName) {
        return warpName;
    }
}
