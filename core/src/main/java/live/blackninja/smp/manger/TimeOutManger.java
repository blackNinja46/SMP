package live.blackninja.smp.manger;

import live.blackninja.smp.Core;
import live.blackninja.smp.config.TimeOutConfig;
import live.blackninja.smp.util.uuid.UUIDFetcher;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

public class TimeOutManger {

    private Core core;
    private TimeOutConfig config;

    public static final String PREFIX = StaffManger.STAFF_PREFIX;

    public TimeOutManger(Core core) {
        this.core = core;

        config = new TimeOutConfig("timeOut");;
    }

    public void timeOut(UUID uuid, String reason, long duration) {
        config.getConfig().set("TimeOut." + uuid + ".Reason", reason);
        config.getConfig().set("TimeOut." + uuid + ".Duration", duration);
        config.save();
    }

    public void unTimeOut(UUID uuid) {
        config.getConfig().set("TimeOut." + uuid, null);
        config.save();
    }

    public String getReason(UUID uuid) {
        return config.getConfig().getString("TimeOut." + uuid + ".Reason");
    }

    public boolean isPlayerExist(UUID uuid) {
        return config.getConfig().get("TimeOut." + uuid) != null;
    }

    public Long getDuration(UUID uuid) {
        return config.getConfig().getLong("TimeOut." + uuid + ".Duration");
    }

    public String getFormatedDuration(long duration) {
        Date date = new Date(duration);

        SimpleDateFormat formatter = new SimpleDateFormat("dd. MMMM yyyy H:m:s");
        return formatter.format(date);
    }

    public String getUsername(UUID uuid) {
        return UUIDFetcher.getName(uuid);
    }

    public Set<String> getTimeOutedPlayer() {
        FileConfiguration cfg = config.getConfig();

        ConfigurationSection playerSection = cfg.getConfigurationSection("TimeOut");
        if (playerSection == null) {
            return Collections.emptySet();
        }

        return playerSection.getKeys(false);
    }

    public Component getTimeOutMessage(String reason, String formattedDuration) {
        MiniMessage miniMessage = MiniMessage.miniMessage();
        return miniMessage.deserialize(
                "<dark_gray>*-----------* </dark_gray> <gradient:#4498DB:#06F5FF>SMP</gradient> <dark_gray>*------------*</dark_gray>\n" +
                " \n" +
                "<gray>Du wurdest bis zum</gray> <color:#ff5724>" + formattedDuration + "</color> <gray>gebannt!</gray> \n" +
                "<gray>Grund:</gray> <color:#c73200>" + reason + "</color> \n" +
                " \n" +
                "<gray>Bei Einwänden melde dich bitte im</gray> <color:#6183ff>Discord</color><gray>.</gray> \n" +
                " \n" +
                "<dark_gray>*---------------------------*</dark_gray>"

        );
    }
}
