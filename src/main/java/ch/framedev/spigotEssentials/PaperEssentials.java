package ch.framedev.spigotEssentials;

import ch.framedev.spigotEssentials.listeners.ChatModerationListener;
import ch.framedev.spigotEssentials.listeners.PlayerListeners;
import ch.framedev.spigotEssentials.managers.CommandManager;
import ch.framedev.spigotEssentials.managers.ModerationManager;
import ch.framedev.spigotEssentials.utils.MessageConfig;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.FileConfigurationOptions;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * Main plugin class for PaperEssentials
 * A comprehensive essentials plugin for Paper/Spigot servers
 */
public final class PaperEssentials extends JavaPlugin {

    private static PaperEssentials instance;
    private CommandManager commandManager;
    private ModerationManager moderationManager;

    @Override
    public void onEnable() {
        instance = this;

        // Initialize configuration
        initializeConfig();

        // Load messages from messages.yml
        MessageConfig.load();

        // Initialize moderation state
        this.moderationManager = new ModerationManager(this);

        // Register managers
        this.commandManager = new CommandManager(this);
        commandManager.registerCommands();

        // Register listeners
        registerListeners();

        getLogger().info("PaperEssentials has been enabled successfully.");
    }

    @Override
    public void onDisable() {
        if (moderationManager != null) {
            moderationManager.shutdown();
        }
        getLogger().info("PaperEssentials has been disabled.");
        instance = null;
    }

    /**
     * Initialize the plugin configuration
     */
    private void initializeConfig() {
        saveDefaultConfig();
        File configFile = new File(getDataFolder(), "config.yml");
        if (mergeBundledConfigDefaults(configFile, "config.yml")) {
            getLogger().info("Updated config.yml with new default values.");
        }
        reloadConfig();
    }

    private boolean mergeBundledConfigDefaults(File file, String resourcePath) {
        try (InputStream inputStream = getResource(resourcePath)) {
            if (inputStream == null) {
                return false;
            }

            YamlConfiguration currentConfig = loadYamlConfiguration(file);
            YamlConfiguration defaultConfig = loadYamlConfiguration(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            boolean changed = mergeMissingEntries(currentConfig, defaultConfig);

            if (changed) {
                currentConfig.save(file);
            }

            return changed;
        } catch (IOException | InvalidConfigurationException exception) {
            getLogger().warning("Could not update " + resourcePath + " defaults: " + exception.getMessage());
            return false;
        }
    }

    private YamlConfiguration loadYamlConfiguration(File file) throws IOException, InvalidConfigurationException {
        YamlConfiguration configuration = new YamlConfiguration();
        configuration.options().parseComments(true);
        if (file.exists()) {
            configuration.load(file);
        }
        return configuration;
    }

    private YamlConfiguration loadYamlConfiguration(InputStreamReader reader) throws IOException, InvalidConfigurationException {
        YamlConfiguration configuration = new YamlConfiguration();
        configuration.options().parseComments(true);
        configuration.load(reader);
        return configuration;
    }

    private boolean mergeMissingEntries(FileConfiguration currentConfig, FileConfiguration defaultConfig) {
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

    private boolean mergeSection(ConfigurationSection currentSection, ConfigurationSection defaultSection) {
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

    private boolean copyCommentsIfMissing(ConfigurationSection currentSection, ConfigurationSection defaultSection, String key) {
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
     * Register all event listeners
     */
    private void registerListeners() {
        getLogger().info("Registering listeners...");

        PlayerListeners playerListeners = new PlayerListeners(this);
        getServer().getPluginManager().registerEvents(playerListeners, this);
        getServer().getPluginManager().registerEvents(new ChatModerationListener(this), this);

        getLogger().info("Listeners registered successfully.");
    }

    /**
     * Get the plugin instance
     * @return The plugin instance
     */
    public static PaperEssentials getInstance() {
        return instance;
    }

    /**
     * Get the command manager
     * @return The command manager
     */
    public CommandManager getCommandManager() {
        return commandManager;
    }

    /**
     * Get the moderation manager
     * @return The moderation manager
     */
    public ModerationManager getModerationManager() {
        return moderationManager;
    }
}
