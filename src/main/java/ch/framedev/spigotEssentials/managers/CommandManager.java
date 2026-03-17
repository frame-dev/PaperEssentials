package ch.framedev.spigotEssentials.managers;

import ch.framedev.spigotEssentials.PaperEssentials;
import ch.framedev.spigotEssentials.commands.*;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;

/**
 * Manages command registration for the plugin
 */
public class CommandManager {

    private final PaperEssentials plugin;

    public CommandManager(PaperEssentials plugin) {
        this.plugin = plugin;
    }

    /**
     * Register all plugin commands
     */
    public void registerCommands() {
        plugin.getLogger().info("Registering commands...");

        // Simple commands
        GameModeCommand gameModeCommand = new GameModeCommand();
        registerCommand("gamemode", gameModeCommand, gameModeCommand);
        registerCommand("fly", new FlyCommand());
        registerCommand("heal", new HealCommand());
        registerCommand("feed", new FeedCommand());
        registerCommand("god", new GodModeCommand());

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
        plugin.getServer().getPluginManager().registerEvents(teleportCommand, plugin);

        // Spawn commands
        SpawnCommand spawnCommand = new SpawnCommand();
        registerCommand("setspawn", spawnCommand);
        registerCommand("spawn", spawnCommand);
        plugin.getServer().getPluginManager().registerEvents(spawnCommand, plugin);

        // Back command
        BackCommand backCommand = new BackCommand(plugin);
        registerCommand("back", backCommand);
        plugin.getServer().getPluginManager().registerEvents(backCommand, plugin);

        // Utility commands
        registerCommand("clear", new ClearCommand());
        registerCommand("speed", new SpeedCommand());
        registerCommand("workbench", new WorkbenchCommand());
        registerCommand("enderchest", new EnderChestCommand());
        registerCommand("suicide", new SuicideCommand());
        registerCommand("ping", new PingCommand());
        registerCommand("hat", new HatCommand());
        registerCommand("invsee", new InvseeCommand());
        registerCommand("playerweather", new PlayerWeatherCommand());
        registerCommand("playertime", new PlayerTimeCommand());

        // AFK command with listener
        AfkCommand afkCommand = new AfkCommand(plugin);
        registerCommand("afk", afkCommand);
        plugin.getServer().getPluginManager().registerEvents(afkCommand, plugin);

        // World management commands
        registerCommand("day", new DayCommand());
        registerCommand("night", new NightCommand());
        registerCommand("sun", new SunCommand());
        registerCommand("rain", new RainCommand());
        registerCommand("thunder", new ThunderCommand());

        plugin.getLogger().info("Commands registered successfully.");
    }

    /**
     * Register a command with an executor
     * @param name The command name
     * @param executor The command executor
     */
    private void registerCommand(String name, CommandExecutor executor) {
        registerCommand(name, executor, null);
    }

    /**
     * Register a command with an executor and tab completer
     * @param name The command name
     * @param executor The command executor
     * @param tabCompleter The tab completer (can be null)
     */
    private void registerCommand(String name, CommandExecutor executor, TabCompleter tabCompleter) {
        PluginCommand command = plugin.getCommand(name);
        if (command == null) {
            plugin.getLogger().warning("Command '" + name + "' not found in plugin.yml!");
            return;
        }
        
        command.setExecutor(executor);
        if (tabCompleter != null) {
            command.setTabCompleter(tabCompleter);
        }
    }
}
