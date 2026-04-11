package ch.framedev.spigotEssentials.utils;

import ch.framedev.spigotEssentials.PaperEssentials;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.FileConfigurationOptions;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

/**
 * Utility class for managing plugin messages with color code support
 * Messages are loaded from messages.yml configuration file
 */
public class MessageConfig {
    private static final LegacyComponentSerializer LEGACY_SERIALIZER = LegacyComponentSerializer.legacyAmpersand();
    private static final String[] EXAMPLE_MESSAGE_RESOURCES = {
            "message-examples/messages_de.yml",
            "message-examples/messages_fr.yml",
            "message-examples/messages_it.yml"
    };

    private static FileConfiguration messagesConfig;
    private static File messagesFile;

    // Permission messages
    public static String NO_PERMISSION_SELF;
    public static String NO_PERMISSION_OTHERS;
    public static String PLAYER_ONLY;
    public static String PLAYER_NOT_FOUND;

    // Command messages
    public static String INVALID_USAGE;
    public static String WORLD_COMMAND_CONSOLE_USAGE;
    public static String WORLD_NOT_FOUND;

    // GameMode messages
    public static String GAMEMODE_CHANGED_SELF;
    public static String GAMEMODE_CHANGED_OTHER;
    public static String GAMEMODE_CHANGED_TARGET;
    public static String GAMEMODE_INVALID;
    public static String GAMEMODE_USAGE;

    // Fly messages
    public static String FLY_ENABLED_SELF;
    public static String FLY_DISABLED_SELF;
    public static String FLY_ENABLED_OTHER;
    public static String FLY_DISABLED_OTHER;
    public static String FLY_ENABLED_TARGET;
    public static String FLY_DISABLED_TARGET;
    public static String FLY_USAGE;

    // Heal messages
    public static String HEAL_SELF;
    public static String HEAL_OTHER;
    public static String HEAL_TARGET;
    public static String HEAL_USAGE;
    public static String HEAL_ATTRIBUTE_MISSING;

    // Feed messages
    public static String FEED_SELF;
    public static String FEED_OTHER;
    public static String FEED_TARGET;
    public static String FEED_USAGE;

    // God mode messages
    public static String GOD_ENABLED_SELF;
    public static String GOD_DISABLED_SELF;
    public static String GOD_ENABLED_OTHER;
    public static String GOD_DISABLED_OTHER;
    public static String GOD_ENABLED_TARGET;
    public static String GOD_DISABLED_TARGET;
    public static String GOD_USAGE;

    // Home messages
    public static String HOME_SET;
    public static String HOME_SET_NAMED;
    public static String HOME_NOT_SET;
    public static String HOME_NOT_SET_NAMED;
    public static String HOME_TELEPORTED;
    public static String HOME_TELEPORTED_NAMED;
    public static String HOME_DELETED;
    public static String HOME_DELETED_NAMED;
    public static String SETHOME_USAGE;
    public static String HOME_USAGE;
    public static String DELHOME_USAGE;
    public static String HOME_SAVE_FAILED;
    public static String HOME_DELETE_FAILED;
    public static String HOME_DELETE_NOT_SET;
    public static String HOME_DELETE_NOT_SET_NAMED;

    // Spawn messages
    public static String SPAWN_SET;
    public static String SPAWN_TELEPORTED;
    public static String SPAWN_NOT_SET;
    public static String SPAWN_TELEPORTED_RESPAWN;
    public static String SPAWN_TELEPORTED_JOIN;
    public static String SPAWN_SAVE_FAILED;

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
    public static String CLEAR_USAGE;

    // Speed command messages
    public static String SPEED_SET;
    public static String SPEED_SET_OTHER;
    public static String SPEED_INVALID;
    public static String SPEED_USAGE;

    // Workbench messages
    public static String WORKBENCH_OPENED;

    // Enderchest messages
    public static String ENDERCHEST_OPENED;
    public static String ENDERCHEST_OPENED_OTHER;
    public static String ENDERCHEST_USAGE;
    public static String BACKPACK_OPENED_SELF;
    public static String BACKPACK_OPENED_OTHER;
    public static String BACKPACK_USAGE;
    public static String BACKPACK_TITLE;
    public static String BACKPACK_LOAD_FAILED;
    public static String BACKPACK_SAVE_FAILED;
    public static String VIRTUAL_STATION_OPENED;
    public static String VIRTUAL_STATION_USAGE;
    public static String TRASH_OPENED;
    public static String TRASH_TITLE;
    public static String TRASH_USAGE;

