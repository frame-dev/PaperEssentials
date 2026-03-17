package ch.framedev.spigotEssentials.utils;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * Utility class for managing plugin messages with color code support
 */
public class MessageConfig {

    // Permission messages
    public static final String NO_PERMISSION_SELF = "§cYou do not have permission to use this command.";
    public static final String NO_PERMISSION_OTHERS = "§cYou do not have permission to use this command on others.";
    public static final String PLAYER_ONLY = "§cThis command can only be used by players.";
    public static final String PLAYER_NOT_FOUND = "§cPlayer not found.";

    // Command messages
    public static final String INVALID_USAGE = "§cInvalid usage. Use: %s";

    // GameMode messages
    public static final String GAMEMODE_CHANGED_SELF = "§aYour game mode has been changed to %s.";
    public static final String GAMEMODE_CHANGED_OTHER = "§a%s's game mode has been changed to %s.";
    public static final String GAMEMODE_CHANGED_TARGET = "§aYour game mode has been changed to %s.";
    public static final String GAMEMODE_INVALID = "§cInvalid game mode. Use survival, creative, adventure, or spectator.";

    // Fly messages
    public static final String FLY_ENABLED_SELF = "§aFly mode enabled.";
    public static final String FLY_DISABLED_SELF = "§cFly mode disabled.";
    public static final String FLY_ENABLED_OTHER = "§aFly mode enabled for %s.";
    public static final String FLY_DISABLED_OTHER = "§cFly mode disabled for %s.";
    public static final String FLY_ENABLED_TARGET = "§aYour fly mode has been enabled.";
    public static final String FLY_DISABLED_TARGET = "§cYour fly mode has been disabled.";

    // Heal messages
    public static final String HEAL_SELF = "§aYou have been healed.";
    public static final String HEAL_OTHER = "§aYou have healed %s.";
    public static final String HEAL_TARGET = "§aYou have been healed by %s.";

    // Feed messages
    public static final String FEED_SELF = "§aYou have been fed.";
    public static final String FEED_OTHER = "§aYou have fed %s.";
    public static final String FEED_TARGET = "§aYou have been fed by %s.";

    // God mode messages
    public static final String GOD_ENABLED_SELF = "§aGod mode enabled.";
    public static final String GOD_DISABLED_SELF = "§cGod mode disabled.";
    public static final String GOD_ENABLED_OTHER = "§aGod mode enabled for %s.";
    public static final String GOD_DISABLED_OTHER = "§cGod mode disabled for %s.";
    public static final String GOD_ENABLED_TARGET = "§aYour god mode has been enabled.";
    public static final String GOD_DISABLED_TARGET = "§cYour god mode has been disabled.";

    // Home messages
    public static final String HOME_SET = "§aHome set successfully.";
    public static final String HOME_SET_NAMED = "§aHome '%s' set successfully.";
    public static final String HOME_NOT_SET = "§cHome not set. Use /sethome to set your home location.";
    public static final String HOME_NOT_SET_NAMED = "§cHome '%s' not set. Use /sethome %s to set your home location.";
    public static final String HOME_TELEPORTED = "§aTeleported to home.";
    public static final String HOME_TELEPORTED_NAMED = "§aTeleported to home '%s'.";
    public static final String HOME_DELETED = "§aHome deleted successfully.";
    public static final String HOME_DELETED_NAMED = "§aHome '%s' deleted successfully.";

    // Spawn messages
    public static final String SPAWN_SET = "§aSpawn point set!";
    public static final String SPAWN_TELEPORTED = "§aTeleported to spawn point!";
    public static final String SPAWN_NOT_SET = "§cSpawn point is not set.";
    public static final String SPAWN_TELEPORTED_RESPAWN = "§aTeleported to spawn point on respawn!";
    public static final String SPAWN_TELEPORTED_JOIN = "§aTeleported to spawn point on join!";

    // Back messages
    public static final String BACK_TELEPORTED = "§aTeleported back to your last location!";
    public static final String BACK_NO_LOCATION = "§cNo previous location found.";
    public static final String BACK_DISABLED = "§cThe back command is disabled in the config.";
    public static final String BACK_USE_COMMAND = "§aUse /back to return to your last location.";

