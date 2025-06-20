package live.blackninja.smp.manger;

import live.blackninja.smp.Core;
import live.blackninja.smp.builder.TextDisplayBuilder;
import live.blackninja.smp.config.Config;
import live.blackninja.smp.util.IntegerFormat;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.TextDisplay;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DelayedOpeningManger {

    private Core core;
    private SMPManger smpManger;
    private Config config;

    private long netherTimestamp;
    private long endTimestamp;

    private Location statusLocation;
    private TextDisplayBuilder status;

    private boolean netherOpened = false;
    private boolean endOpened = false;

    private boolean isRunning = false;

    public DelayedOpeningManger(Core core, SMPManger smpManger) {
        this.core = core;
        this.smpManger = smpManger;
        this.config = smpManger.getConfig();

        load();
        if (statusLocation != null) {
            Bukkit.getScheduler().runTaskLater(core, () -> {
                if (statusLocation.getWorld() == null) {
                    Bukkit.getLogger().warning("TextDisplay-Location konnte nicht geladen werden (Welt null): " + statusLocation);
                    return;
                }
                spawnTextDisplay(statusLocation);
            }, 20L);
        }

        runDelay();
    }

    public void startDelay() {
        if (isRunning) {
            return;
        }
        isRunning = true;
    }

    public void runDelay() {
        isRunning = true;
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!isRunning) return;

                long now = System.currentTimeMillis() / 1000;

                if (!netherOpened && netherTimestamp > 0 && now >= netherTimestamp) {
                    netherOpened = true;

                }

                if (!endOpened && endTimestamp > 0 && now >= endTimestamp) {
                    endOpened = true;
                }

                updateTextDisplay();

                if (netherOpened && endOpened) cancel();
            }
        }.runTaskTimer(core, 0L, 20L);
    }

    public void pauseDelay() {
        isRunning = false;
    }

    public void load() {
        netherTimestamp = config.getConfig().getLong("nether-timestamp");
        endTimestamp = config.getConfig().getLong("end-timestamp");

        netherOpened = config.getConfig().getBoolean("nether-opened");
        endOpened = config.getConfig().getBoolean("end-opened");
        isRunning = config.getConfig().getBoolean("is-running");

        statusLocation = config.getLocation("status-location");
    }

    public void save() {
        config.getConfig().set("nether-opened", netherOpened);
        config.getConfig().set("end-opened", endOpened);
        config.getConfig().set("is-running", isRunning);
        config.getConfig().set("nether-timestamp", netherTimestamp);
        config.getConfig().set("end-timestamp", endTimestamp);
        config.save();
    }

    public void setDate(String type, long timestamp) {
        switch (type.toLowerCase()) {
            case "nether" -> netherTimestamp = timestamp;
            case "end" -> endTimestamp = timestamp;
            default -> throw new IllegalArgumentException("Invalid type: " + type);
        }
        save();
    }

    public void spawnTextDisplay(Location location) {

        for (World world : Bukkit.getWorlds()) {
            world.getEntities().stream()
                    .filter(entity -> entity instanceof TextDisplay && entity.getScoreboardTags().contains("status-display"))
                    .forEach(Entity::remove);
        }

        Component displayText = MiniMessage.miniMessage().deserialize(buildDisplayText());

        this.status = new TextDisplayBuilder(location)
                .setTextMiniMessage(displayText)
                .setAlignment(TextDisplay.TextAlignment.CENTER)
                .setBillboard(TextDisplay.Billboard.FIXED)
                .setInvisibleBackground(true)
                .setShadowed(true)
                .setTag("status-display")
                .setRotation(location.getYaw(), 0);

        this.statusLocation = location;
        config.setLocation(location, "status-location");
        config.save();
    }

    public void updateTextDisplay() {
        for (World world : Bukkit.getWorlds()) {
            world.getEntities().stream()
                    .filter(entity -> entity instanceof TextDisplay && entity.getScoreboardTags().contains("status-display"))
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
        long now = System.currentTimeMillis() / 1000;
        String nether = (netherTimestamp <= 0)
                ? "<color:#ff0000>✘ Nicht festgelegt</color>"
                : (netherOpened ? "<color:#00ff00>✔ Geöffnet</color>" : "<b><color:#03d9ff>" + IntegerFormat.getFormattedTimeLong(netherTimestamp - now) + "</color></b>");
        String end = (endTimestamp <= 0)
                ? "<color:#ff0000>✘ Nicht festgelegt</color>"
                : (endOpened ? "<color:#00ff00>✔ Geöffnet</color>" : "<b><color:#03d9ff>" + IntegerFormat.getFormattedTimeLong(endTimestamp - now) + "</color></b>");
        return "<gradient:#5CDAC7:#48A3D8>ᴇʀᴏᴇғғɴᴜɴɢᴇɴ</gradient>\n" +
                "<gray>*-----------------------*</gray>\n" +
                " \n" +
                "<white>\uEfe4 Nether: " + nether + "\n" +
                " \n" +
                "<white>\uEfe5 End: " + end + "\n";
    }

    public boolean isEndOpened() {
        return endOpened;
    }

    public boolean isNetherOpened() {
        return netherOpened;
    }
}
