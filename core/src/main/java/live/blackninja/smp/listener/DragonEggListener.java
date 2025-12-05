package live.blackninja.smp.listener;

import live.blackninja.smp.Core;
import live.blackninja.smp.builder.ItemBuilder;
import live.blackninja.smp.builder.MessageBuilder;
import live.blackninja.smp.util.EntityGlow;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.*;

public class DragonEggListener implements Listener {

    private final Core core;

    private final Set<UUID> particleCooldown = new HashSet<>();
    private final Map<UUID, TextDisplay> eggDisplays = new HashMap<>();
    private final Set<UUID> processedEggs = new HashSet<>();
    private ItemStack clicked;

    public DragonEggListener(Core core) {
        this.core = core;

        Bukkit.getScheduler().runTaskTimer(core, this::tickDragonEggs, 0L, 20L);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        ItemStack clicked = event.getCurrentItem();
        ItemStack cursor = event.getCursor();

        // SHIFT + Klick -> Versuch Item zu verschieben
        if (event.isShiftClick() && clicked != null && clicked.getType() == Material.DRAGON_EGG) {
            event.setCancelled(true);
            player.sendActionBar("✕ Das Drachen-Ei darf nicht verschoben werden.");
            return;
        }

        // Bundle ↔ Egg
        if (isEgg(clicked) && isBundle(cursor) || isEgg(cursor) && isBundle(clicked)) {
            event.setCancelled(true);
            player.sendActionBar("✕ Das Drachen-Ei darf nicht in Bundles gelegt werden.");
            return;
        }
    }

    private boolean isEgg(ItemStack stack) {
        return stack != null && stack.getType() == Material.DRAGON_EGG;
    }

    private boolean isBundle(ItemStack stack) {
        return stack != null && stack.getType() == Material.BUNDLE;
    }


    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        ItemStack cursor = event.getOldCursor();

        if (cursor == null || cursor.getType() != Material.DRAGON_EGG) return;