    // Teleport request messages
    public static final String TPA_SENT = "§aTeleport request sent to %s.";
    public static final String TPA_RECEIVED = "§a%s has requested to teleport to you. Type %s to accept or %s to deny.";
    public static final String TPAHERE_RECEIVED = "§a%s has requested to teleport you to them. Type %s to accept or %s to deny.";
    public static final String TPA_NO_REQUEST = "§cNo teleport requests found.";
    public static final String TPA_EXPIRED = "§cTeleport request has expired.";
    public static final String TPA_PLAYER_OFFLINE = "§cPlayer is no longer online.";
    public static final String TPA_ACCEPTED_SENDER = "§aYou have been teleported to %s.";
    public static final String TPA_ACCEPTED_TARGET = "§aTeleport request accepted.";
    public static final String TPAHERE_ACCEPTED_SENDER = "§a%s has teleported to you.";
    public static final String TPA_DENIED_SENDER = "§cYour teleport request to %s was denied.";
    public static final String TPA_DENIED_TARGET = "§cTeleport request from %s denied.";
    public static final String TPA_CANNOT_SELF = "§cYou cannot teleport to yourself.";

    // Clear command messages
    public static final String CLEAR_SELF = "§aYour inventory has been cleared.";
    public static final String CLEAR_OTHER = "§aCleared %s's inventory.";
    public static final String CLEAR_TARGET = "§aYour inventory has been cleared by %s.";

    // Speed command messages
    public static final String SPEED_SET = "§aYour %s speed has been set to %s.";
    public static final String SPEED_SET_OTHER = "§aSet %s's %s speed to %s.";
    public static final String SPEED_INVALID = "§cSpeed must be a number between 0 and 10.";

    // Workbench messages
    public static final String WORKBENCH_OPENED = "§aOpened crafting table.";

    // Enderchest messages
    public static final String ENDERCHEST_OPENED = "§aOpened ender chest.";
    public static final String ENDERCHEST_OPENED_OTHER = "§aOpened %s's ender chest.";

    // Invsee messages
    public static final String INVSEE_OPENED = "§aOpened %s's inventory.";

    // Suicide command messages
    public static final String SUICIDE_SUCCESS = "§cYou have committed suicide.";

    // Ping command messages
    public static final String PING_SELF = "§aYour ping: %sms";
    public static final String PING_OTHER = "§a%s's ping: %sms";

    // Hat command messages
    public static final String HAT_SET = "§aYou are now wearing %s as a hat!";
    public static final String HAT_NOTHING = "§cYou must be holding an item to wear it as a hat.";
    public static final String HAT_AIR = "§cYou cannot wear air as a hat.";

    // AFK messages
    public static final String AFK_ENABLED = "§7You are now AFK.";
    public static final String AFK_DISABLED = "§7You are no longer AFK.";
    public static final String AFK_BROADCAST_ENABLED = "§7%s is now AFK.";
    public static final String AFK_BROADCAST_DISABLED = "§7%s is no longer AFK.";
    public static final String AFK_AUTO = "§7%s is now AFK (Auto).";

    // First join messages
    public static final String FIRST_JOIN_BROADCAST = "§aWelcome %s to the server for the first time!";

    // Player weather messages
    public static final String PWEATHER_SET = "§aYour personal weather has been set to %s.";
    public static final String PWEATHER_SET_OTHER = "§aSet %s's personal weather to %s.";
    public static final String PWEATHER_RESET = "§aYour personal weather has been reset.";
    public static final String PWEATHER_RESET_OTHER = "§aReset %s's personal weather.";
    public static final String PWEATHER_INVALID = "§cInvalid weather type. Use: clear, rain, or reset.";

    // Player time messages
    public static final String PTIME_SET = "§aYour personal time has been set to %s.";
    public static final String PTIME_SET_OTHER = "§aSet %s's personal time to %s.";
    public static final String PTIME_RESET = "§aYour personal time has been reset.";
    public static final String PTIME_RESET_OTHER = "§aReset %s's personal time.";
    public static final String PTIME_INVALID = "§cInvalid time. Use: day, night, noon, midnight, or a number (0-24000), or reset.";

    // World time messages
    public static final String TIME_SET_DAY = "§aTime set to day.";
    public static final String TIME_SET_NIGHT = "§aTime set to night.";

    // World weather messages
    public static final String WEATHER_SET_CLEAR = "§aWeather set to clear.";
    public static final String WEATHER_SET_RAIN = "§aWeather set to rain.";
    public static final String WEATHER_SET_THUNDER = "§aWeather set to thunder.";

    /**
     * Translate color codes in a message
     * @param message The message to translate
     * @return The message with color codes translated
     */
    public static String colorize(String message) {
        if (message == null) return null;
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    /**
     * Send a formatted message to a command sender
     * @param sender The command sender
     * @param message The message to send
     * @param args Arguments to format the message with
     */
    public static void send(CommandSender sender, String message, Object... args) {
        if (sender == null || message == null) return;
        sender.sendMessage(String.format(message, args));
    }

    /**
     * Format a message with arguments
     * @param message The message template
     * @param args Arguments to format the message with
     * @return The formatted message
     */
    public static String format(String message, Object... args) {
        if (message == null) return null;
        return String.format(message, args);
    }
}
