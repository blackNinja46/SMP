package live.blackninja.smp.manger;

import live.blackninja.smp.Core;
import live.blackninja.smp.config.HomeConfig;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

public class HomeManger {

    private Core core;
    private HomeConfig config;

    public HomeManger(Core core) {
        this.core = core;

        config = new HomeConfig("homes");

        if (!config.getConfig().contains("Homes")) {
            config.getConfig().set("Homes", null);
            config.save();
        }
    }

    public void initPlayer(String player) {
        if (!config.getConfig().contains("Homes." + player)) {
            config.getConfig().set("HomeCounting." + player + ".Count", getMaxHomes());
            config.getConfig().set("Homes." + player, new HashMap<String, Object>());
            config.save();
        }
    }

    public int getMaxHomes() {
        return core.getSmpManger().getConfig().getConfig().getInt("MaxHomes");
    }

    public void setHome(String player, String name, Location location) {
        config.setLocation(location, "Homes." + player + "." + name);

        int limit = getCountForPlayer(player);
        setCountForPlayer(player, limit - 1);

        config.save();
    }

    public Location getHome(String player, String name) {
        return config.getLocation("Homes." + player + "." + name);
    }

    public boolean existsHome(String player, String name) {
        return config.getConfig().contains("Homes." + player + "." + name);
    }

    public void deleteHome(String player, String name) {
        config.getConfig().set("Homes." + player + "." + name, null);

        int limit = getCountForPlayer(player);
        setCountForPlayer(player, limit + 1);

        config.save();
    }

    public void setCountForPlayer(String player, int count) {
        config.getConfig().set("HomeCounting." + player + ".Count", count);
        config.save();
    }

    public int getCountForPlayer(String player) {
        return config.getConfig().getInt("HomeCounting." + player + ".Count");
    }

    public Set<String> getHomes(String playerName) {
        FileConfiguration cfg = config.getConfig();

        ConfigurationSection playerSection = cfg.getConfigurationSection("Homes." + playerName);
        if (playerSection == null) {
            return Collections.emptySet();
        }

        return playerSection.getKeys(false);
    }



    public void teleportPlayerToHome(String player, String name) {
        Location location = getHome(player, name);
        if (location != null) {
            core.getServer().getPlayer(player).teleport(location);
        } else {
            core.getServer().getPlayer(player).sendMessage("Home not found");
        }
    }

}
