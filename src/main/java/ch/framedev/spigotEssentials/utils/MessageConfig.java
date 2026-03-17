package ch.framedev.spigotEssentials.utils;

import ch.framedev.spigotEssentials.PaperEssentials;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

/**
 * Utility class for managing plugin messages with color code support
 * Messages are loaded from messages.yml configuration file
 */
public class MessageConfig {

    private static FileConfiguration messagesConfig;
    private static File messagesFile;

    // Permission messages
    public static String NO_PERMISSION_SELF;
    public static String NO_PERMISSION_OTHERS;
    public static String PLAYER_ONLY;
    public static String PLAYER_NOT_FOUND;

    // Command messages
    public static String INVALID_USAGE;

    // GameMode messages
    public static String GAMEMODE_CHANGED_SELF;
    public static String GAMEMODE_CHANGED_OTHER;
    public static String GAMEMODE_CHANGED_TARGET;
    public static String GAMEMODE_INVALID;

    // Fly messages
    public static String FLY_ENABLED_SELF;
    public static String FLY_DISABLED_SELF;
    public static String FLY_ENABLED_OTHER;
    public static String FLY_DISABLED_OTHER;
    public static String FLY_ENABLED_TARGET;
    public static String FLY_DISABLED_TARGET;

    // Heal messages
    public static String HEAL_SELF;
    public static String HEAL_OTHER;
    public static String HEAL_TARGET;

    // Feed messages
    public static String FEED_SELF;
    public static String FEED_OTHER;
    public static String FEED_TARGET;

    // God mode messages
    public static String GOD_ENABLED_SELF;
    public static String GOD_DISABLED_SELF;
    public static String GOD_ENABLED_OTHER;
    public static String GOD_DISABLED_OTHER;
    public static String GOD_ENABLED_TARGET;
    public static String GOD_DISABLED_TARGET;

    // Home messages
    public static String HOME_SET;
    public static String HOME_SET_NAMED;
    public static String HOME_NOT_SET;
    public static String HOME_NOT_SET_NAMED;
    public static String HOME_TELEPORTED;
    public static String HOME_TELEPORTED_NAMED;
    public static String HOME_DELETED;
    public static String HOME_DELETED_NAMED;

    // Spawn messages
    public static String SPAWN_SET;
    public static String SPAWN_TELEPORTED;
    public static String SPAWN_NOT_SET;
    public static String SPAWN_TELEPORTED_RESPAWN;
    public static String SPAWN_TELEPORTED_JOIN;

    // Back messages
    public static String BACK_TELEPORTED;
    public static String BACK_NO_LOCATION;
    public static String BACK_DISABLED;
    public static String BACK_USE_COMMAND;

    // Teleport request messages
    public static String TPA_SENT;
    public static String TPA_RECEIVED;
    public static String TPAHERE_RECEIVED;
    public static String TPA_NO_REQUEST;
    public static String TPA_EXPIRED;
    public static String TPA_PLAYER_OFFLINE;
    public static String TPA_ACCEPTED_SENDER;
    public static String TPA_ACCEPTED_TARGET;
    public static String TPAHERE_ACCEPTED_SENDER;
    public static String TPA_DENIED_SENDER;
    public static String TPA_DENIED_TARGET;
    public static String TPA_CANNOT_SELF;

    // Clear command messages
    public static String CLEAR_SELF;
    public static String CLEAR_OTHER;
    public static String CLEAR_TARGET;

    // Speed command messages
    public static String SPEED_SET;
    public static String SPEED_SET_OTHER;
    public static String SPEED_INVALID;

    // Workbench messages
    public static String WORKBENCH_OPENED;

    // Enderchest messages
    public static String ENDERCHEST_OPENED;
    public static String ENDERCHEST_OPENED_OTHER;

    // Invsee messages
    public static String INVSEE_OPENED;

    // Suicide command messages
    public static String SUICIDE_SUCCESS;

