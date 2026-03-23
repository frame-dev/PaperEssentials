package ch.framedev.spigotEssentials;

import ch.framedev.spigotEssentials.listeners.ChatModerationListener;
import ch.framedev.spigotEssentials.listeners.PlayerListeners;
import ch.framedev.spigotEssentials.managers.CommandManager;
import ch.framedev.spigotEssentials.managers.ModerationManager;
import ch.framedev.spigotEssentials.utils.MessageConfig;
import org.bukkit.plugin.java.JavaPlugin;

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
