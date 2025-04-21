package live.blackninja.smp.manger;

import java.util.UUID;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class InvSeeHolder implements InventoryHolder {
    private final UUID targetUUID;

    public InvSeeHolder(UUID targetUUID) {
        this.targetUUID = targetUUID;
    }

    public UUID getTargetUUID() {
        return targetUUID;
    }

    @Override
    public Inventory getInventory() {
        return null;
    }
}

