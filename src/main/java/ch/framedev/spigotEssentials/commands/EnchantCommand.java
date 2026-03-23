package ch.framedev.spigotEssentials.commands;

import ch.framedev.spigotEssentials.utils.MessageConfig;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.MenuType;
import org.bukkit.inventory.view.EnchantmentView;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

/**
 * Command to open enchanting table interface
 */
public class EnchantCommand extends AbstractCommand implements Listener {
    private static final int[] OFFER_COSTS = {10, 20, 30};

    private final Set<UUID> virtualEnchanters = new HashSet<>();
    private final Map<UUID, VirtualEnchantData> preparedEnchants = new HashMap<>();

    @Override
    protected boolean execute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player player = asPlayer(sender);
        if (player == null) {
            sendMessage(sender, MessageConfig.PLAYER_ONLY);
            return true;
        }

        if (!checkPermission(sender, "spigotessentials.enchant", MessageConfig.NO_PERMISSION_SELF)) {
            return true;
        }

        EnchantmentView view = MenuType.ENCHANTMENT.builder()
                .checkReachable(false)
                .title(MessageConfig.component(""))
                .build(player);
        player.openInventory(view);
        virtualEnchanters.add(player.getUniqueId());
        sendMessage(sender, MessageConfig.ENCHANT_OPENED);
        return true;
    }

    @EventHandler
    public void onPrepareItemEnchant(PrepareItemEnchantEvent event) {
        Player player = event.getEnchanter();
        if (!isVirtualEnchantView(player, event.getView())) {
            return;
        }

        VirtualEnchantData enchantData = createVirtualEnchantData(event.getItem(), event.getView().getEnchantmentSeed());
        preparedEnchants.put(player.getUniqueId(), enchantData);

        for (int slot = 0; slot < OFFER_COSTS.length; slot++) {
            event.getOffers()[slot] = enchantData.getOffer(slot);
        }
        event.getView().setOffers(event.getOffers());
    }

    @EventHandler
    public void onEnchantItem(EnchantItemEvent event) {
        Player player = event.getEnchanter();
        if (!isVirtualEnchantView(player, event.getView())) {
            return;
        }

        int button = event.whichButton();
        VirtualEnchantData enchantData = preparedEnchants.computeIfAbsent(
                player.getUniqueId(),
                ignored -> createVirtualEnchantData(event.getItem(), ((EnchantmentView) event.getView()).getEnchantmentSeed())
        );

        Map<Enchantment, Integer> enchants = enchantData.getEnchants(button);
        if (enchants.isEmpty()) {
            return;
        }

        event.getEnchantsToAdd().clear();
        event.getEnchantsToAdd().putAll(enchants);
        event.setExpLevelCost(enchantData.getCost(button));
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        UUID playerId = event.getPlayer().getUniqueId();
        virtualEnchanters.remove(playerId);
        preparedEnchants.remove(playerId);
    }

    private boolean isVirtualEnchantView(Player player, org.bukkit.inventory.InventoryView view) {
        return virtualEnchanters.contains(player.getUniqueId()) && view instanceof EnchantmentView;
    }

    private VirtualEnchantData createVirtualEnchantData(org.bukkit.inventory.ItemStack item, int enchantmentSeed) {
        EnchantmentOfferData[] offerData = new EnchantmentOfferData[OFFER_COSTS.length];
        for (int slot = 0; slot < OFFER_COSTS.length; slot++) {
            int cost = OFFER_COSTS[slot];
            Random random = new Random(enchantmentSeed + (slot * 31L));
            Map<Enchantment, Integer> enchants = new HashMap<>(item.enchantWithLevels(cost, false, random).getEnchantments());
            offerData[slot] = new EnchantmentOfferData(cost, enchants);
        }

        return new VirtualEnchantData(offerData);
    }

    private record VirtualEnchantData(EnchantmentOfferData[] offers) {
        private org.bukkit.enchantments.EnchantmentOffer getOffer(int slot) {
            return offers[slot].toOffer();
        }

        private Map<Enchantment, Integer> getEnchants(int slot) {
            return offers[slot].enchants();
        }

        private int getCost(int slot) {
            return offers[slot].cost();
        }
    }

    private record EnchantmentOfferData(int cost, Map<Enchantment, Integer> enchants) {
        private org.bukkit.enchantments.EnchantmentOffer toOffer() {
            if (enchants.isEmpty()) {
                return null;
            }

            Map.Entry<Enchantment, Integer> previewEnchant = enchants.entrySet().stream()
                    .sorted(Comparator
                            .<Map.Entry<Enchantment, Integer>>comparingInt(Map.Entry::getValue)
                            .reversed()
                            .thenComparing(entry -> entry.getKey().getKey().asString()))
                    .findFirst()
                    .orElseThrow();

            return new org.bukkit.enchantments.EnchantmentOffer(previewEnchant.getKey(), previewEnchant.getValue(), cost);
        }
    }
}