        for (int slot : event.getRawSlots()) {
            ItemStack item = event.getView().getItem(slot);

            if (item != null && item.getType() == Material.BUNDLE) {
                event.setCancelled(true);
                player.sendActionBar(MessageBuilder.build("<color:#ff0044>✕ Das Drachen-Ei darf nicht in Bundles gelegt werden."));
                return;
            }

            InventoryType type = event.getView().getInventory(slot).getType();
            if (type == InventoryType.ENDER_CHEST || type == InventoryType.SHULKER_BOX) {
                event.setCancelled(true);
                player.sendActionBar(MessageBuilder.build("<color:#ff0044>✕ Das Drachen-Ei darf nicht in Container gezogen werden."));
                return;
            }
        }
    }


    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (!player.getInventory().contains(Material.DRAGON_EGG)) return;

        player.getInventory().remove(Material.DRAGON_EGG);

        Item dragonEgg = (Item) player.getWorld().spawnEntity(player.getLocation(), EntityType.ITEM);
        EntityGlow glow = new EntityGlow(dragonEgg);

        dragonEgg.setItemStack(new ItemBuilder(Material.DRAGON_EGG).build());

        glow.setGlowing(NamedTextColor.DARK_PURPLE);
        dragonEgg.setUnlimitedLifetime(true);
        dragonEgg.setGlowing(true);
        dragonEgg.setInvulnerable(true);
        dragonEgg.setPersistent(true);
        dragonEgg.setGravity(false);

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

        if (core.getDragonEggManger().getDragonEgg() != null) {
            if (core.getDragonEggManger().isPlayerInRange(player, core.getDragonEggManger().getDragonEgg().getLocation())) {
                player.showBossBar(core.getDragonEggManger().getEggBreakStateBar());
                return;
            }
            player.hideBossBar(core.getDragonEggManger().getEggBreakStateBar());
            return;
        }

        if (!player.getInventory().contains(Material.DRAGON_EGG)) return;

        if (!particleCooldown.add(player.getUniqueId())) return;

        Bukkit.getScheduler().runTaskLater(core, () ->
                particleCooldown.remove(player.getUniqueId()), 6L);

        player.getWorld().spawnParticle(
                Particle.DRAGON_BREATH,
                player.getLocation().add(0, -0.5, 0),
                2, 0.5, 0, 0.5, 0.01, 0.0f
        );
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();

        if (block == null) {
            return;
        }

        if (block.getType() != Material.DRAGON_EGG) {
            return;
        }

        if (event.getAction() != Action.LEFT_CLICK_BLOCK) {
            return;
        }

        block.setType(Material.AIR);
        event.setCancelled(true);

        Bukkit.getScheduler().runTaskLater(core, () -> {
            if (block.getType() != Material.DRAGON_EGG) {
                core.getDragonEggManger().spawnArena(block.getLocation());
                for (Player players : Bukkit.getOnlinePlayers()) {
                    players.playSound(block.getLocation(), Sound.BLOCK_BEACON_POWER_SELECT, 5f, 1f);
                }
            }
        }, 1L);

    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();

        if (!(event.getRightClicked() instanceof Interaction interaction && interaction.getScoreboardTags().contains(core.getDragonEggManger().getDragonEggInteractionTag()))) {
            return;
        }

        Location location = core.getDragonEggManger().getRandomAirLocation(interaction.getLocation());

        if (location == null) {
            return;
        }

        core.getDragonEggManger().teleportDragonEgg(location);

        core.getDragonEggManger().spawnDragonEggTeleportParticles(location);

        for (Player players : Bukkit.getOnlinePlayers()) {
            players.playSound(location, Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 1f);
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Interaction interaction && interaction.getScoreboardTags().contains(core.getDragonEggManger().getDragonEggInteractionTag()))) {
            return;
        }

        if (!(event.getDamager() instanceof Player player)) {
            return;
        }

        if (!(core.getDragonEggManger().getPlayerInRange(interaction.getLocation()) == 1)) {
            return;
        }

        event.setCancelled(true);
        float progress = core.getDragonEggManger().getEggBreakStateBar().progress();
        float newProgress = Math.min(1.0f, progress + 0.1f);
        core.getDragonEggManger().getEggBreakStateBar().progress(newProgress);

        player.playSound(interaction.getLocation(), Sound.PARTICLE_SOUL_ESCAPE, 5f, 1.0f);

        if (newProgress >= 1.0f) {
            for (Entity entity : interaction.getNearbyEntities(20, 20, 20)) {
                if (entity instanceof Player players) {
                    players.hideBossBar(core.getDragonEggManger().getEggBreakStateBar());
                }
            }

            interaction.remove();
            core.getDragonEggManger().getEggBreakStateBar().progress(0);

            core.getDragonEggManger().getDragonEgg().remove();
            core.getDragonEggManger().getDragonEggTextDisplay().remove();

            Item dragonEgg = (Item) interaction.getWorld().spawnEntity(interaction.getLocation(), EntityType.ITEM);
            dragonEgg.setItemStack(new ItemBuilder(Material.DRAGON_EGG).build());
            player.playSound(interaction.getLocation(), Sound.ENTITY_DRAGON_FIREBALL_EXPLODE, 1.0f, 1.0f);
            interaction.getWorld().spawnParticle(Particle.EXPLOSION_EMITTER, interaction.getLocation(), 1, 0.1f, 0.1f, 0.1f, 10f);
        }

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

        EntityGlow glow = new EntityGlow(item);
        if (glow.isGlowing()) glow.removeGlowing();

        player.getWorld().spawnParticle(
                Particle.DRAGON_BREATH,
                player.getLocation().add(0, 1, 0),
                30, 0.3, 0.3, 0.3, 0.01, 0.0f
        );

        TextDisplay display = eggDisplays.remove(item.getUniqueId());
        if (display != null && display.isValid()) display.remove();
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

    @EventHandler
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {

        if (!(event.getEntity() instanceof FallingBlock fallingBlock)) {
            return;
        }

        if (fallingBlock.getBlockData().getMaterial() != Material.DRAGON_EGG) {
            return;
        }

        if (processedEggs.contains(fallingBlock.getUniqueId())) {
            return;
        }

        processedEggs.add(fallingBlock.getUniqueId());

        Location loc = event.getBlock().getLocation();

        fallingBlock.remove();
        event.setCancelled(true);
        event.getBlock().setType(Material.AIR);

        core.getDragonEggManger().spawnArena(loc);

        Bukkit.getOnlinePlayers().forEach(player ->
                player.playSound(loc, Sound.BLOCK_BEACON_POWER_SELECT, 5f, 1f)
        );
    }

    private int getWorldHeight(World world) {
        return world.getName().equals("world") ? -61 : 0;
    }
}
