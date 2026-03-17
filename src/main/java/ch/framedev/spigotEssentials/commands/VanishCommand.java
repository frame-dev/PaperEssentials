package ch.framedev.spigotEssentials.commands;

import ch.framedev.spigotEssentials.utils.MessageConfig;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Command to toggle player invisibility (vanish mode)
 */
public class VanishCommand extends AbstractCommand implements Listener {

    private static final Set<UUID> vanishedPlayers = new HashSet<>();

    @Override
    protected boolean execute(CommandSender sender, Command command, String label, String[] args) {
        Player player = asPlayer(sender);
        if (player == null) return true;

        if (!checkPermission(sender, "spigotessentials.vanish", MessageConfig.NO_PERMISSION_SELF)) {
            return true;
        }

        UUID playerUUID = player.getUniqueId();

        if (vanishedPlayers.contains(playerUUID)) {
            // Unvanish
            vanishedPlayers.remove(playerUUID);
            for (Player onlinePlayer : player.getServer().getOnlinePlayers()) {
                onlinePlayer.showPlayer(player.getServer().getPluginManager().getPlugin("PaperEssentials"), player);
            }
            sendMessage(sender, MessageConfig.VANISH_DISABLED);
        } else {
            // Vanish
            vanishedPlayers.add(playerUUID);
            for (Player onlinePlayer : player.getServer().getOnlinePlayers()) {
                if (!onlinePlayer.hasPermission("spigotessentials.vanish.see")) {
                    onlinePlayer.hidePlayer(player.getServer().getPluginManager().getPlugin("PaperEssentials"), player);
                }
            }
            sendMessage(sender, MessageConfig.VANISH_ENABLED);
        }

        return true;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player joiningPlayer = event.getPlayer();

        // Hide vanished players from newly joined player if they don't have permission
        if (!joiningPlayer.hasPermission("spigotessentials.vanish.see")) {
            for (UUID vanishedUUID : vanishedPlayers) {
                Player vanishedPlayer = joiningPlayer.getServer().getPlayer(vanishedUUID);
                if (vanishedPlayer != null && vanishedPlayer.isOnline()) {
                    joiningPlayer.hidePlayer(joiningPlayer.getServer().getPluginManager().getPlugin("PaperEssentials"), vanishedPlayer);
                }
            }
        }

        // If joining player is vanished, hide them from others
        if (vanishedPlayers.contains(joiningPlayer.getUniqueId())) {
            for (Player onlinePlayer : joiningPlayer.getServer().getOnlinePlayers()) {
                if (!onlinePlayer.hasPermission("spigotessentials.vanish.see") && !onlinePlayer.equals(joiningPlayer)) {
                    onlinePlayer.hidePlayer(joiningPlayer.getServer().getPluginManager().getPlugin("PaperEssentials"), joiningPlayer);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        // Keep vanish state persistent across logins - don't remove from set
        // If you want vanish to reset on logout, uncomment the line below:
        // vanishedPlayers.remove(event.getPlayer().getUniqueId());
    }

    public static boolean isVanished(UUID playerUUID) {
        return vanishedPlayers.contains(playerUUID);
    }

    public static void unvanish(UUID playerUUID) {
        vanishedPlayers.remove(playerUUID);
    }
}
