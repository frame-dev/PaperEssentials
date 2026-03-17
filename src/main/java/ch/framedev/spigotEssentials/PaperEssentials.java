package ch.framedev.spigotEssentials;

import ch.framedev.spigotEssentials.listeners.PlayerListeners;
import ch.framedev.spigotEssentials.managers.CommandManager;
import ch.framedev.spigotEssentials.utils.MessageConfig;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Main plugin class for PaperEssentials
 * A comprehensive essentials plugin for Paper/Spigot servers
 */
public final class PaperEssentials extends JavaPlugin {

    private static PaperEssentials instance;
    private CommandManager commandManager;

    @Override
    public void onEnable() {
        instance = this;

        // Initialize configuration
        initializeConfig();

        // Load messages from messages.yml
        MessageConfig.load();

        // Register managers
        this.commandManager = new CommandManager(this);
        commandManager.registerCommands();

        // Register listeners
        registerListeners();

        getLogger().info("PaperEssentials has been enabled successfully.");
    }

    @Override
    public void onDisable() {
        getLogger().info("PaperEssentials has been disabled.");
        instance = null;
    }

    /**
     * Initialize the plugin configuration
     */
    private void initializeConfig() {
        getConfig().options().copyDefaults(true);
        saveDefaultConfig();
    }

    /**
     * Register all event listeners
     */
    private void registerListeners() {
        getLogger().info("Registering listeners...");

        PlayerListeners playerListeners = new PlayerListeners(this);
        getServer().getPluginManager().registerEvents(playerListeners, this);

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
}
