package ch.framedev.spigotEssentials.commands;

import ch.framedev.spigotEssentials.PaperEssentials;
import ch.framedev.spigotEssentials.utils.MessageConfig;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BackpackCommand extends AbstractCommand implements Listener {
    private static final String BACKPACK_PERMISSION = "spigotessentials.backpack";
    private static final String BACKPACK_PERMISSION_OTHERS = "spigotessentials.backpack.others";
    private static final int DEFAULT_BACKPACK_SIZE = 27;
    private static final int MIN_BACKPACK_SIZE = 9;
    private static final int MAX_BACKPACK_SIZE = 54;

    private final PaperEssentials plugin;
    private final File backpacksFolder;
    private final int configuredBackpackSize;
    private final Map<UUID, Inventory> openBackpacks = new HashMap<>();

    public BackpackCommand(PaperEssentials plugin) {
        this.plugin = plugin;
        this.backpacksFolder = new File(plugin.getDataFolder(), "backpacks");
        this.configuredBackpackSize = resolveConfiguredBackpackSize();
        if (!backpacksFolder.exists() && !backpacksFolder.mkdirs()) {
            plugin.getLogger().warning("Could not create backpacks folder.");
        }
    }

    @Override
    protected boolean execute(CommandSender sender, Command command, String label, String[] args) {
        Player viewer = asPlayer(sender);
        if (viewer == null) {
            sendMessage(sender, MessageConfig.PLAYER_ONLY);
            return true;
        }

        if (args.length == 0) {
            if (!checkPermission(sender, BACKPACK_PERMISSION, MessageConfig.NO_PERMISSION_SELF)) {
                return true;
            }
            openBackpack(viewer, viewer);
            return true;
        }

        if (args.length == 1) {
            if (!checkPermission(sender, BACKPACK_PERMISSION_OTHERS, MessageConfig.NO_PERMISSION_OTHERS)) {
                return true;
            }

            Player target = getPlayer(sender, args[0]);
            if (target == null) {
                return true;
            }

            openBackpack(viewer, target);
            return true;
        }

        sendMessage(sender, MessageConfig.BACKPACK_USAGE);
        return false;
    }

    private void openBackpack(Player viewer, Player owner) {
        Inventory backpack = openBackpacks.get(owner.getUniqueId());
        if (backpack == null) {
            backpack = createBackpackInventory(owner, viewer);
            openBackpacks.put(owner.getUniqueId(), backpack);
        }

        viewer.openInventory(backpack);

        if (viewer.getUniqueId().equals(owner.getUniqueId())) {
            sendMessage(viewer, MessageConfig.BACKPACK_OPENED_SELF);
        } else {
            sendMessage(viewer, MessageConfig.BACKPACK_OPENED_OTHER, owner.getName());
        }
    }

    private Inventory createBackpackInventory(Player owner, CommandSender requester) {
        BackpackData backpackData = loadBackpackData(owner.getUniqueId());
        int backpackSize = Math.max(configuredBackpackSize, backpackData.size());
        BackpackHolder holder = new BackpackHolder(owner.getUniqueId(), owner.getName());
        Inventory inventory = Bukkit.createInventory(holder, backpackSize, MessageConfig.component(MessageConfig.BACKPACK_TITLE, owner.getName()));
        holder.setInventory(inventory);

        if (backpackData.loadFailed()) {
            sendMessage(requester, MessageConfig.BACKPACK_LOAD_FAILED);
        }

        ItemStack[] contents = new ItemStack[inventory.getSize()];
        System.arraycopy(backpackData.contents(), 0, contents, 0, Math.min(backpackData.contents().length, contents.length));
        inventory.setContents(contents);
        return inventory;
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Inventory inventory = event.getInventory();
        InventoryHolder holder = inventory.getHolder();
        if (!(holder instanceof BackpackHolder backpackHolder)) {
            return;
        }

        Bukkit.getScheduler().runTask(plugin, () -> {
            if (!inventory.getViewers().isEmpty()) {
                return;
            }

            boolean saved = saveBackpackContents(backpackHolder.ownerId(), backpackHolder.ownerName(), inventory);
            if (!saved && event.getPlayer() instanceof Player player) {
                sendMessage(player, MessageConfig.BACKPACK_SAVE_FAILED);
            }

            openBackpacks.remove(backpackHolder.ownerId());
        });
    }

    private BackpackData loadBackpackData(UUID playerId) {
        File backpackFile = new File(backpacksFolder, playerId + ".yml");
        if (!backpackFile.exists()) {
            return new BackpackData(configuredBackpackSize, new ItemStack[configuredBackpackSize], false);
        }

        try {
            YamlConfiguration configuration = YamlConfiguration.loadConfiguration(backpackFile);
            int storedSize = resolveStoredBackpackSize(configuration.getInt("size", configuredBackpackSize));
            ItemStack[] contents = new ItemStack[storedSize];

            if (configuration.isConfigurationSection("serialized-contents")) {
                for (int slot = 0; slot < storedSize; slot++) {
                    String encoded = configuration.getString("serialized-contents." + slot);
                    if (encoded == null || encoded.isBlank()) {
                        continue;
                    }
                    contents[slot] = ItemStack.deserializeBytes(Base64.getDecoder().decode(encoded));
                }
            } else {
                for (int slot = 0; slot < storedSize; slot++) {
                    contents[slot] = configuration.getItemStack("contents." + slot);
                }
            }

            return new BackpackData(storedSize, contents, false);
        } catch (Exception exception) {
            plugin.getLogger().warning("Could not load backpack data for " + playerId + ": " + exception.getMessage());
            return new BackpackData(configuredBackpackSize, new ItemStack[configuredBackpackSize], true);
        }
    }

    private boolean saveBackpackContents(UUID playerId, String ownerName, Inventory inventory) {
        File backpackFile = new File(backpacksFolder, playerId + ".yml");
        YamlConfiguration configuration = new YamlConfiguration();
        configuration.set("owner", ownerName);
        configuration.set("size", inventory.getSize());
        configuration.set("format", "item-bytes-v1");

        ItemStack[] contents = inventory.getContents();
        for (int slot = 0; slot < contents.length; slot++) {
            ItemStack itemStack = contents[slot];
            if (itemStack == null) {
                continue;
            }
            configuration.set("serialized-contents." + slot, Base64.getEncoder().encodeToString(itemStack.serializeAsBytes()));
        }

        try {
            configuration.save(backpackFile);
            return true;
        } catch (IOException exception) {
            plugin.getLogger().warning("Could not save backpack data for " + playerId + ": " + exception.getMessage());
            return false;
        }
    }

    private int resolveConfiguredBackpackSize() {
        int configuredSize = plugin.getConfig().getInt("backpack-size", DEFAULT_BACKPACK_SIZE);
        if (isValidBackpackSize(configuredSize)) {
            return configuredSize;
        }

        plugin.getLogger().warning("Invalid backpack-size '" + configuredSize + "' in config.yml. Using default size " + DEFAULT_BACKPACK_SIZE + ".");
        return DEFAULT_BACKPACK_SIZE;
    }

    private int resolveStoredBackpackSize(int storedSize) {
        return isValidBackpackSize(storedSize) ? storedSize : configuredBackpackSize;
    }

    private boolean isValidBackpackSize(int size) {
        return size >= MIN_BACKPACK_SIZE && size <= MAX_BACKPACK_SIZE && size % 9 == 0;
    }

    private static final class BackpackHolder implements InventoryHolder {
        private final UUID ownerId;
        private final String ownerName;
        private Inventory inventory;

        private BackpackHolder(UUID ownerId, String ownerName) {
            this.ownerId = ownerId;
            this.ownerName = ownerName;
        }

        @Override
        public Inventory getInventory() {
            return inventory;
        }

        private void setInventory(Inventory inventory) {
            this.inventory = inventory;
        }

        private UUID ownerId() {
            return ownerId;
        }

        private String ownerName() {
            return ownerName;
        }
    }

    private record BackpackData(int size, ItemStack[] contents, boolean loadFailed) { }
}
