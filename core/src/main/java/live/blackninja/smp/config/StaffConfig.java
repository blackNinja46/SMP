package live.blackninja.smp.config;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class StaffConfig {

    private final File file;
    private final FileConfiguration config;

    public StaffConfig(String configName) {
        File dir = new File("./plugins/SMP/");

        if (!dir.exists()) {
            dir.mkdirs();
        }

        this.file = new File(dir, configName + ".yml");

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        this.config = YamlConfiguration.loadConfiguration(file);
    }

    public FileConfiguration getConfig() {
        return this.config;
    }

    public void addList(StaffTypes staffTypes, UUID uuid) {
        String path = "staff." + staffTypes.name().toLowerCase() + ".list";
        List<String> list = this.config.getStringList(path);
        if (!list.contains(uuid.toString())) {
            list.add(uuid.toString());
            this.config.set(path, list);
            this.save();
        }
    }

    public void removeList(StaffTypes staffTypes, UUID uuid) {
        String path = "staff." + staffTypes.name().toLowerCase() + ".list";
        List<String> list = this.config.getStringList(path);
        if (list.remove(uuid.toString())) {
            this.config.set(path, list);
            this.save();
        }
    }

    public boolean containsList(StaffTypes staffTypes, UUID uuid) {
        String path = "staff." + staffTypes.name().toLowerCase() + ".list";
        return this.config.contains(path) && this.config.getStringList(path).contains(uuid.toString());
    }

    public void load() {
        try {
            this.config.load(this.file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InvalidConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    public void save() {

        try {
            this.config.save(this.file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

