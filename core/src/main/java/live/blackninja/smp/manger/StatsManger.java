package live.blackninja.smp.manger;

import live.blackninja.smp.Core;
import live.blackninja.smp.builder.TextDisplayBuilder;
import live.blackninja.smp.config.StatsConfig;
import live.blackninja.smp.util.IntegerFormat;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;


import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class StatsManger {

    private Core core;
    private StatsConfig config;

    private Location statusLocation;
    private TextDisplayBuilder status;

    public StatsManger(Core core) {
        this.core = core;

        config = new StatsConfig("stats");

        startPlaytimeTracker();
    }

    public void setStat(UUID player, StatType stat, int value) {
        config.getConfig().set("Stats." + player + "." + stat.name(), value);
        config.save();
    }

    public int getStat(UUID player, StatType stat) {
        return config.getConfig().getInt("Stats." + player + "." + stat.name(), 0);
    }

    public void addStat(UUID player, StatType stat, int value) {
        int currentValue = getStat(player, stat);
        setStat(player, stat, currentValue + value);
    }

    public void resetStats(UUID player) {
        for (StatType stat : StatType.values()) {
            setStat(player, stat, 0);
        }
    }

    public void startPlaytimeTracker() {
        Bukkit.getScheduler().runTaskTimer(core, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                UUID playerUUID = player.getUniqueId();
                addStat(playerUUID, StatType.PLAYTIME_IN_MINUTES, 1);
            }
        }, 20L, 20L * 60L);
    }

    public Map<UUID, Integer> getAllStats(StatType statType) {
        Map<UUID, Integer> map = new HashMap<>();
        ConfigurationSection statsSection = config.getConfig().getConfigurationSection("Stats");
        if (statsSection == null) return map;

        for (String uuidStr : statsSection.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(uuidStr);
                int value = config.getConfig().getInt("Stats." + uuid + "." + statType.name(), 0);
                map.put(uuid, value);
            } catch (IllegalArgumentException ignored) {}
        }
        return map;
    }

    public void spawnTextDisplay(Location location) {

        for (World world : Bukkit.getWorlds()) {
            world.getEntities().stream()
                    .filter(entity -> entity instanceof TextDisplay && entity.getScoreboardTags().contains("stats-display"))
                    .forEach(Entity::remove);
        }

        Component displayText = MiniMessage.miniMessage().deserialize(buildDisplayText());

        this.status = new TextDisplayBuilder(location)
                .setTextMiniMessage(displayText)
                .setAlignment(TextDisplay.TextAlignment.CENTER)
                .setBillboard(TextDisplay.Billboard.FIXED)
                .setInvisibleBackground(true)
                .setShadowed(true)
                .setTag("stats-display")
                .setRotation(location.getYaw(), 0);

        this.statusLocation = location;
        config.setLocation(location, "stats-display");
        config.save();
    }

    public void updateTextDisplay() {
        for (World world : Bukkit.getWorlds()) {
            world.getEntities().stream()
                    .filter(entity -> entity instanceof TextDisplay && entity.getScoreboardTags().contains("stats-display"))
                    .forEach(entity -> {
                        TextDisplay textDisplay = (TextDisplay) entity;
                        Component displayText = MiniMessage.miniMessage().deserialize(buildDisplayText());
                        textDisplay.text(displayText);
                    });
        }
    }

    public void removeTextDisplay() {
        if (status != null) {
            status.remove();
            status = null;
            config.getConfig().set("status-location", null);
        }
    }

    private String buildDisplayText() {
        return "";
    }



}

