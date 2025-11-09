package live.blackninja.smp.listener;

import io.papermc.paper.event.player.PlayerPickItemEvent;
import live.blackninja.smp.Core;
import live.blackninja.smp.builder.ItemBuilder;
import live.blackninja.smp.builder.MessageBuilder;
import net.kyori.adventure.util.TriState;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Beacon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

public record DragonEggListener(Core core) implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        if (event.getCurrentItem() == null) {
            return;
        }

        if (event.getInventory().getType() != InventoryType.ENDER_CHEST) {
            return;
        }

        if (event.getCurrentItem().getType().equals(Material.DRAGON_EGG)) {
            event.setCancelled(true);
            player.sendActionBar(MessageBuilder.build("<color:#ff0044>✕ Das Drachen Ei darf nicht in die Ender Chest gelegt werden."));
        }
    }

    @EventHandler
    public void onEntityPickupItem(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        if (!core.getSmpManger().getDelayedOpeningManger().isEndPhase()) {
            return;
        }

        if (!event.getItem().getItemStack().getType().equals(Material.DRAGON_EGG)) {
            return;
        }

        for (Entity entity : player.getNearbyEntities(20, 20, 20)) {
            if (entity instanceof Player target) {
                if (target.getName().equals(player.getName())) {
                    return;
                }

                event.setCancelled(true);
                player.sendActionBar(MessageBuilder.build("<color:#ff0044>✕ Es befinden sich Spieler in der Nähe."));
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (!player.getInventory().contains(Material.DRAGON_EGG)) {
            return;
        }

        player.getInventory().remove(Material.DRAGON_EGG);
        Item dragonEgg = (Item) player.getWorld().spawnEntity(player.getLocation(), EntityType.ITEM);
        dragonEgg.setItemStack(new ItemBuilder(Material.DRAGON_EGG).build());
        dragonEgg.setGlowing(true);
        dragonEgg.setInvulnerable(true);
        dragonEgg.setUnlimitedLifetime(true);

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            World world = player.getWorld();
            int x = player.getLocation().getBlockX();
            int z = player.getLocation().getBlockZ();
            onlinePlayer.sendMessage(MessageBuilder.build("<dark_gray>[</dark_gray><color:#00ddff>⚡<dark_gray>]</dark_gray> <color:#ff0044>ACHTUNG:</color> <gray>Das <light_purple>Drachen Ei</light_purple> wurde an dieser <color:#00ddff>Positon</color> zurückgelassen: " +
                    "<color:#00ddff>" + world.getName() + "<dark_gray>,</dark_gray> " + x + "<dark_gray>,</dark_gray> " + z));
        }
    }


}
