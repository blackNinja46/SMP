package live.blackninja.smp.manger;

import live.blackninja.smp.Core;
import live.blackninja.smp.config.StatusConfig;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.Set;

public class StatusManger {

    private Core core;

    private StatusConfig config;

    public StatusManger(Core core) {
        this.core = core;

        config = new StatusConfig("status");
    }

    public void addStatus(String name, String display) {
        config.getConfig().set("Status." + name + ".Display", display);
        config.save();
    }

    public void removeStatus(String name) {
        config.getConfig().set("Status." + name, null);
        config.save();
    }

    public Set<String> getStatusList() {
        FileConfiguration cfg = config.getConfig();

        ConfigurationSection statusSection = cfg.getConfigurationSection("Status");
        if (statusSection == null) {
            return Collections.emptySet();
        }

        return statusSection.getKeys(false);
    }

    public String getStatusDisplay(String name) {
        return config.getConfig().getString("Status." + name + ".Display");
    }

    public boolean existStatus(String name) {
        return config.getConfig().contains("Status." + name);
    }

    public void setStatusPlayer(Player player, String name) {
        config.getConfig().set("Status-Players." + player.getUniqueId() + ".CurrentStatus", name);
        config.getConfig().set("Status-Players." + player.getUniqueId() + ".hasStatus", true);
        config.save();
        core.getSmpManger().getRankManger().updateRanks(player.getName());
    }

    public void removeStatusPlayer(Player player) {
        config.getConfig().set("Status-Players." + player.getUniqueId() + ".CurrentStatus", null);
        config.getConfig().set("Status-Players." + player.getUniqueId() + ".hasStatus", false);
        config.save();
        core.getSmpManger().getRankManger().updateRanks(player.getName());
    }

    public String getStatus(Player player) {
        return config.getConfig().getString("Status-Players." + player.getUniqueId() + ".CurrentStatus");
    }

    public boolean hasPlayerStatus(Player player) {
        return config.getConfig().getBoolean("Status-Players." + player.getUniqueId() + ".hasStatus");
    }

    public void updatePlayer(String player) {
        core.getSmpManger().getRankManger().updateRanks(player);
    }
}
