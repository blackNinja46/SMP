package live.blackninja.smp.listener;

import live.blackninja.smp.Core;
import live.blackninja.smp.builder.ItemBuilder;
import live.blackninja.smp.builder.MessageBuilder;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.data.type.DriedGhast;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.HappyGhast;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public record PlayerInteractListener(Core core) implements Listener {

    public static ArrayList<String> scale = new ArrayList<>();
    private final static Map<UUID, Long> messageCooldown = new HashMap<>();
    private final static long cooldownMillis = 5000; // 5 Sekunden

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (core.getSmpManger().getDelayedOpeningManger().isEndOpened()) {
            return;
        }

        if (event.getItem() == null || event.getItem().getType() == Material.AIR) {
            return;
        }

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) {
            if (event.getItem().getType().toString().contains("CHESTPLATE") && event.getPlayer().getInventory().getChestplate().getItemMeta().getDisplayName().equals("§bTemporäre Elytra")) {
                event.setCancelled(true);
            }
        }

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (event.getClickedBlock().getType() == Material.END_PORTAL_FRAME) {
                event.setCancelled(true);
                player.sendMessage(MessageBuilder.buildOld(Core.PREFIX + "§7Das §dEnd §7wurde noch %rnicht §7geöffnet!"));
            }
        }
    }

    @EventHandler
    public void onEntityPortalEnter(EntityPortalEnterEvent event) {
        if (core.getSmpManger().getDelayedOpeningManger().isNetherOpened()) {
            return;
        }

        if (event.getEntity() instanceof Player player) {
            UUID uuid = player.getUniqueId();
            long now = System.currentTimeMillis();

            if (messageCooldown.containsKey(uuid)) {
                long lastMessage = messageCooldown.get(uuid);
                if (now - lastMessage < cooldownMillis) {
                    event.setCancelled(true);
                    return;
                }
            }

            messageCooldown.put(uuid, now);
            if (event.getPortalType() == PortalType.ENDER) {
                player.sendMessage(MessageBuilder.buildOld(Core.PREFIX + "§7Das §dEnd §7wurde noch %rnicht §7geöffnet!"));
                return;
            }
            player.sendMessage(MessageBuilder.buildOld(Core.PREFIX + "§7Der §cNether §7wurde noch %rnicht §7geöffnet!"));
        }

        event.setCancelled(true);
    }


    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        if (event.getCurrentItem() != null && event.getCurrentItem().getItemMeta() != null && event.getCurrentItem().getItemMeta().getDisplayName().contains("§bTemporäre Elytra")) {
            event.setCancelled(true);
            event.getInventory().clear(event.getSlot());
            return;
        }

    }

    @EventHandler
    public void onInventoryClick(PlayerDropItemEvent event) {
        Player player = (Player) event.getPlayer();

        if (event.getItemDrop().getItemStack().getItemMeta() != null && event.getItemDrop().getItemStack().getItemMeta().getDisplayName().contains("§bTemporäre Elytra")) {
            event.setCancelled(true);
            return;
        }

    }

    @EventHandler
    public void onInventoryClick(PlayerSwapHandItemsEvent event) {
        Player player = (Player) event.getPlayer();

        if (event.getMainHandItem().getItemMeta() != null && event.getMainHandItem().getItemMeta().getDisplayName().contains("§bTemporäre Elytra")) {
            event.setCancelled(true);
            return;
        }

        if (event.getOffHandItem().getItemMeta() != null && event.getOffHandItem().getItemMeta().getDisplayName().contains("§bTemporäre Elytra")) {
            event.setCancelled(true);
            return;
        }

    }



    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {

        System.out.println("EntityDropItemEvent triggered");

        Player player = event.getPlayer();

        if (!event.getItemDrop().getItemStack().getType().equals(Material.DIAMOND)) return;

        Location locPlayer = player.getLocation();
        World world = locPlayer.getWorld();

        if (locPlayer.getY() >= 190 && locPlayer.getY() <= 200 && world.getEnvironment() == World.Environment.NORMAL) {

            int random = (int) (Math.random() * 100);
            Location itemLoc = event.getItemDrop().getLocation();

            if (random >= 10) {
                world.spawnParticle(Particle.SMOKE, itemLoc, 10);
                world.playSound(itemLoc, Sound.ENTITY_VILLAGER_NO, 1f, 1f);
                return;
            }

            event.getItemDrop().remove();

            world.spawnParticle(Particle.CLOUD, itemLoc, 30);
            world.playSound(itemLoc, Sound.ENTITY_ILLUSIONER_CAST_SPELL, 1f, 1f);

            ItemStack cloudCatalyst = new ItemBuilder(Material.IRON_HORSE_ARMOR)
                    .setDisplayName(MessageBuilder.build("<italic:false><color:#c5f5fc>Cloud Catalyst</color>"))
                    .setModel("smp:cloud_catalyst")
                    .addFlags(ItemFlag.HIDE_ATTRIBUTES)
                    .build();

            world.dropItemNaturally(itemLoc, cloudCatalyst);
        }
    }

    @EventHandler
    public void onEntityMount(EntityMountEvent event) {
        if (!(event.getMount() instanceof HappyGhast happyGhast)) {
            return;
       }

        happyGhast.getAttribute(Attribute.FLYING_SPEED).setBaseValue(0.1);
    }

    @EventHandler
    public void onEntityDismount(EntityDismountEvent event) {
        if (!(event.getDismounted() instanceof HappyGhast happyGhast)) {
            return;
        }

        happyGhast.getAttribute(Attribute.FLYING_SPEED).setBaseValue(0.05);
    }


    public static void setScale(ArrayList<String> scale) {
        scale = scale;
    }

    public static ArrayList<String> getScale() {
        return scale;
    }
}
