package ch.framedev.spigotEssentials.managers;

import ch.framedev.spigotEssentials.PaperEssentials;
import ch.framedev.spigotEssentials.utils.MessageConfig;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

/**
 * Central manager for persisted moderation state and session-only staff chat state.
 */
public class ModerationManager {
    private static final long EXPIRY_CHECK_INTERVAL_TICKS = 20L * 60L;
    private static final DateTimeFormatter TIMESTAMP_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault());

    private final PaperEssentials plugin;
    private final File moderationFile;

    private final Map<UUID, MuteRecord> mutes = new HashMap<>();
    private final Map<UUID, WarningHistory> warnings = new HashMap<>();
    private final Set<UUID> staffChatToggled = new HashSet<>();

    private boolean globalChatMuted;
    private String globalChatMutedBy = "";
    private long globalChatMutedAt;

    public ModerationManager(PaperEssentials plugin) {
        this.plugin = plugin;
        this.moderationFile = new File(plugin.getDataFolder(), "moderation.yml");
        load();
        startExpiryTask();
    }

    public synchronized void load() {
        ensureFileExists();
        FileConfiguration config = YamlConfiguration.loadConfiguration(moderationFile);

        globalChatMuted = config.getBoolean("globalChatMuted", false);
        globalChatMutedBy = config.getString("globalChatMutedBy", "");
        globalChatMutedAt = config.getLong("globalChatMutedAt", 0L);

        loadMutes(config);
        loadWarnings(config);
        pruneExpiredMutes();
    }

    public synchronized void shutdown() {
        save();
        staffChatToggled.clear();
    }

    public synchronized MuteRecord mute(UUID playerId, String lastKnownName, String reason, String actor) {
        MuteRecord record = new MuteRecord(playerId, lastKnownName, reason, actor, System.currentTimeMillis(), null);
        mutes.put(playerId, record);
        save();
        return record;
    }

    public synchronized MuteRecord tempMute(UUID playerId, String lastKnownName, String reason, String actor, long durationMillis) {
        long createdAt = System.currentTimeMillis();
        MuteRecord record = new MuteRecord(playerId, lastKnownName, reason, actor, createdAt, createdAt + durationMillis);
        mutes.put(playerId, record);
        save();
        return record;
    }

    public synchronized MuteRecord unmute(UUID playerId) {
        MuteRecord removed = mutes.remove(playerId);
        if (removed != null) {
            save();
        }
        return removed;
    }

    public synchronized MuteRecord getActiveMute(UUID playerId) {
        MuteRecord record = mutes.get(playerId);
        if (record == null) {
            return null;
        }

        if (record.isExpired(System.currentTimeMillis())) {
            mutes.remove(playerId);
            save();
            return null;
        }

        return record;
    }

    public synchronized boolean isGlobalChatMuted() {
        return globalChatMuted;
    }

    public synchronized boolean setGlobalChatMuted(boolean muted, String actor) {
        if (globalChatMuted == muted) {
            return false;
        }

        globalChatMuted = muted;
        globalChatMutedBy = actor;
        globalChatMutedAt = System.currentTimeMillis();
        save();
        return true;
    }

    public synchronized void addWarning(UUID playerId, String lastKnownName, String reason, String actor) {
        WarningHistory history = warnings.computeIfAbsent(playerId, ignored -> new WarningHistory(lastKnownName, new ArrayList<>()));
        history.lastKnownName = lastKnownName;
        history.entries.add(new WarningEntry(reason, actor, System.currentTimeMillis()));
        save();
    }

    public synchronized List<WarningEntry> getWarnings(UUID playerId) {
        WarningHistory history = warnings.get(playerId);
        if (history == null) {
            return List.of();
        }

        return new ArrayList<>(history.entries);
    }

    public synchronized int clearWarnings(UUID playerId) {
        WarningHistory removedHistory = warnings.remove(playerId);
        if (removedHistory == null) {
            return 0;
        }

        save();
        return removedHistory.entries.size();
    }

    public synchronized boolean toggleStaffChat(UUID playerId) {
        if (staffChatToggled.contains(playerId)) {
            staffChatToggled.remove(playerId);
            return false;
        }

        staffChatToggled.add(playerId);
        return true;
    }

    public synchronized void setStaffChatEnabled(UUID playerId, boolean enabled) {
        if (enabled) {
            staffChatToggled.add(playerId);
        } else {
            staffChatToggled.remove(playerId);
        }
    }

    public synchronized boolean isStaffChatEnabled(UUID playerId) {
        return staffChatToggled.contains(playerId);
    }

    public synchronized void clearSessionState(UUID playerId) {
        staffChatToggled.remove(playerId);
    }

    public void sendStaffChat(String senderName, String message) {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (onlinePlayer.hasPermission("spigotessentials.staffchat")) {
                MessageConfig.send(onlinePlayer, MessageConfig.STAFFCHAT_FORMAT, senderName, message);
            }
        }

        CommandSender console = Bukkit.getConsoleSender();
        if (console.hasPermission("spigotessentials.staffchat")) {
            MessageConfig.send(console, MessageConfig.STAFFCHAT_FORMAT, senderName, message);
        }
    }

    public static String formatDuration(long durationMillis) {
        long totalSeconds = Math.max(1L, (durationMillis + 999L) / 1000L);
        long weeks = totalSeconds / TimeUnit.DAYS.toSeconds(7L);
        totalSeconds %= TimeUnit.DAYS.toSeconds(7L);
        long days = totalSeconds / TimeUnit.DAYS.toSeconds(1L);
        totalSeconds %= TimeUnit.DAYS.toSeconds(1L);
        long hours = totalSeconds / TimeUnit.HOURS.toSeconds(1L);
        totalSeconds %= TimeUnit.HOURS.toSeconds(1L);
        long minutes = totalSeconds / TimeUnit.MINUTES.toSeconds(1L);
        long seconds = totalSeconds % TimeUnit.MINUTES.toSeconds(1L);

        List<String> parts = new ArrayList<>();
        appendDurationPart(parts, weeks, "w");
        appendDurationPart(parts, days, "d");
        appendDurationPart(parts, hours, "h");
        appendDurationPart(parts, minutes, "m");
        appendDurationPart(parts, seconds, "s");

        return String.join(" ", parts);
    }

    public static String formatTimestamp(long epochMillis) {
        return TIMESTAMP_FORMATTER.format(Instant.ofEpochMilli(epochMillis));
    }

    private static void appendDurationPart(List<String> parts, long amount, String suffix) {
        if (amount > 0L) {
            parts.add(amount + suffix);
        }
    }

    private synchronized void pruneExpiredMutes() {
        long now = System.currentTimeMillis();
        boolean removedAny = mutes.entrySet().removeIf(entry -> entry.getValue().isExpired(now));
        if (removedAny) {
            save();
        }
    }

    private void startExpiryTask() {
        Bukkit.getScheduler().runTaskTimer(plugin, this::pruneExpiredMutes,
                EXPIRY_CHECK_INTERVAL_TICKS, EXPIRY_CHECK_INTERVAL_TICKS);
    }

    private void ensureFileExists() {
        if (moderationFile.exists()) {
            return;
        }

        File parent = moderationFile.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }

        try {
            moderationFile.createNewFile();
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not create moderation.yml", e);
        }
    }

    private void loadMutes(FileConfiguration config) {
        mutes.clear();
        ConfigurationSection mutesSection = config.getConfigurationSection("mutes");
        if (mutesSection == null) {
            return;
        }

        for (String key : mutesSection.getKeys(false)) {
            UUID playerId = parseUuid("mutes", key);
            if (playerId == null) {
                continue;
            }

            ConfigurationSection muteSection = mutesSection.getConfigurationSection(key);
            if (muteSection == null) {
                continue;
            }

            String lastKnownName = muteSection.getString("lastKnownName", key);
            String reason = muteSection.getString("reason", MessageConfig.DEFAULT_REASON);
            String actor = muteSection.getString("actor", "Unknown");
            long createdAt = muteSection.getLong("createdAt", 0L);
            long expiresAt = muteSection.getLong("expiresAt", 0L);

            mutes.put(playerId, new MuteRecord(
                    playerId,
                    lastKnownName,
                    reason,
                    actor,
                    createdAt,
                    expiresAt > 0L ? expiresAt : null
            ));
        }
    }

    private void loadWarnings(FileConfiguration config) {
        warnings.clear();
        ConfigurationSection warningsSection = config.getConfigurationSection("warnings");
        if (warningsSection == null) {
            return;
        }

        for (String key : warningsSection.getKeys(false)) {
            UUID playerId = parseUuid("warnings", key);
            if (playerId == null) {
                continue;
            }

            ConfigurationSection warningSection = warningsSection.getConfigurationSection(key);
            if (warningSection == null) {
                continue;
            }

            String lastKnownName = warningSection.getString("lastKnownName", key);
            List<Map<?, ?>> entryMaps = warningSection.getMapList("entries");
            List<WarningEntry> entries = new ArrayList<>();
            for (Map<?, ?> entryMap : entryMaps) {
                Object reasonValue = entryMap.containsKey("reason") ? entryMap.get("reason") : MessageConfig.DEFAULT_REASON;
                Object actorValue = entryMap.containsKey("actor") ? entryMap.get("actor") : "Unknown";
                entries.add(new WarningEntry(
                        String.valueOf(reasonValue),
                        String.valueOf(actorValue),
                        parseLong(entryMap.get("createdAt"))
                ));
            }

            if (!entries.isEmpty()) {
                warnings.put(playerId, new WarningHistory(lastKnownName, entries));
            }
        }
    }

    private synchronized void save() {
        FileConfiguration config = new YamlConfiguration();
        config.set("globalChatMuted", globalChatMuted);
        config.set("globalChatMutedBy", globalChatMutedBy);
        config.set("globalChatMutedAt", globalChatMutedAt);

        for (Map.Entry<UUID, MuteRecord> entry : mutes.entrySet()) {
            String basePath = "mutes." + entry.getKey();
            MuteRecord mute = entry.getValue();
            config.set(basePath + ".lastKnownName", mute.lastKnownName());
            config.set(basePath + ".reason", mute.reason());
            config.set(basePath + ".actor", mute.actor());
            config.set(basePath + ".createdAt", mute.createdAt());
            config.set(basePath + ".expiresAt", mute.expiresAt() == null ? 0L : mute.expiresAt());
        }

        for (Map.Entry<UUID, WarningHistory> entry : warnings.entrySet()) {
            String basePath = "warnings." + entry.getKey();
            WarningHistory history = entry.getValue();
            config.set(basePath + ".lastKnownName", history.lastKnownName);

            List<Map<String, Object>> serializedEntries = new ArrayList<>();
            for (WarningEntry warningEntry : history.entries) {
                Map<String, Object> serializedEntry = new LinkedHashMap<>();
                serializedEntry.put("reason", warningEntry.reason());
                serializedEntry.put("actor", warningEntry.actor());
                serializedEntry.put("createdAt", warningEntry.createdAt());
                serializedEntries.add(serializedEntry);
            }
            config.set(basePath + ".entries", serializedEntries);
        }

        try {
            config.save(moderationFile);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not save moderation.yml", e);
        }
    }

    private UUID parseUuid(String sectionName, String rawUuid) {
        try {
            return UUID.fromString(rawUuid);
        } catch (IllegalArgumentException exception) {
            plugin.getLogger().warning("Skipping invalid " + sectionName + " entry for UUID: " + rawUuid);
            return null;
        }
    }

    private long parseLong(Object value) {
        if (value instanceof Number number) {
            return number.longValue();
        }

        if (value instanceof String stringValue) {
            try {
                return Long.parseLong(stringValue);
            } catch (NumberFormatException ignored) {
                return 0L;
            }
        }

        return 0L;
    }

    public record MuteRecord(
            UUID playerId,
            String lastKnownName,
            String reason,
            String actor,
            long createdAt,
            Long expiresAt
    ) {
        public boolean isTemporary() {
            return expiresAt != null && expiresAt > 0L;
        }

        public boolean isExpired(long now) {
            return isTemporary() && expiresAt <= now;
        }

        public long getRemainingMillis(long now) {
            if (!isTemporary()) {
                return 0L;
            }

            return Math.max(0L, expiresAt - now);
        }
    }

    public record WarningEntry(String reason, String actor, long createdAt) { }

    private static final class WarningHistory {
        private String lastKnownName;
        private final List<WarningEntry> entries;

        private WarningHistory(String lastKnownName, List<WarningEntry> entries) {
            this.lastKnownName = lastKnownName;
            this.entries = entries;
        }
    }
}