    // Ping command messages
    public static String PING_SELF;
    public static String PING_OTHER;

    // Hat command messages
    public static String HAT_SET;
    public static String HAT_NOTHING;
    public static String HAT_AIR;

    // AFK messages
    public static String AFK_ENABLED;
    public static String AFK_DISABLED;
    public static String AFK_BROADCAST_ENABLED;
    public static String AFK_BROADCAST_DISABLED;
    public static String AFK_AUTO;

    // First join messages
    public static String FIRST_JOIN_BROADCAST;

    // Player weather messages
    public static String PWEATHER_SET;
    public static String PWEATHER_SET_OTHER;
    public static String PWEATHER_RESET;
    public static String PWEATHER_RESET_OTHER;
    public static String PWEATHER_INVALID;

    // Player time messages
    public static String PTIME_SET;
    public static String PTIME_SET_OTHER;
    public static String PTIME_RESET;
    public static String PTIME_RESET_OTHER;
    public static String PTIME_INVALID;

    // World time messages
    public static String TIME_SET_DAY;
    public static String TIME_SET_NIGHT;

    // World weather messages
    public static String WEATHER_SET_CLEAR;
    public static String WEATHER_SET_RAIN;
    public static String WEATHER_SET_THUNDER;

    // Warp messages
    public static String WARP_TELEPORTED;
    public static String WARP_NOT_FOUND;
    public static String WARP_INVALID;
    public static String WARP_CREATED;
    public static String WARP_DELETED;
    public static String WARP_LIST;
    public static String WARP_LIST_EMPTY;
    public static String WARP_USAGE_SET;
    public static String WARP_USAGE_DELETE;

    // Repair messages
    public static String REPAIR_SUCCESS;
    public static String REPAIR_ALL_SUCCESS;
    public static String REPAIR_NO_ITEM;
    public static String REPAIR_CANNOT_REPAIR;
    public static String REPAIR_NOTHING_TO_REPAIR;

    // Enchant messages
    public static String ENCHANT_OPENED;

    // Freeze messages
    public static String FREEZE_FROZEN;
    public static String FREEZE_UNFROZEN;
    public static String FREEZE_FROZEN_TARGET;
    public static String FREEZE_UNFROZEN_TARGET;
    public static String FREEZE_CANNOT_MOVE;
    public static String FREEZE_USAGE;

    // Vanish messages
    public static String VANISH_ENABLED;
    public static String VANISH_DISABLED;

    // Broadcast messages
    public static String BROADCAST_FORMAT;
    public static String BROADCAST_USAGE;

