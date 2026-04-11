package ch.framedev.spigotEssentials;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;

public class LocationManager {

    private final String locationName;
    private final PaperEssentials plugin;
    private final File file;
    private final FileConfiguration config;

    public LocationManager(String locationName) {
        this.locationName = locationName;
        this.plugin = PaperEssentials.getInstance();
        this.file = new File(plugin.getDataFolder(), "locations.yml");

        // Ensure parent directory exists
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }

        this.config = YamlConfiguration.loadConfiguration(file);
    }

    /**
     * Save a location to the configuration file
     * @param location The location to save
     * @return true if saved successfully, false otherwise
     */
    public boolean saveLocation(Location location) {
        if (location == null || location.getWorld() == null) {
            plugin.getLogger().warning("Cannot save null location or location with null world");
            return false;
        }

        config.set(locationName, locationToString(location));
        try {
            config.save(file);
            return true;
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not save location to locations.yml", e);
            return false;
        }
    }

    /**
     * Get a location from the configuration file
     * @return The location, or null if not found or invalid
     */
    public Location getLocation() {
        if (!config.contains(locationName)) {
            return null;
        }
        String locationString = config.getString(locationName);
        if (locationString == null) {
            return null;
        }
        return stringToLocation(locationString);
    }

    /**
     * Delete a location from the configuration file
     * @return true if deleted successfully, false otherwise
     */
    public boolean deleteLocation() {
        config.set(locationName, null);
        try {
            config.save(file);
            return true;
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not delete location from locations.yml", e);
            return false;
        }
    }

    /**
     * Check if a location exists in the configuration
     * @return true if the location exists, false otherwise
     */
    public boolean hasLocation() {
        return config.contains(locationName);
    }

    /**
     * List the saved child location keys beneath a section, such as a player's homes.
     * @param sectionName The root section name
     * @return Sorted child keys, or an empty list if none exist
     */
    public static List<String> getLocationNames(String sectionName) {
        PaperEssentials plugin = PaperEssentials.getInstance();
        File file = new File(plugin.getDataFolder(), "locations.yml");
        if (!file.exists()) {
            return List.of();
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection section = config.getConfigurationSection(sectionName);
        if (section == null) {
            return List.of();
        }

        return section.getKeys(false).stream()
                .sorted(Comparator.naturalOrder())
                .collect(java.util.stream.Collectors.toCollection(ArrayList::new));
    }

    private String locationToString(Location location) {
        return String.format("%s;%.2f;%.2f;%.2f;%.2f;%.2f",
                location.getWorld().getName(),
                location.getX(),
                location.getY(),
                location.getZ(),
                location.getYaw(),
                location.getPitch());
    }

    private Location stringToLocation(String locationString) {
        try {
            String[] parts = locationString.split(";");
            if (parts.length != 6) {
                plugin.getLogger().warning("Invalid location format for: " + locationName);
                return null;
            }

            World world = plugin.getServer().getWorld(parts[0]);
            if (world == null) {
                plugin.getLogger().warning("World '" + parts[0] + "' not found for location: " + locationName);
                return null;
            }

            double x = Double.parseDouble(parts[1]);
            double y = Double.parseDouble(parts[2]);
            double z = Double.parseDouble(parts[3]);
            float yaw = Float.parseFloat(parts[4]);
            float pitch = Float.parseFloat(parts[5]);

            Location location = new Location(world, x, y, z);
            location.setYaw(yaw);
            location.setPitch(pitch);
            return location;
        } catch (NumberFormatException e) {
            plugin.getLogger().log(Level.WARNING, "Invalid number format in location: " + locationName, e);
            return null;
        }
    }
}
