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
    private final TabCompleter sharedTabCompleter;

    public CommandManager(PaperEssentials plugin) {
        this.plugin = plugin;
        this.sharedTabCompleter = new CommandTabCompleter(plugin);
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
        BackpackCommand backpackCommand = new BackpackCommand(plugin);
        registerCommand("backpack", backpackCommand);
        plugin.getServer().getPluginManager().registerEvents(backpackCommand, plugin);
        VirtualStationCommand virtualStationCommand = new VirtualStationCommand();
        registerCommand("anvil", virtualStationCommand);
        registerCommand("grindstone", virtualStationCommand);
        registerCommand("loom", virtualStationCommand);
        registerCommand("stonecutter", virtualStationCommand);
        registerCommand("cartographytable", virtualStationCommand);
        registerCommand("smithingtable", virtualStationCommand);
        registerCommand("trash", new TrashCommand());
        registerCommand("suicide", new SuicideCommand());
        registerCommand("ping", new PingCommand());
        registerCommand("hat", new HatCommand());
        registerCommand("invsee", new InvseeCommand());
        registerCommand("playerweather", new PlayerWeatherCommand());
        registerCommand("playertime", new PlayerTimeCommand());

        // Private messaging commands
        PrivateMessageCommand privateMessageCommand = new PrivateMessageCommand();
        registerCommand("msg", privateMessageCommand);
        registerCommand("reply", privateMessageCommand);
        registerCommand("ignore", privateMessageCommand);
        registerCommand("msgtoggle", privateMessageCommand);
        plugin.getServer().getPluginManager().registerEvents(privateMessageCommand, plugin);

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

        // Warp commands
        WarpCommand warpCommand = new WarpCommand(plugin);
        registerCommand("warp", warpCommand, warpCommand);
        registerCommand("setwarp", warpCommand, warpCommand);
        registerCommand("delwarp", warpCommand, warpCommand);

        // Item commands
        registerCommand("repair", new RepairCommand());
        EnchantCommand enchantCommand = new EnchantCommand();
        registerCommand("enchantingtable", enchantCommand);
        plugin.getServer().getPluginManager().registerEvents(enchantCommand, plugin);

        // Player management commands
        FreezeCommand freezeCommand = new FreezeCommand();
        registerCommand("freeze", freezeCommand);
        plugin.getServer().getPluginManager().registerEvents(freezeCommand, plugin);

        VanishCommand vanishCommand = new VanishCommand();
        registerCommand("vanish", vanishCommand);
        plugin.getServer().getPluginManager().registerEvents(vanishCommand, plugin);

        // Server commands
        registerCommand("broadcast", new BroadcastCommand());
        registerCommand("chatclear", new ChatClearCommand());

        // Moderation commands
        MuteCommand muteCommand = new MuteCommand(plugin);
        registerCommand("mute", muteCommand);
        registerCommand("tempmute", muteCommand);
        registerCommand("unmute", muteCommand);
        registerCommand("chatmute", new ChatMuteCommand(plugin));
        registerCommand("staffchat", new StaffChatCommand(plugin));

        WarningCommand warningCommand = new WarningCommand(plugin);
        registerCommand("warn", warningCommand);
        registerCommand("warnings", warningCommand);

        ModerationUtilityCommand moderationUtilityCommand = new ModerationUtilityCommand(plugin);
        registerCommand("muteinfo", moderationUtilityCommand);
        registerCommand("clearwarnings", moderationUtilityCommand);
        registerCommand("stafflist", moderationUtilityCommand);

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
        command.setTabCompleter(tabCompleter != null ? tabCompleter : sharedTabCompleter);
    }
}