    // Invsee messages
    public static String INVSEE_OPENED;
    public static String INVSEE_USAGE;

    // Private messaging messages
    public static String MSG_USAGE;
    public static String MSG_SENT;
    public static String MSG_RECEIVED;
    public static String MSG_CANNOT_SELF;
    public static String MSG_TARGET_DISABLED;
    public static String MSG_IGNORED;
    public static String REPLY_USAGE;
    public static String REPLY_NO_TARGET;
    public static String REPLY_PLAYER_OFFLINE;
    public static String IGNORE_USAGE;
    public static String IGNORE_ENABLED;
    public static String IGNORE_DISABLED;
    public static String MSGTOGGLE_USAGE;
    public static String MSGTOGGLE_ENABLED;
    public static String MSGTOGGLE_DISABLED;

    // Suicide command messages
    public static String SUICIDE_SUCCESS;

    // Ping command messages
    public static String PING_SELF;
    public static String PING_OTHER;
    public static String PING_USAGE;

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
    public static String PWEATHER_USAGE;

    // Player time messages
    public static String PTIME_SET;
    public static String PTIME_SET_OTHER;
    public static String PTIME_RESET;
    public static String PTIME_RESET_OTHER;
    public static String PTIME_INVALID;
    public static String PTIME_USAGE;

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
    public static String WARP_INVALID_NAME;
    public static String WARP_SAVE_FAILED;
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

    // Teleport command messages
    public static String TELEPORT_REQUEST_USAGE;

    // Broadcast messages
    public static String BROADCAST_FORMAT;
    public static String BROADCAST_USAGE;
    public static String CHATCLEAR_BROADCAST;
    public static String CHATCLEAR_USAGE;

    // Moderation messages
    public static String DEFAULT_REASON;
    public static String MUTE_USAGE;
    public static String MUTE_SET;
    public static String MUTE_TARGET;
    public static String TEMPMUTE_USAGE;
    public static String TEMPMUTE_SET;
    public static String TEMPMUTE_TARGET;
    public static String UNMUTE_USAGE;
    public static String UNMUTE_SUCCESS;
    public static String UNMUTE_TARGET;
    public static String UNMUTE_NOT_MUTED;
    public static String DURATION_INVALID;
    public static String MUTE_BLOCKED_PERMANENT;
    public static String MUTE_BLOCKED_TEMP;
    public static String CHATMUTE_USAGE;
    public static String CHATMUTE_ENABLED;
    public static String CHATMUTE_DISABLED;
    public static String CHATMUTE_ALREADY_ENABLED;
    public static String CHATMUTE_ALREADY_DISABLED;
    public static String CHATMUTE_BLOCKED;
    public static String STAFFCHAT_TOGGLE_ON;
    public static String STAFFCHAT_TOGGLE_OFF;
    public static String STAFFCHAT_FORMAT;
    public static String STAFFCHAT_USAGE;
    public static String WARN_USAGE;
    public static String WARN_ADDED;
    public static String WARN_TARGET;
    public static String WARNINGS_USAGE;
    public static String WARNINGS_HEADER;
    public static String WARNINGS_ENTRY;
    public static String WARNINGS_NONE;
    public static String MUTEINFO_USAGE;
    public static String MUTEINFO_NONE;
    public static String MUTEINFO_PERMANENT;
    public static String MUTEINFO_TEMP;
    public static String CLEARWARNINGS_USAGE;
    public static String CLEARWARNINGS_NONE;
    public static String CLEARWARNINGS_SUCCESS;
    public static String CLEARWARNINGS_TARGET;
    public static String STAFFLIST_USAGE;
    public static String STAFFLIST_FORMAT;
    public static String STAFFLIST_EMPTY;

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

        exportExampleMessageFiles(plugin);

