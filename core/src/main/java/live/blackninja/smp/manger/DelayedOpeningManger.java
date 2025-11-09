package live.blackninja.smp.manger;

import live.blackninja.smp.Core;
import live.blackninja.smp.builder.MessageBuilder;
import live.blackninja.smp.builder.TextDisplayBuilder;
import live.blackninja.smp.config.Config;
import live.blackninja.smp.util.IntegerFormat;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.title.Title;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
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
    private int endPhaseTime;

    private Location statusLocation;
    private TextDisplayBuilder status;

    private boolean netherOpened = false;
    private boolean endOpened = false;

    private boolean isRunning = false;
    private boolean isEndPhase;

    private BossBar endPhaseBar;

    public DelayedOpeningManger(Core core, SMPManger smpManger) {
        this.core = core;
        this.smpManger = smpManger;
        this.config = smpManger.getConfig();

        this.isEndPhase = false;
        this.endPhaseTime = 3 * 60 * 60;
        this.endPhaseBar = BossBar.bossBar(Component.text("N/A"), 1.0f, BossBar.Color.WHITE, BossBar.Overlay.PROGRESS);

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
        startParticleTask();

        if (isEndPhase) {
            runEndPhase();
        }
    }

    public void load() {
        netherTimestamp = config.getConfig().getLong("nether-timestamp");
        endTimestamp = config.getConfig().getLong("end-timestamp");

        netherOpened = config.getConfig().getBoolean("nether-opened");
        endOpened = config.getConfig().getBoolean("end-opened");
        isRunning = config.getConfig().getBoolean("is-running");

        statusLocation = config.getLocation("status-location");

        endPhaseTime = config.getConfig().getInt("EndPhaseTime");
        isEndPhase = config.getConfig().getBoolean("isEndPhase");
    }

    public void save() {
        config.getConfig().set("nether-opened", netherOpened);
        config.getConfig().set("end-opened", endOpened);
        config.getConfig().set("is-running", isRunning);
        config.getConfig().set("nether-timestamp", netherTimestamp);
        config.getConfig().set("end-timestamp", endTimestamp);
        config.getConfig().set("End-Phase-Time", endPhaseTime);
        config.getConfig().set("isEndPhase", isEndPhase);
        config.save();
    }

    public void startDelay() {
        if (isRunning) {
            return;
        }
        isRunning = true;
        updateTextDisplay();
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
                    runEndPhase();
                    sendInfoMessage();
                }

                updateTextDisplay();

                if (netherOpened && endOpened) cancel();
            }
        }.runTaskTimer(core, 0L, 20L);
    }

    public void pauseDelay() {
        isRunning = false;
        updateTextDisplay();
    }

    public void sendInfoMessage() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.showTitle(Title.title(MessageBuilder.build("<color:#aa00ff>End-Event</color>"), MessageBuilder.build("<gray>Die <light_purple>End-Phase <gray>hat jetzt <green>begonnen<gray>!")));
            player.sendMessage(MessageBuilder.build("<dark_gray>[</dark_gray><color:#00ddff>⚡<dark_gray>]</dark_gray> <gray>Die <light_purple>End-Phase </light_purple>hat nun <green>begonnen<gray>."));
            player.sendMessage(MessageBuilder.build("<dark_gray>[</dark_gray><color:#00ddff>⚡<dark_gray>]</dark_gray> <color:#ff0044>ACHTUNG:</color> <gray>Keep-Inventory ist jetzt <green>aktiviert<gray>."));
            player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
        }
    }

    public void runEndPhase() {
        isEndPhase = true;
        World world = Bukkit.getWorld("world_the_end");
        if (world != null) {
            world.setGameRule(GameRule.KEEP_INVENTORY, true);
        }
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.showBossBar(endPhaseBar);
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!isEndPhase) return;

                if (endPhaseTime <= 0) {
                    isEndPhase = false;
                    cancel();
                    world.setGameRule(GameRule.KEEP_INVENTORY, false);
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        player.hideBossBar(endPhaseBar);
                        player.showTitle(Title.title(MessageBuilder.build("<color:#aa00ff>End-Event</color>"), MessageBuilder.build("<gray>Die <light_purple>End-Phase <gray>ist jetzt <red>beendet<gray>!")));
                        player.sendMessage(MessageBuilder.build("<dark_gray>[</dark_gray><color:#00ddff>⚡<dark_gray>]</dark_gray> <gray>Die <light_purple>End-Phase </light_purple>ist nun <red>beendet<gray>."));
                        player.sendMessage(MessageBuilder.build("<dark_gray>[</dark_gray><color:#00ddff>⚡<dark_gray>]</dark_gray> <color:#ff0044>ACHTUNG:</color> <gray>Keep-Inventory ist jetzt <red>deaktiviert<gray>."));
                        player.playSound(player, Sound.ENTITY_ENDER_DRAGON_DEATH, 1.0f, 1.0f);
                    }
                }

                endPhaseTime--;
                endPhaseBar.name(MessageBuilder.build("<color:#aa00ff>End-Event</color> endet in: <b><color:#00ddff>" + IntegerFormat.getFormattedTime(endPhaseTime) + "</color></b>"));
                Bukkit.getOnlinePlayers().forEach(player -> player.showBossBar(endPhaseBar));
            }
        }.runTaskTimer(core, 0L, 20L);
    }

    private void startParticleTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Entity entity : Bukkit.getWorlds().getFirst().getEntities()) {
                    if (entity instanceof Item item && item.getItemStack().getType() == Material.DRAGON_EGG) {
                        item.getWorld().spawnParticle(
                                Particle.DRAGON_BREATH,
                                item.getLocation().add(0, 0.3, 0),
                                10,
                                0.3, 0.3, 0.3,
                                0.01
                        );

                        item.getWorld().spawnParticle(
                                Particle.END_ROD,
                                item.getLocation().add(0, 0.2, 0),
                                3,
                                0.1, 0.3, 0.1,
                                0.01
                        );
                    }
                }
            }
        }.runTaskTimer(core, 0L, 10L);
    }

    public void setDate(String type, long timestamp) {
        switch (type.toLowerCase()) {
            case "nether" -> netherTimestamp = timestamp;
            case "end" -> endTimestamp = timestamp;
            default -> throw new IllegalArgumentException("Invalid type: " + type);
        }
        save();
        updateTextDisplay();
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

    public void reset() {
        netherTimestamp = 0;
        endTimestamp = 0;
        endPhaseTime = 3 * 60 * 60;
        netherOpened = false;
        endOpened = false;
        isRunning = false;
        isEndPhase = false;
        save();
        updateTextDisplay();
    }

    public boolean isEndPhase() {
        return isEndPhase;
    }

    public boolean isEndOpened() {
        return endOpened;
    }

    public boolean isNetherOpened() {
        return netherOpened;
    }
}
