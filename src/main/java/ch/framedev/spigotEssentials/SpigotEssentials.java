package ch.framedev.spigotEssentials;

import ch.framedev.spigotEssentials.commands.*;
import ch.framedev.spigotEssentials.listeners.PlayerListeners;
import org.bukkit.plugin.java.JavaPlugin;

@SuppressWarnings("DataFlowIssue")
public final class SpigotEssentials extends JavaPlugin {

    // Singleton instance
    private static SpigotEssentials instance;

    @Override
    public void onEnable() {
        instance = this;

        getConfig().options().copyDefaults(true);
        saveDefaultConfig();

        registerCommands();
        registerListeners();

        getLogger().info("SpigotEssentials has been enabled.");
    }

    private void registerCommands() {
        getLogger().info("Registering commands...");

        // Simple commands
        GameModeCommand gameModeCommand = new GameModeCommand();
        registerCommand("gamemode", gameModeCommand, gameModeCommand);
        registerCommand("fly", new FlyCommand());
        registerCommand("heal", new HealCommand());
        registerCommand("feed", new FeedCommand());

        // Home commands
        HomeCommand homeCommand = new HomeCommand();
        registerCommand("sethome", homeCommand);
        registerCommand("home", homeCommand);
        registerCommand("delhome", homeCommand);

        // Teleport commands
        TeleportCommand teleportCommand = new TeleportCommand();
        registerCommand("tpa", teleportCommand);
        registerCommand("tpaaccept", teleportCommand);
        registerCommand("tpadeny", teleportCommand);
        registerCommand("tpahere", teleportCommand);
        registerCommand("tpahereaccept", teleportCommand);
        registerCommand("tpaheredeny", teleportCommand);
        getServer().getPluginManager().registerEvents(teleportCommand, this);

        // Spawn commands
        SpawnCommand spawnCommand = new SpawnCommand();
        registerCommand("setspawn", spawnCommand);
        registerCommand("spawn", spawnCommand);
        getServer().getPluginManager().registerEvents(spawnCommand, this);

        // Back command
        BackCommand backCommand = new BackCommand(this);
        registerCommand("back", backCommand);
        getServer().getPluginManager().registerEvents(backCommand, this);

        getLogger().info("Commands registered.");
    }

    private void registerListeners() {
        getLogger().info("Registering listeners...");

        PlayerListeners playerListeners = new PlayerListeners(this);
        getServer().getPluginManager().registerEvents(playerListeners, this);

        getLogger().info("Listeners registered.");
    }

    private void registerCommand(String name, Object executor) {
        registerCommand(name, executor, null);
    }

    private void registerCommand(String name, Object executor, Object tabCompleter) {
        var cmd = getCommand(name);
        cmd.setExecutor((org.bukkit.command.CommandExecutor) executor);
        if (tabCompleter != null) {
            cmd.setTabCompleter((org.bukkit.command.TabCompleter) tabCompleter);
        }
    }

    @Override
    public void onDisable() {
        getLogger().info("SpigotEssentials has been disabled.");
    }

    /**
     * Get the instance of the main class
     * @return SpigotEssentials instance
     */
    public static SpigotEssentials getInstance() {
        return instance;
    }
}
