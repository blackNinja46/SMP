package live.blackninja.smp.manger;

import live.blackninja.smp.config.ElytraConfig;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ElytraManger {

    private ElytraConfig config;

    private final Set<UUID> received = new HashSet<>();
    private final Set<UUID> used = new HashSet<>();

    public ElytraManger() {
        config = new ElytraConfig("elytra");

        load();
    }

    private void load() {
        if (config.getConfig().contains("Elytra.Received")) {
            for (String uuidStr : config.getConfig().getStringList("Elytra.Received")) {
                received.add(UUID.fromString(uuidStr));
            }
        }
        if (config.getConfig().contains("Elytra.Used")) {
            for (String uuidStr : config.getConfig().getStringList("Elytra.Used")) {
                used.add(UUID.fromString(uuidStr));
            }
        }
    }

    public void save() {
        config.getConfig().set("elytra.received", received.stream().map(UUID::toString).toList());
        config.getConfig().set("elytra.used", used.stream().map(UUID::toString).toList());

        config.save();
    }

    public boolean  hasReceived(UUID uuid) {
        return received.contains(uuid);
    }

    public boolean hasUsed(UUID uuid) {
        return used.contains(uuid);
    }

    public void markReceived(UUID uuid) {
        received.add(uuid);
        save();
    }

    public void markUsed(UUID uuid) {
        used.add(uuid);
        save();
    }

    public void clear(UUID uuid) {
        received.remove(uuid);
        used.remove(uuid);
        save();
    }
}
