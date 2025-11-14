package live.blackninja.smp.listener;

import live.blackninja.smp.Core;
import live.blackninja.smp.builder.ItemBuilder;
import live.blackninja.smp.builder.MessageBuilder;
import live.blackninja.smp.util.EntityGlow;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.util.Vector;

import java.util.*;

public class DragonEggListener implements Listener {

    private final Core core;

    private final Set<UUID> particleCooldown = new HashSet<>();
    private final Map<UUID, TextDisplay> eggDisplays = new HashMap<>();

    public DragonEggListener(Core core) {
        this.core = core;

        Bukkit.getScheduler().runTaskTimer(core, this::tickDragonEggs, 0L, 20L);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (event.getCurrentItem() == null) return;

        if (event.getInventory().getType() != InventoryType.ENDER_CHEST) return;

        if (event.getCurrentItem().getType() == Material.DRAGON_EGG) {
            event.setCancelled(true);
            player.sendActionBar(MessageBuilder.build("<color:#ff0044>✕ Das Drachen Ei darf nicht in die Ender Chest gelegt werden."));
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (!player.getInventory().contains(Material.DRAGON_EGG)) return;

        player.getInventory().remove(Material.DRAGON_EGG);

        Component msg = MessageBuilder.build(
                "<dark_gray>[</dark_gray><color:#00ddff>⚡<dark_gray>]</dark_gray> " +
                        "<color:#ff0044>ACHTUNG:</color> <gray>Das <light_purple>Drachen Ei</light_purple> wurde hier zurückgelassen: " +
                        "<color:#00ddff>" + player.getWorld().getName() + "<dark_gray>,</dark_gray> " +
                        player.getLocation().getBlockX() + "<dark_gray>,</dark_gray> " +
                        player.getLocation().getBlockZ()
        );

        for (Player online : Bukkit.getOnlinePlayers()) {
            online.sendMessage(msg);
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (!player.getInventory().contains(Material.DRAGON_EGG)) return;

        if (!particleCooldown.add(player.getUniqueId())) return;

        Bukkit.getScheduler().runTaskLater(core, () ->
                particleCooldown.remove(player.getUniqueId()), 8L);

        player.getWorld().spawnParticle(
                Particle.DRAGON_BREATH,
                player.getLocation().add(0, -0.5, 0),
                2, 0.5, 0, 0.5, 0.01
        );
    }

    @EventHandler
    public void onItemSpawn(ItemSpawnEvent event) {
        Item item = event.getEntity();

        if (item.getItemStack().getType() != Material.DRAGON_EGG) return;

        Bukkit.getScheduler().runTaskLater(core, () -> {
            if (!item.isValid()) return;

            Location loc = item.getLocation().clone().add(0, 0.6, 0);

            TextDisplay display = core.getSmpManger()
                    .getDelayedOpeningManger()
                    .getOrCreateEggDisplay(loc);

            eggDisplays.put(item.getUniqueId(), display);

        }, 10L);
    }

    @EventHandler
    public void onEntityPickupItem(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        Item item = event.getItem();
        if (item.getItemStack().getType() != Material.DRAGON_EGG) return;

        long nearby = player.getNearbyEntities(20, 20, 20)
                .stream().filter(e -> e instanceof Player).count();

        if (nearby > 1) {
            event.setCancelled(true);
            player.sendActionBar(MessageBuilder.build("<color:#ff0044>✕ Es befinden sich Spieler in der Nähe."));
            return;
        }

        // Glow entfernen
        EntityGlow glow = new EntityGlow(item);
        if (glow.isGlowing()) glow.removeGlowing();

        // Effekte
        player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1, 1);
        player.getWorld().spawnParticle(
                Particle.DRAGON_BREATH,
                player.getLocation().add(0, 1, 0),
                30, 0.3, 0.3, 0.3, 0.01
        );

        TextDisplay display = eggDisplays.remove(item.getUniqueId());
        if (display != null && display.isValid()) display.remove();

        // Broadcast
        for (Player online : Bukkit.getOnlinePlayers()) {
            online.sendMessage(MessageBuilder.build(
                    "<dark_gray>[</dark_gray><color:#00ddff>⚡<dark_gray>]</dark_gray> " +
                            "<gray>Das <light_purple>Drachen Ei</light_purple> ist nun in Besitz von <color:#00ddff>" +
                            player.getName() + "</color><gray>."
            ));
        }
    }

    private void tickDragonEggs() {
        for (World world : Bukkit.getWorlds()) {
            for (Item item : world.getEntitiesByClass(Item.class)) {

                if (item.getItemStack().getType() != Material.DRAGON_EGG) continue;

                if (item.getLocation().getY() < getWorldHeight(world)) {

                    Location spawn = world.getHighestBlockAt(item.getLocation())
                            .getLocation().add(0, 1, 0);

                    item.teleport(spawn);
                    item.setVelocity(new Vector(0, 0, 0));
                }
            }
        }
    }

    private int getWorldHeight(World world) {
        return world.getName().equals("world") ? -61 : 0;
    }
}