        messagesConfig = loadYamlConfiguration(messagesFile);
        mergeDefaults(plugin);
        loadMessages();
    }

    /**
     * Reload messages from the messages.yml file
     */
    public static void reload() {
        messagesConfig = loadYamlConfiguration(messagesFile);
        mergeDefaults(PaperEssentials.getInstance());
        loadMessages();
    }

    private static void exportExampleMessageFiles(PaperEssentials plugin) {
        for (String resourcePath : EXAMPLE_MESSAGE_RESOURCES) {
            File targetFile = new File(plugin.getDataFolder(), resourcePath);
            if (targetFile.exists()) {
                continue;
            }

            try {
                plugin.saveResource(resourcePath, false);
            } catch (IllegalArgumentException exception) {
                plugin.getLogger().warning("Could not export bundled message example: " + resourcePath);
            }
        }
    }

    private static void mergeDefaults(PaperEssentials plugin) {
        try (InputStream inputStream = plugin.getResource("messages.yml")) {
            if (inputStream == null) {
                return;
            }

            YamlConfiguration defaultMessages = loadYamlConfiguration(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            if (mergeMissingEntries(messagesConfig, defaultMessages)) {
                messagesConfig.save(messagesFile);
                messagesConfig = loadYamlConfiguration(messagesFile);
                plugin.getLogger().info("Updated messages.yml with new default values.");
            }
        } catch (IOException | InvalidConfigurationException exception) {
            plugin.getLogger().warning("Could not update messages.yml defaults: " + exception.getMessage());
        }
    }

    private static YamlConfiguration loadYamlConfiguration(File file) {
        YamlConfiguration configuration = new YamlConfiguration();
        configuration.options().parseComments(true);

        if (!file.exists()) {
            return configuration;
        }

        try {
            configuration.load(file);
        } catch (IOException | InvalidConfigurationException exception) {
            PaperEssentials.getInstance().getLogger().warning("Could not load " + file.getName() + ": " + exception.getMessage());
        }

        return configuration;
    }

    private static YamlConfiguration loadYamlConfiguration(InputStreamReader reader) throws IOException, InvalidConfigurationException {
        YamlConfiguration configuration = new YamlConfiguration();
        configuration.options().parseComments(true);
        configuration.load(reader);
        return configuration;
    }

    private static boolean mergeMissingEntries(FileConfiguration currentConfig, FileConfiguration defaultConfig) {
        FileConfigurationOptions currentOptions = currentConfig.options();
        FileConfigurationOptions defaultOptions = defaultConfig.options();
        boolean changed = false;

        currentOptions.parseComments(true);
        defaultOptions.parseComments(true);

        if (currentOptions.getHeader().isEmpty() && !defaultOptions.getHeader().isEmpty()) {
            currentOptions.setHeader(defaultOptions.getHeader());
            changed = true;
        }

        if (currentOptions.getFooter().isEmpty() && !defaultOptions.getFooter().isEmpty()) {
            currentOptions.setFooter(defaultOptions.getFooter());
            changed = true;
        }

        return mergeSection(currentConfig, defaultConfig) || changed;
    }

    private static boolean mergeSection(ConfigurationSection currentSection, ConfigurationSection defaultSection) {
        boolean changed = false;

        for (String key : defaultSection.getKeys(false)) {
            Object defaultValue = defaultSection.get(key);
            boolean isDefaultSection = defaultValue instanceof ConfigurationSection;

            if (isDefaultSection) {
                ConfigurationSection currentChild = currentSection.getConfigurationSection(key);
                if (currentChild == null) {
                    currentChild = currentSection.createSection(key);
                    changed = true;
                }

                if (copyCommentsIfMissing(currentSection, defaultSection, key)) {
                    changed = true;
                }

                if (mergeSection(currentChild, (ConfigurationSection) defaultValue)) {
                    changed = true;
                }
                continue;
            }

            if (!currentSection.contains(key, true)) {
                currentSection.set(key, defaultValue);
                changed = true;
            }

            if (copyCommentsIfMissing(currentSection, defaultSection, key)) {
                changed = true;
            }
        }

        return changed;
    }

    private static boolean copyCommentsIfMissing(ConfigurationSection currentSection, ConfigurationSection defaultSection, String key) {
        boolean changed = false;

        if (currentSection.getComments(key).isEmpty() && !defaultSection.getComments(key).isEmpty()) {
            currentSection.setComments(key, defaultSection.getComments(key));
            changed = true;
        }

        if (currentSection.getInlineComments(key).isEmpty() && !defaultSection.getInlineComments(key).isEmpty()) {
            currentSection.setInlineComments(key, defaultSection.getInlineComments(key));
            changed = true;
        }

        return changed;
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
        WORLD_COMMAND_CONSOLE_USAGE = colorize(messagesConfig.getString("world-command-console-usage", "&cUsage from console: /%s <world>"));
        WORLD_NOT_FOUND = colorize(messagesConfig.getString("world-not-found", "&cWorld not found: %s"));

        // GameMode messages
        GAMEMODE_CHANGED_SELF = colorize(messagesConfig.getString("gamemode-changed-self", "&aYour game mode has been changed to %s."));
        GAMEMODE_CHANGED_OTHER = colorize(messagesConfig.getString("gamemode-changed-other", "&a%s's game mode has been changed to %s."));
        GAMEMODE_CHANGED_TARGET = colorize(messagesConfig.getString("gamemode-changed-target", "&aYour game mode has been changed to %s."));
        GAMEMODE_INVALID = colorize(messagesConfig.getString("gamemode-invalid", "&cInvalid game mode. Use survival, creative, adventure, or spectator."));
        GAMEMODE_USAGE = colorize(messagesConfig.getString("gamemode-usage", "&cUsage: /gamemode <mode> [player]"));

        // Fly messages
        FLY_ENABLED_SELF = colorize(messagesConfig.getString("fly-enabled-self", "&aFly mode enabled."));
        FLY_DISABLED_SELF = colorize(messagesConfig.getString("fly-disabled-self", "&cFly mode disabled."));
        FLY_ENABLED_OTHER = colorize(messagesConfig.getString("fly-enabled-other", "&aFly mode enabled for %s."));
        FLY_DISABLED_OTHER = colorize(messagesConfig.getString("fly-disabled-other", "&cFly mode disabled for %s."));
        FLY_ENABLED_TARGET = colorize(messagesConfig.getString("fly-enabled-target", "&aYour fly mode has been enabled."));
        FLY_DISABLED_TARGET = colorize(messagesConfig.getString("fly-disabled-target", "&cYour fly mode has been disabled."));
        FLY_USAGE = colorize(messagesConfig.getString("fly-usage", "&cUsage: /fly [player]"));

        // Heal messages
        HEAL_SELF = colorize(messagesConfig.getString("heal-self", "&aYou have been healed."));
        HEAL_OTHER = colorize(messagesConfig.getString("heal-other", "&aYou have healed %s."));
        HEAL_TARGET = colorize(messagesConfig.getString("heal-target", "&aYou have been healed by %s."));
        HEAL_USAGE = colorize(messagesConfig.getString("heal-usage", "&cUsage: /heal [player]"));
        HEAL_ATTRIBUTE_MISSING = colorize(messagesConfig.getString("heal-attribute-missing", "&cCould not retrieve max health attribute."));

        // Feed messages
        FEED_SELF = colorize(messagesConfig.getString("feed-self", "&aYou have been fed."));
        FEED_OTHER = colorize(messagesConfig.getString("feed-other", "&aYou have fed %s."));
        FEED_TARGET = colorize(messagesConfig.getString("feed-target", "&aYou have been fed by %s."));
        FEED_USAGE = colorize(messagesConfig.getString("feed-usage", "&cUsage: /feed [player]"));

        // God mode messages
        GOD_ENABLED_SELF = colorize(messagesConfig.getString("god-enabled-self", "&aGod mode enabled."));
        GOD_DISABLED_SELF = colorize(messagesConfig.getString("god-disabled-self", "&cGod mode disabled."));
        GOD_ENABLED_OTHER = colorize(messagesConfig.getString("god-enabled-other", "&aGod mode enabled for %s."));
        GOD_DISABLED_OTHER = colorize(messagesConfig.getString("god-disabled-other", "&cGod mode disabled for %s."));
        GOD_ENABLED_TARGET = colorize(messagesConfig.getString("god-enabled-target", "&aYour god mode has been enabled."));
        GOD_DISABLED_TARGET = colorize(messagesConfig.getString("god-disabled-target", "&cYour god mode has been disabled."));
        GOD_USAGE = colorize(messagesConfig.getString("god-usage", "&cUsage: /god [player]"));

        // Home messages
        HOME_SET = colorize(messagesConfig.getString("home-set", "&aHome set successfully."));
        HOME_SET_NAMED = colorize(messagesConfig.getString("home-set-named", "&aHome '%s' set successfully."));
        HOME_NOT_SET = colorize(messagesConfig.getString("home-not-set", "&cHome not set. Use /sethome to set your home location."));
        HOME_NOT_SET_NAMED = colorize(messagesConfig.getString("home-not-set-named", "&cHome '%s' not set. Use /sethome %s to set your home location."));
        HOME_TELEPORTED = colorize(messagesConfig.getString("home-teleported", "&aTeleported to home."));
        HOME_TELEPORTED_NAMED = colorize(messagesConfig.getString("home-teleported-named", "&aTeleported to home '%s'."));
        HOME_DELETED = colorize(messagesConfig.getString("home-deleted", "&aHome deleted successfully."));
        HOME_DELETED_NAMED = colorize(messagesConfig.getString("home-deleted-named", "&aHome '%s' deleted successfully."));
        SETHOME_USAGE = colorize(messagesConfig.getString("sethome-usage", "&cUsage: /sethome [name]"));
        HOME_USAGE = colorize(messagesConfig.getString("home-usage", "&cUsage: /home [name]"));
        DELHOME_USAGE = colorize(messagesConfig.getString("delhome-usage", "&cUsage: /delhome [name]"));
        HOME_SAVE_FAILED = colorize(messagesConfig.getString("home-save-failed", "&cFailed to save home location."));
        HOME_DELETE_FAILED = colorize(messagesConfig.getString("home-delete-failed", "&cFailed to delete home location."));
        HOME_DELETE_NOT_SET = colorize(messagesConfig.getString("home-delete-not-set", "&cHome not set."));
        HOME_DELETE_NOT_SET_NAMED = colorize(messagesConfig.getString("home-delete-not-set-named", "&cHome '%s' not set."));

        // Spawn messages
        SPAWN_SET = colorize(messagesConfig.getString("spawn-set", "&aSpawn point set!"));
        SPAWN_TELEPORTED = colorize(messagesConfig.getString("spawn-teleported", "&aTeleported to spawn point!"));
        SPAWN_NOT_SET = colorize(messagesConfig.getString("spawn-not-set", "&cSpawn point is not set."));
        SPAWN_TELEPORTED_RESPAWN = colorize(messagesConfig.getString("spawn-teleported-respawn", "&aTeleported to spawn point on respawn!"));
        SPAWN_TELEPORTED_JOIN = colorize(messagesConfig.getString("spawn-teleported-join", "&aTeleported to spawn point on join!"));
        SPAWN_SAVE_FAILED = colorize(messagesConfig.getString("spawn-save-failed", "&cFailed to save spawn location."));

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
        CLEAR_USAGE = colorize(messagesConfig.getString("clear-usage", "&cUsage: /clear [player]"));

        // Speed command messages
        SPEED_SET = colorize(messagesConfig.getString("speed-set", "&aYour %s speed has been set to %s."));
        SPEED_SET_OTHER = colorize(messagesConfig.getString("speed-set-other", "&aSet %s's %s speed to %s."));
        SPEED_INVALID = colorize(messagesConfig.getString("speed-invalid", "&cSpeed must be a number between 0 and 10."));
        SPEED_USAGE = colorize(messagesConfig.getString("speed-usage", "&cUsage: /speed <walk|fly> <0-10> [player]"));

        // Workbench messages
        WORKBENCH_OPENED = colorize(messagesConfig.getString("workbench-opened", "&aOpened crafting table."));

        // Enderchest messages
        ENDERCHEST_OPENED = colorize(messagesConfig.getString("enderchest-opened", "&aOpened ender chest."));
        ENDERCHEST_OPENED_OTHER = colorize(messagesConfig.getString("enderchest-opened-other", "&aOpened %s's ender chest."));
        ENDERCHEST_USAGE = colorize(messagesConfig.getString("enderchest-usage", "&cUsage: /enderchest [player]"));
        BACKPACK_OPENED_SELF = colorize(messagesConfig.getString("backpack-opened-self", "&aOpened your backpack."));
        BACKPACK_OPENED_OTHER = colorize(messagesConfig.getString("backpack-opened-other", "&aOpened %s's backpack."));
        BACKPACK_USAGE = colorize(messagesConfig.getString("backpack-usage", "&cUsage: /backpack [player]"));
        BACKPACK_TITLE = colorize(messagesConfig.getString("backpack-title", "&8Backpack: %s"));
        BACKPACK_LOAD_FAILED = colorize(messagesConfig.getString("backpack-load-failed", "&cCould not load backpack data."));
        BACKPACK_SAVE_FAILED = colorize(messagesConfig.getString("backpack-save-failed", "&cCould not save backpack data."));
        VIRTUAL_STATION_OPENED = colorize(messagesConfig.getString("virtual-station-opened", "&aOpened %s."));
        VIRTUAL_STATION_USAGE = colorize(messagesConfig.getString("virtual-station-usage", "&cUsage: /%s"));
        TRASH_OPENED = colorize(messagesConfig.getString("trash-opened", "&aOpened trash inventory."));
        TRASH_TITLE = colorize(messagesConfig.getString("trash-title", "&8Trash"));
        TRASH_USAGE = colorize(messagesConfig.getString("trash-usage", "&cUsage: /trash"));

        // Invsee messages
        INVSEE_OPENED = colorize(messagesConfig.getString("invsee-opened", "&aOpened %s's inventory."));
        INVSEE_USAGE = colorize(messagesConfig.getString("invsee-usage", "&cUsage: /invsee <player>"));

        // Private messaging messages
        MSG_USAGE = colorize(messagesConfig.getString("msg-usage", "&cUsage: /msg <player> <message>"));
        MSG_SENT = colorize(messagesConfig.getString("msg-sent", "&8[&dMe &7-> &d%s&8] &f%s"));
        MSG_RECEIVED = colorize(messagesConfig.getString("msg-received", "&8[&d%s &7-> &dMe&8] &f%s"));
        MSG_CANNOT_SELF = colorize(messagesConfig.getString("msg-cannot-self", "&cYou cannot message yourself."));
        MSG_TARGET_DISABLED = colorize(messagesConfig.getString("msg-target-disabled", "&c%s is not accepting private messages."));
        MSG_IGNORED = colorize(messagesConfig.getString("msg-ignored", "&c%s is ignoring you."));
        REPLY_USAGE = colorize(messagesConfig.getString("reply-usage", "&cUsage: /reply <message>"));
        REPLY_NO_TARGET = colorize(messagesConfig.getString("reply-no-target", "&cYou have nobody to reply to."));
        REPLY_PLAYER_OFFLINE = colorize(messagesConfig.getString("reply-player-offline", "&cThat player is no longer online."));
        IGNORE_USAGE = colorize(messagesConfig.getString("ignore-usage", "&cUsage: /ignore <player>"));
        IGNORE_ENABLED = colorize(messagesConfig.getString("ignore-enabled", "&aYou are now ignoring %s."));
        IGNORE_DISABLED = colorize(messagesConfig.getString("ignore-disabled", "&aYou are no longer ignoring %s."));
        MSGTOGGLE_USAGE = colorize(messagesConfig.getString("msgtoggle-usage", "&cUsage: /msgtoggle"));
        MSGTOGGLE_ENABLED = colorize(messagesConfig.getString("msgtoggle-enabled", "&cYou are no longer accepting private messages."));
        MSGTOGGLE_DISABLED = colorize(messagesConfig.getString("msgtoggle-disabled", "&aYou are now accepting private messages."));

        // Suicide command messages
        SUICIDE_SUCCESS = colorize(messagesConfig.getString("suicide-success", "&cYou have committed suicide."));

        // Ping command messages
        PING_SELF = colorize(messagesConfig.getString("ping-self", "&aYour ping: %sms"));
        PING_OTHER = colorize(messagesConfig.getString("ping-other", "&a%s's ping: %sms"));
        PING_USAGE = colorize(messagesConfig.getString("ping-usage", "&cUsage: /ping [player]"));

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
        PWEATHER_USAGE = colorize(messagesConfig.getString("pweather-usage", "&cUsage: /playerweather <clear|rain|reset> [player]"));

        // Player time messages
        PTIME_SET = colorize(messagesConfig.getString("ptime-set", "&aYour personal time has been set to %s."));
        PTIME_SET_OTHER = colorize(messagesConfig.getString("ptime-set-other", "&aSet %s's personal time to %s."));
        PTIME_RESET = colorize(messagesConfig.getString("ptime-reset", "&aYour personal time has been reset."));
        PTIME_RESET_OTHER = colorize(messagesConfig.getString("ptime-reset-other", "&aReset %s's personal time."));
        PTIME_INVALID = colorize(messagesConfig.getString("ptime-invalid", "&cInvalid time. Use: day, night, noon, midnight, or a number (0-24000), or reset."));
        PTIME_USAGE = colorize(messagesConfig.getString("ptime-usage", "&cUsage: /playertime <time|reset> [player]"));

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
        WARP_INVALID_NAME = colorize(messagesConfig.getString("warp-invalid-name", "&cWarp name '%s' is invalid. Use only letters, numbers, hyphens, and underscores."));
        WARP_SAVE_FAILED = colorize(messagesConfig.getString("warp-save-failed", "&cCould not save warp data."));
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

        // Teleport command messages
        TELEPORT_REQUEST_USAGE = colorize(messagesConfig.getString("teleport-request-usage", "&cUsage: /%s <player>"));

        // Broadcast messages
        BROADCAST_FORMAT = colorize(messagesConfig.getString("broadcast-format", "&6[Broadcast] &f%message%"));
        BROADCAST_USAGE = colorize(messagesConfig.getString("broadcast-usage", "&cUsage: /broadcast <message>"));
        CHATCLEAR_BROADCAST = colorize(messagesConfig.getString("chatclear-broadcast", "&7Chat was cleared by %s."));
        CHATCLEAR_USAGE = colorize(messagesConfig.getString("chatclear-usage", "&cUsage: /chatclear"));

        // Moderation messages
        DEFAULT_REASON = colorize(messagesConfig.getString("default-reason", "No reason provided."));
        MUTE_USAGE = colorize(messagesConfig.getString("mute-usage", "&cUsage: /mute <player> [reason]"));
        MUTE_SET = colorize(messagesConfig.getString("mute-set", "&aMuted %s. Reason: %s"));
        MUTE_TARGET = colorize(messagesConfig.getString("mute-target", "&cYou have been muted. Reason: %s"));
        TEMPMUTE_USAGE = colorize(messagesConfig.getString("tempmute-usage", "&cUsage: /tempmute <player> <duration> [reason]"));
        TEMPMUTE_SET = colorize(messagesConfig.getString("tempmute-set", "&aMuted %s for %s. Reason: %s"));
        TEMPMUTE_TARGET = colorize(messagesConfig.getString("tempmute-target", "&cYou have been muted for %s. Reason: %s"));
        UNMUTE_USAGE = colorize(messagesConfig.getString("unmute-usage", "&cUsage: /unmute <player>"));
        UNMUTE_SUCCESS = colorize(messagesConfig.getString("unmute-success", "&aUnmuted %s."));
        UNMUTE_TARGET = colorize(messagesConfig.getString("unmute-target", "&aYou have been unmuted."));
        UNMUTE_NOT_MUTED = colorize(messagesConfig.getString("unmute-not-muted", "&c%s is not currently muted."));
        DURATION_INVALID = colorize(messagesConfig.getString("duration-invalid", "&cInvalid duration. Use formats like 30m, 2h, 1d, or 1w."));
        MUTE_BLOCKED_PERMANENT = colorize(messagesConfig.getString("mute-blocked-permanent", "&cYou are muted. Reason: %s"));
        MUTE_BLOCKED_TEMP = colorize(messagesConfig.getString("mute-blocked-temp", "&cYou are muted for %s. Reason: %s"));
        CHATMUTE_USAGE = colorize(messagesConfig.getString("chatmute-usage", "&cUsage: /chatmute [on|off]"));
        CHATMUTE_ENABLED = colorize(messagesConfig.getString("chatmute-enabled", "&cGlobal chat has been muted by %s."));
        CHATMUTE_DISABLED = colorize(messagesConfig.getString("chatmute-disabled", "&aGlobal chat has been unmuted by %s."));
        CHATMUTE_ALREADY_ENABLED = colorize(messagesConfig.getString("chatmute-already-enabled", "&cGlobal chat is already muted."));
        CHATMUTE_ALREADY_DISABLED = colorize(messagesConfig.getString("chatmute-already-disabled", "&cGlobal chat is already unmuted."));
        CHATMUTE_BLOCKED = colorize(messagesConfig.getString("chatmute-blocked", "&cChat is currently muted by staff."));
        STAFFCHAT_TOGGLE_ON = colorize(messagesConfig.getString("staffchat-toggle-on", "&aStaff chat mode enabled."));
        STAFFCHAT_TOGGLE_OFF = colorize(messagesConfig.getString("staffchat-toggle-off", "&cStaff chat mode disabled."));
        STAFFCHAT_FORMAT = colorize(messagesConfig.getString("staffchat-format", "&8[StaffChat] &b%s&8: &f%s"));
        STAFFCHAT_USAGE = colorize(messagesConfig.getString("staffchat-usage", "&cUsage: /staffchat <message>"));
        WARN_USAGE = colorize(messagesConfig.getString("warn-usage", "&cUsage: /warn <player> [reason]"));
        WARN_ADDED = colorize(messagesConfig.getString("warn-added", "&eAdded a warning to %s. Reason: %s"));
        WARN_TARGET = colorize(messagesConfig.getString("warn-target", "&eYou have received a warning. Reason: %s"));
        WARNINGS_USAGE = colorize(messagesConfig.getString("warnings-usage", "&cUsage: /warnings <player>"));
        WARNINGS_HEADER = colorize(messagesConfig.getString("warnings-header", "&6Warnings for %s &8(%s)&6:"));
        WARNINGS_ENTRY = colorize(messagesConfig.getString("warnings-entry", "&8#%s &7%s &8- &f%s &8(by %s)"));
        WARNINGS_NONE = colorize(messagesConfig.getString("warnings-none", "&a%s has no warnings."));
        MUTEINFO_USAGE = colorize(messagesConfig.getString("muteinfo-usage", "&cUsage: /muteinfo <player>"));
        MUTEINFO_NONE = colorize(messagesConfig.getString("muteinfo-none", "&a%s is not currently muted."));
        MUTEINFO_PERMANENT = colorize(messagesConfig.getString("muteinfo-permanent", "&6Mute info for %s: &fPermanent &8| &7Reason: &f%s &8| &7By: &f%s &8| &7At: &f%s"));
        MUTEINFO_TEMP = colorize(messagesConfig.getString("muteinfo-temp", "&6Mute info for %s: &fTemporary &8| &7Remaining: &f%s &8| &7Reason: &f%s &8| &7By: &f%s &8| &7At: &f%s"));
        CLEARWARNINGS_USAGE = colorize(messagesConfig.getString("clearwarnings-usage", "&cUsage: /clearwarnings <player>"));
        CLEARWARNINGS_NONE = colorize(messagesConfig.getString("clearwarnings-none", "&a%s has no warnings to clear."));
        CLEARWARNINGS_SUCCESS = colorize(messagesConfig.getString("clearwarnings-success", "&aCleared %s warning(s) for %s."));
        CLEARWARNINGS_TARGET = colorize(messagesConfig.getString("clearwarnings-target", "&e%s cleared %s of your warning(s)."));
        STAFFLIST_USAGE = colorize(messagesConfig.getString("stafflist-usage", "&cUsage: /stafflist"));
        STAFFLIST_FORMAT = colorize(messagesConfig.getString("stafflist-format", "&6Online staff &8(%s)&6: &f%s"));
        STAFFLIST_EMPTY = colorize(messagesConfig.getString("stafflist-empty", "&eNo staff members are currently visible online."));
    }

    /**
     * Translate color codes in a message
     * @param message The message to translate
     * @return The message with color codes translated
     */
    public static String colorize(String message) {
        return message;
    }

    /**
     * Convert a legacy-formatted message into an Adventure component
     * @param message The message to convert
     * @param args Arguments to format the message with
     * @return The converted component
     */
    public static Component component(String message, Object... args) {
        if (message == null) return Component.empty();
        String formattedMessage = format(message, args);
        return LEGACY_SERIALIZER.deserialize(formattedMessage.replace('§', '&'));
    }

    /**
     * Send a formatted message to a command sender
     * @param sender The command sender
     * @param message The message to send
     * @param args Arguments to format the message with
     */
    public static void send(CommandSender sender, String message, Object... args) {
        if (sender == null || message == null) return;
        sender.sendMessage(component(message, args));
    }

    /**
     * Broadcast a formatted message to the whole server
     * @param message The message to broadcast
     * @param args Arguments to format the message with
     */
    public static void broadcast(String message, Object... args) {
        if (message == null) return;
        Bukkit.broadcast(component(message, args));
    }

    /**
     * Format a message with arguments
     * @param message The message template
     * @param args Arguments to format the message with
     * @return The formatted message
     */
    public static String format(String message, Object... args) {
        if (message == null) return null;
        if (args == null || args.length == 0) {
            return message;
        }
        return String.format(message, args);
    }
}
