package ch.framedev.spigotEssentials;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.logging.Level;

@SuppressWarnings("BooleanMethodIsAlwaysInverted")
public class LocationManager {

    private final String locationName;
    private final File file = new File(SpigotEssentials.getInstance().getDataFolder(), "locations.yml");
    private final FileConfiguration config = YamlConfiguration.loadConfiguration(file);

    public LocationManager(String locationName) {
        this.locationName = locationName;
    }

    public void saveLocation(Location location) {
        config.set(locationName, locationToString(location));
        try {
            config.save(file);
        } catch (Exception e) {
            SpigotEssentials.getInstance().getLogger().log(Level.SEVERE, "Could not save location to locations.yml", e);
        }
    }

    public Location getLocation() {
        if(!config.contains(locationName)) {
            return null;
        }
        String locationString = config.getString(locationName);
        if(locationString == null) {
            return null;
        }
        return stringToLocation(locationString);
    }

    public void deleteLocation() {
        config.set(locationName, null);
        try {
            config.save(file);
        } catch (Exception e) {
            SpigotEssentials.getInstance().getLogger().log(Level.SEVERE, "Could not delete location from locations.yml", e);
        }
    }

    public boolean locationExists() {
        return config.contains(locationName);
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
        String[] parts = locationString.split(";");
        Location location = new Location(
                SpigotEssentials.getInstance().getServer().getWorld(parts[0]),
                Double.parseDouble(parts[1]),
                Double.parseDouble(parts[2]),
                Double.parseDouble(parts[3])
        );
        location.setYaw(Float.parseFloat(parts[4]));
        location.setPitch(Float.parseFloat(parts[5]));
        return location;
    }
}
