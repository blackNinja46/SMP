package live.blackninja.smp.manger;

import live.blackninja.smp.Core;
import live.blackninja.smp.config.Config;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DelayedOpeningManger {

    private Core core;

    private SMPManger smpManger;
    private Config config;

    private LocalDateTime netherDate;
    private LocalDateTime endDate;

    private boolean netherOpened = false;
    private boolean endOpened = false;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public DelayedOpeningManger(Core core, SMPManger smpManger) {
        this.core = core;
        this.smpManger = smpManger;

        config = smpManger.getConfig();

        load();
    }

    public void startDelay() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!netherOpened && netherDate != null && LocalDateTime.now().isAfter(netherDate)) {
                    netherOpened = true;
                }

                if (!endOpened && endDate != null && LocalDateTime.now().isAfter(endDate)) {
                    endOpened = true;
                }

                if (netherOpened && endOpened) cancel();
            }
        }.runTaskTimer(core, 0L, 20L);
    }

    public void load() {
        String netherDateString = config.getConfig().getString("nether-date");
        String endDateString = config.getConfig().getString("end-date");

        netherOpened = config.getConfig().getBoolean("nether-opened");
        endOpened = config.getConfig().getBoolean("end-opened");

        if (netherDateString != null) netherDate = LocalDateTime.parse(netherDateString, formatter);
        if (endDateString != null) endDate = LocalDateTime.parse(endDateString, formatter);
    }

    public void save() {
        config.getConfig().set("nether-opened", netherOpened);
        config.getConfig().set("end-opened", endOpened);

        if (netherDate != null) config.getConfig().set("nether-date", netherDate.format(formatter));
        if (endDate != null) config.getConfig().set("end-date", endDate.format(formatter));
    }

    public void setDate(String type, LocalDateTime date) {
        switch (type.toLowerCase()) {
            case "nether" -> {
                config.getConfig().set("nether-date", date.format(formatter));
                netherDate = date;
            }
            case "end" -> {
                config.getConfig().set("end-date", date.format(formatter));
                endDate = date;
            }
            default -> throw new IllegalArgumentException("Invalid type: " + type);
        }
        config.save();
    }
}
