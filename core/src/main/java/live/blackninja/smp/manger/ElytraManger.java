package live.blackninja.smp.manger;

import live.blackninja.smp.config.ElytraConfig;

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

    public void load() {
        if (config.getConfig().contains("elytra.received")) {
            for (String uuidStr : config.getConfig().getStringList("elytra.received")) {
                received.add(UUID.fromString(uuidStr));
            }
        }
        if (config.getConfig().contains("elytra.used")) {
            for (String uuidStr : config.getConfig().getStringList("elytra.used")) {
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
