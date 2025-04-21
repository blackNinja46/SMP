package live.blackninja.smp.listener;

import live.blackninja.smp.Core;
import live.blackninja.smp.manger.InvSeeHolder;
import org.bukkit.Material;
import org.bukkit.event.Listener;

import java.util.UUID;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.*;

public record InvSeeListener(Core core) implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (!(e.getInventory().getHolder() instanceof InvSeeHolder)) return;

        int slot = e.getRawSlot();

        if (slot >= 36 && slot <= 45) {
            e.setCancelled(true);
            return;
        }

        e.setCancelled(false);

        InvSeeHolder holder = (InvSeeHolder) e.getInventory().getHolder();
        UUID targetUUID = holder.getTargetUUID();
        Player target = e.getWhoClicked().getServer().getPlayer(targetUUID);
        if (target == null) return;

        e.getWhoClicked().getServer().getScheduler().runTaskLater(core,
                () -> syncInventory(e.getInventory(), target.getInventory()),
                1L
        );
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        if (!(e.getInventory().getHolder() instanceof InvSeeHolder)) return;

        InvSeeHolder holder = (InvSeeHolder) e.getInventory().getHolder();
        UUID targetUUID = holder.getTargetUUID();
        Player target = e.getPlayer().getServer().getPlayer(targetUUID);
        if (target == null) return;

        syncInventory(e.getInventory(), target.getInventory());
    }

    private void syncInventory(Inventory src, PlayerInventory dest) {
        for (int i = 0; i < 36; i++) {
            dest.setItem(i, src.getItem(i));
        }
        dest.setHelmet(src.getItem(45));
        dest.setChestplate(src.getItem(46));
        dest.setLeggings(src.getItem(47));
        dest.setBoots(src.getItem(48));
        dest.setItemInOffHand(src.getItem(49));
    }
}