    /**
     * Load messages from the messages.yml file
     */
    public static void load() {
        PaperEssentials plugin = PaperEssentials.getInstance();
        messagesFile = new File(plugin.getDataFolder(), "messages.yml");

        // Create messages.yml if it doesn't exist
        if (!messagesFile.exists()) {
            try {
                messagesFile.getParentFile().mkdirs();
                try (InputStream in = plugin.getResource("messages.yml")) {
                    if (in != null) {
                        Files.copy(in, messagesFile.toPath());
                    }
                }
            } catch (IOException e) {
                plugin.getLogger().severe("Could not create messages.yml: " + e.getMessage());
            }
        }

        messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);
        loadMessages();
    }

    /**
     * Reload messages from the messages.yml file
     */
    public static void reload() {
        messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);
        loadMessages();
    }

    /**
     * Load all messages from configuration into static fields
     */
    private static void loadMessages() {
        // Permission messages
        NO_PERMISSION_SELF = colorize(messagesConfig.getString("no-permission-self", "&cYou do not have permission to use this command."));
        NO_PERMISSION_OTHERS = colorize(messagesConfig.getString("no-permission-others", "&cYou do not have permission to use this command on others."));
        PLAYER_ONLY = colorize(messagesConfig.getString("player-only", "&cThis command can only be used by players."));
        PLAYER_NOT_FOUND = colorize(messagesConfig.getString("player-not-found", "&cPlayer not found."));

        // Command messages
        INVALID_USAGE = colorize(messagesConfig.getString("invalid-usage", "&cInvalid usage. Use: %s"));

        // GameMode messages
        GAMEMODE_CHANGED_SELF = colorize(messagesConfig.getString("gamemode-changed-self", "&aYour game mode has been changed to %s."));
        GAMEMODE_CHANGED_OTHER = colorize(messagesConfig.getString("gamemode-changed-other", "&a%s's game mode has been changed to %s."));
        GAMEMODE_CHANGED_TARGET = colorize(messagesConfig.getString("gamemode-changed-target", "&aYour game mode has been changed to %s."));
        GAMEMODE_INVALID = colorize(messagesConfig.getString("gamemode-invalid", "&cInvalid game mode. Use survival, creative, adventure, or spectator."));

        // Fly messages
        FLY_ENABLED_SELF = colorize(messagesConfig.getString("fly-enabled-self", "&aFly mode enabled."));
        FLY_DISABLED_SELF = colorize(messagesConfig.getString("fly-disabled-self", "&cFly mode disabled."));
        FLY_ENABLED_OTHER = colorize(messagesConfig.getString("fly-enabled-other", "&aFly mode enabled for %s."));
        FLY_DISABLED_OTHER = colorize(messagesConfig.getString("fly-disabled-other", "&cFly mode disabled for %s."));
        FLY_ENABLED_TARGET = colorize(messagesConfig.getString("fly-enabled-target", "&aYour fly mode has been enabled."));
        FLY_DISABLED_TARGET = colorize(messagesConfig.getString("fly-disabled-target", "&cYour fly mode has been disabled."));

        // Heal messages
        HEAL_SELF = colorize(messagesConfig.getString("heal-self", "&aYou have been healed."));
        HEAL_OTHER = colorize(messagesConfig.getString("heal-other", "&aYou have healed %s."));
        HEAL_TARGET = colorize(messagesConfig.getString("heal-target", "&aYou have been healed by %s."));

        // Feed messages
        FEED_SELF = colorize(messagesConfig.getString("feed-self", "&aYou have been fed."));
        FEED_OTHER = colorize(messagesConfig.getString("feed-other", "&aYou have fed %s."));
        FEED_TARGET = colorize(messagesConfig.getString("feed-target", "&aYou have been fed by %s."));

        // God mode messages
        GOD_ENABLED_SELF = colorize(messagesConfig.getString("god-enabled-self", "&aGod mode enabled."));
        GOD_DISABLED_SELF = colorize(messagesConfig.getString("god-disabled-self", "&cGod mode disabled."));
        GOD_ENABLED_OTHER = colorize(messagesConfig.getString("god-enabled-other", "&aGod mode enabled for %s."));
        GOD_DISABLED_OTHER = colorize(messagesConfig.getString("god-disabled-other", "&cGod mode disabled for %s."));
        GOD_ENABLED_TARGET = colorize(messagesConfig.getString("god-enabled-target", "&aYour god mode has been enabled."));
        GOD_DISABLED_TARGET = colorize(messagesConfig.getString("god-disabled-target", "&cYour god mode has been disabled."));

        // Home messages
        HOME_SET = colorize(messagesConfig.getString("home-set", "&aHome set successfully."));
        HOME_SET_NAMED = colorize(messagesConfig.getString("home-set-named", "&aHome '%s' set successfully."));
        HOME_NOT_SET = colorize(messagesConfig.getString("home-not-set", "&cHome not set. Use /sethome to set your home location."));
        HOME_NOT_SET_NAMED = colorize(messagesConfig.getString("home-not-set-named", "&cHome '%s' not set. Use /sethome %s to set your home location."));
        HOME_TELEPORTED = colorize(messagesConfig.getString("home-teleported", "&aTeleported to home."));
        HOME_TELEPORTED_NAMED = colorize(messagesConfig.getString("home-teleported-named", "&aTeleported to home '%s'."));
        HOME_DELETED = colorize(messagesConfig.getString("home-deleted", "&aHome deleted successfully."));
        HOME_DELETED_NAMED = colorize(messagesConfig.getString("home-deleted-named", "&aHome '%s' deleted successfully."));

        // Spawn messages
        SPAWN_SET = colorize(messagesConfig.getString("spawn-set", "&aSpawn point set!"));
        SPAWN_TELEPORTED = colorize(messagesConfig.getString("spawn-teleported", "&aTeleported to spawn point!"));
        SPAWN_NOT_SET = colorize(messagesConfig.getString("spawn-not-set", "&cSpawn point is not set."));
        SPAWN_TELEPORTED_RESPAWN = colorize(messagesConfig.getString("spawn-teleported-respawn", "&aTeleported to spawn point on respawn!"));
        SPAWN_TELEPORTED_JOIN = colorize(messagesConfig.getString("spawn-teleported-join", "&aTeleported to spawn point on join!"));

        // Back messages
        BACK_TELEPORTED = colorize(messagesConfig.getString("back-teleported", "&aTeleported back to your last location!"));
        BACK_NO_LOCATION = colorize(messagesConfig.getString("back-no-location", "&cNo previous location found."));
        BACK_DISABLED = colorize(messagesConfig.getString("back-disabled", "&cThe back command is disabled in the config."));
        BACK_USE_COMMAND = colorize(messagesConfig.getString("back-use-command", "&aUse /back to return to your last location."));

        // Teleport request messages
        TPA_SENT = colorize(messagesConfig.getString("tpa-sent", "&aTeleport request sent to %s."));
        TPA_RECEIVED = colorize(messagesConfig.getString("tpa-received", "&a%s has requested to teleport to you. Type %s to accept or %s to deny."));
        TPAHERE_RECEIVED = colorize(messagesConfig.getString("tpahere-received", "&a%s has requested to teleport you to them. Type %s to accept or %s to deny."));
        TPA_NO_REQUEST = colorize(messagesConfig.getString("tpa-no-request", "&cNo teleport requests found."));
        TPA_EXPIRED = colorize(messagesConfig.getString("tpa-expired", "&cTeleport request has expired."));
        TPA_PLAYER_OFFLINE = colorize(messagesConfig.getString("tpa-player-offline", "&cPlayer is no longer online."));
        TPA_ACCEPTED_SENDER = colorize(messagesConfig.getString("tpa-accepted-sender", "&aYou have been teleported to %s."));
        TPA_ACCEPTED_TARGET = colorize(messagesConfig.getString("tpa-accepted-target", "&aTeleport request accepted."));
        TPAHERE_ACCEPTED_SENDER = colorize(messagesConfig.getString("tpahere-accepted-sender", "&a%s has teleported to you."));
        TPA_DENIED_SENDER = colorize(messagesConfig.getString("tpa-denied-sender", "&cYour teleport request to %s was denied."));
        TPA_DENIED_TARGET = colorize(messagesConfig.getString("tpa-denied-target", "&cTeleport request from %s denied."));
        TPA_CANNOT_SELF = colorize(messagesConfig.getString("tpa-cannot-self", "&cYou cannot teleport to yourself."));

        // Clear command messages
        CLEAR_SELF = colorize(messagesConfig.getString("clear-self", "&aYour inventory has been cleared."));
        CLEAR_OTHER = colorize(messagesConfig.getString("clear-other", "&aCleared %s's inventory."));
        CLEAR_TARGET = colorize(messagesConfig.getString("clear-target", "&aYour inventory has been cleared by %s."));

        // Speed command messages
        SPEED_SET = colorize(messagesConfig.getString("speed-set", "&aYour %s speed has been set to %s."));
        SPEED_SET_OTHER = colorize(messagesConfig.getString("speed-set-other", "&aSet %s's %s speed to %s."));
        SPEED_INVALID = colorize(messagesConfig.getString("speed-invalid", "&cSpeed must be a number between 0 and 10."));

        // Workbench messages
        WORKBENCH_OPENED = colorize(messagesConfig.getString("workbench-opened", "&aOpened crafting table."));

        // Enderchest messages
        ENDERCHEST_OPENED = colorize(messagesConfig.getString("enderchest-opened", "&aOpened ender chest."));
        ENDERCHEST_OPENED_OTHER = colorize(messagesConfig.getString("enderchest-opened-other", "&aOpened %s's ender chest."));

        // Invsee messages
        INVSEE_OPENED = colorize(messagesConfig.getString("invsee-opened", "&aOpened %s's inventory."));

        // Suicide command messages
        SUICIDE_SUCCESS = colorize(messagesConfig.getString("suicide-success", "&cYou have committed suicide."));

        // Ping command messages
        PING_SELF = colorize(messagesConfig.getString("ping-self", "&aYour ping: %sms"));
        PING_OTHER = colorize(messagesConfig.getString("ping-other", "&a%s's ping: %sms"));

        // Hat command messages
        HAT_SET = colorize(messagesConfig.getString("hat-set", "&aYou are now wearing %s as a hat!"));
        HAT_NOTHING = colorize(messagesConfig.getString("hat-nothing", "&cYou must be holding an item to wear it as a hat."));
        HAT_AIR = colorize(messagesConfig.getString("hat-air", "&cYou cannot wear air as a hat."));

        // AFK messages
        AFK_ENABLED = colorize(messagesConfig.getString("afk-enabled", "&7You are now AFK."));
        AFK_DISABLED = colorize(messagesConfig.getString("afk-disabled", "&7You are no longer AFK."));
        AFK_BROADCAST_ENABLED = colorize(messagesConfig.getString("afk-broadcast-enabled", "&7%s is now AFK."));
        AFK_BROADCAST_DISABLED = colorize(messagesConfig.getString("afk-broadcast-disabled", "&7%s is no longer AFK."));
        AFK_AUTO = colorize(messagesConfig.getString("afk-auto", "&7%s is now AFK (Auto)."));

        // First join messages
        FIRST_JOIN_BROADCAST = colorize(messagesConfig.getString("first-join-broadcast", "&aWelcome %s to the server for the first time!"));

        // Player weather messages
        PWEATHER_SET = colorize(messagesConfig.getString("pweather-set", "&aYour personal weather has been set to %s."));
        PWEATHER_SET_OTHER = colorize(messagesConfig.getString("pweather-set-other", "&aSet %s's personal weather to %s."));
        PWEATHER_RESET = colorize(messagesConfig.getString("pweather-reset", "&aYour personal weather has been reset."));
        PWEATHER_RESET_OTHER = colorize(messagesConfig.getString("pweather-reset-other", "&aReset %s's personal weather."));
        PWEATHER_INVALID = colorize(messagesConfig.getString("pweather-invalid", "&cInvalid weather type. Use: clear, rain, or reset."));

        // Player time messages
        PTIME_SET = colorize(messagesConfig.getString("ptime-set", "&aYour personal time has been set to %s."));
        PTIME_SET_OTHER = colorize(messagesConfig.getString("ptime-set-other", "&aSet %s's personal time to %s."));
        PTIME_RESET = colorize(messagesConfig.getString("ptime-reset", "&aYour personal time has been reset."));
        PTIME_RESET_OTHER = colorize(messagesConfig.getString("ptime-reset-other", "&aReset %s's personal time."));
        PTIME_INVALID = colorize(messagesConfig.getString("ptime-invalid", "&cInvalid time. Use: day, night, noon, midnight, or a number (0-24000), or reset."));

        // World time messages
        TIME_SET_DAY = colorize(messagesConfig.getString("time-set-day", "&aTime set to day."));
        TIME_SET_NIGHT = colorize(messagesConfig.getString("time-set-night", "&aTime set to night."));

        // World weather messages
        WEATHER_SET_CLEAR = colorize(messagesConfig.getString("weather-set-clear", "&aWeather set to clear."));
        WEATHER_SET_RAIN = colorize(messagesConfig.getString("weather-set-rain", "&aWeather set to rain."));
        WEATHER_SET_THUNDER = colorize(messagesConfig.getString("weather-set-thunder", "&aWeather set to thunder."));

        // Warp messages
        WARP_TELEPORTED = colorize(messagesConfig.getString("warp-teleported", "&aTeleported to warp '%s'."));
        WARP_NOT_FOUND = colorize(messagesConfig.getString("warp-not-found", "&cWarp '%s' not found."));
        WARP_INVALID = colorize(messagesConfig.getString("warp-invalid", "&cWarp '%s' is invalid (world not found)."));
        WARP_CREATED = colorize(messagesConfig.getString("warp-created", "&aWarp '%s' created successfully."));
        WARP_DELETED = colorize(messagesConfig.getString("warp-deleted", "&aWarp '%s' deleted successfully."));
        WARP_LIST = colorize(messagesConfig.getString("warp-list", "&aAvailable warps: %s"));
        WARP_LIST_EMPTY = colorize(messagesConfig.getString("warp-list-empty", "&cNo warps available."));
        WARP_USAGE_SET = colorize(messagesConfig.getString("warp-usage-set", "&cUsage: /setwarp <name>"));
        WARP_USAGE_DELETE = colorize(messagesConfig.getString("warp-usage-delete", "&cUsage: /delwarp <name>"));

        // Repair messages
        REPAIR_SUCCESS = colorize(messagesConfig.getString("repair-success", "&aItem repaired successfully."));
        REPAIR_ALL_SUCCESS = colorize(messagesConfig.getString("repair-all-success", "&aRepaired %s items."));
        REPAIR_NO_ITEM = colorize(messagesConfig.getString("repair-no-item", "&cYou must be holding an item to repair."));
        REPAIR_CANNOT_REPAIR = colorize(messagesConfig.getString("repair-cannot-repair", "&cThis item cannot be repaired or is already fully repaired."));
        REPAIR_NOTHING_TO_REPAIR = colorize(messagesConfig.getString("repair-nothing-to-repair", "&cNo items to repair in your inventory."));

        // Enchant messages
        ENCHANT_OPENED = colorize(messagesConfig.getString("enchant-opened", "&aOpened enchanting table."));

        // Freeze messages
        FREEZE_FROZEN = colorize(messagesConfig.getString("freeze-frozen", "&aFroze %s."));
        FREEZE_UNFROZEN = colorize(messagesConfig.getString("freeze-unfrozen", "&aUnfroze %s."));
        FREEZE_FROZEN_TARGET = colorize(messagesConfig.getString("freeze-frozen-target", "&cYou have been frozen!"));
        FREEZE_UNFROZEN_TARGET = colorize(messagesConfig.getString("freeze-unfrozen-target", "&aYou have been unfrozen!"));
        FREEZE_CANNOT_MOVE = colorize(messagesConfig.getString("freeze-cannot-move", "&cYou cannot move while frozen!"));
        FREEZE_USAGE = colorize(messagesConfig.getString("freeze-usage", "&cUsage: /freeze <player>"));

        // Vanish messages
        VANISH_ENABLED = colorize(messagesConfig.getString("vanish-enabled", "&aVanish mode enabled. You are now invisible."));
        VANISH_DISABLED = colorize(messagesConfig.getString("vanish-disabled", "&cVanish mode disabled. You are now visible."));

        // Broadcast messages
        BROADCAST_FORMAT = colorize(messagesConfig.getString("broadcast-format", "&6[Broadcast] &f%message%"));
        BROADCAST_USAGE = colorize(messagesConfig.getString("broadcast-usage", "&cUsage: /broadcast <message>"));
    }

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
