package live.blackninja.smp.manger;

import live.blackninja.smp.Core;
import live.blackninja.smp.builder.ItemBuilder;
import live.blackninja.smp.builder.MessageBuilder;
import live.blackninja.smp.builder.TextDisplayBuilder;
import live.blackninja.smp.util.EntityGlow;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.joml.AxisAngle4f;
import org.joml.Random;
import org.joml.Vector3f;

public class DragonEggManger {

    protected final Core core;
    protected final String dragonEggTag = "dragon_egg.info";
    protected final String dragonEggBlockDisplayTag = "dragon_egg.block_display";
    protected final String dragonEggInteractionTag = "dragon_egg.interaction";

    protected final BossBar eggBreakStateBar;


    public DragonEggManger(Core core) {
        this.core = core;
        this.eggBreakStateBar = BossBar.bossBar(Component.text("N/A"), 0f, BossBar.Color.PURPLE, BossBar.Overlay.NOTCHED_10);

        startTask();
        startRotationTask();
        System.out.println("Particles started");
    }

    public void startRotationTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (World world : Bukkit.getWorlds()) {
                    for (Entity entity : world.getEntities()) {
                        if (entity instanceof BlockDisplay blockDisplay && blockDisplay.getScoreboardTags().contains(dragonEggBlockDisplayTag)) {
                            if (!blockDisplay.isValid()) {
                                cancel();
                                return;
                            }

                            blockDisplay.setRotation(blockDisplay.getYaw() + 3, 0);
                        }
                    }
                }
            }
        }.runTaskTimer(core, 0L, 1L);
    }

    private void startTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (!player.getInventory().contains(Material.DRAGON_EGG)) {
                        player.setGlowing(false);
                        continue;
                    }
                    player.setGlowing(true);
                }

                for (World world : Bukkit.getWorlds()) {
                    for (Entity entity : world.getEntities()) {
                        if (entity instanceof BlockDisplay blockDisplay && blockDisplay.getScoreboardTags().contains(dragonEggBlockDisplayTag)) {
                            if (!blockDisplay.isValid()) {
                                cancel();
                                return;
                            }
                            updateEggDisplay();
                            continue;
                        }

                        if (entity instanceof Item item && item.getItemStack().getType() == Material.DRAGON_EGG) {
                            EntityGlow glow = new EntityGlow(item);

                            glow.setGlowing(NamedTextColor.DARK_PURPLE);
                            item.setUnlimitedLifetime(true);
                            entity.setGlowing(true);
                            entity.setInvulnerable(true);
                            entity.setPersistent(true);
                            entity.setGravity(false);

                            item.getWorld().spawnParticle(
                                    Particle.DRAGON_BREATH,
                                    item.getLocation().add(0, 0.3, 0),
                                    5,
                                    0.3, 0.3, 0.3,
                                    0.01,
                                    0.0f
                            );

                            item.getWorld().spawnParticle(
                                    Particle.END_ROD,
                                    item.getLocation().add(0, 0.2, 0),
                                    5,
                                    0.1, 0.3, 0.1,
                                    0.01
                            );
                        }
                    }
                }

                if (getDragonEgg() != null) {
                    spawnDragonEggSphere(getDragonEgg().getLocation());
                }
            }
        }.runTaskTimer(core, 0L, 10L);
    }


    public BlockDisplay getDragonEgg() {
        for (Entity entity : Bukkit.getWorlds().getFirst().getEntities()) {
            if (entity instanceof BlockDisplay blockDisplay && blockDisplay.getScoreboardTags().contains(dragonEggBlockDisplayTag)) {
                return blockDisplay;
            }
        }
        return null;
    }

    public TextDisplay getDragonEggTextDisplay() {
        for (Entity entity : Bukkit.getWorlds().getFirst().getEntities()) {
            if (entity instanceof TextDisplay textDisplay && textDisplay.getScoreboardTags().contains(dragonEggTag)) {
                return textDisplay;
            }
        }
        return null;
    }

    public Interaction getDragonEggInteraction() {
        for (Entity entity : Bukkit.getWorlds().getFirst().getEntities()) {
            if (entity instanceof Interaction interaction && interaction.getScoreboardTags().contains(dragonEggInteractionTag)) {
                return interaction;
            }
        }
        return null;
    }

    public void spawnArena(Location location) {

        Location displayLocation = new Location(location.getWorld(), location.getBlockX() + 0.5, location.getBlockY() + 0.5, location.getBlockZ() + 0.5, location.getYaw(), location.getPitch());

        BlockDisplay blockDisplay = location.getWorld().spawn(displayLocation, BlockDisplay.class);
        EntityGlow glow = new EntityGlow(blockDisplay);

        blockDisplay.setBlock(Material.DRAGON_EGG.createBlockData());
        blockDisplay.setInvulnerable(true);
        blockDisplay.setGravity(false);
        blockDisplay.setPersistent(true);
        blockDisplay.addScoreboardTag(dragonEggBlockDisplayTag);

        Transformation transformation = blockDisplay.getTransformation();
        transformation.getTranslation().set(new Vector3f(-0.5F, -0.5F, -0.5F));
        blockDisplay.setTransformation(transformation);

        glow.setGlowing(NamedTextColor.DARK_PURPLE);

        Interaction interaction = location.getWorld().spawn(displayLocation.add(new Vector(0, -0.5, 0)), Interaction.class);
        interaction.addScoreboardTag(dragonEggInteractionTag);
        interaction.setGravity(false);
        interaction.setInvulnerable(true);
        interaction.setPersistent(true);

        updateEggDisplay();
    }

    private String getPlayerInRangeText(Location location) {
        int playerInRange = this.getPlayerInRange(location);

        if (playerInRange == 1) {
            return "<light_purple>" + playerInRange + " \uD83D\uDC64 ✔</light_purple>";
        }
        return "<red>" + playerInRange + " \uD83D\uDC64</red>";
    }

    public int getPlayerInRange(Location location) {
        return location.getNearbyEntities(20, 20, 20).stream()
                .filter(entity -> entity instanceof Player)
                .mapToInt(entity -> 1)
                .sum();
    }

    public void updateEggDisplay() {
        BlockDisplay egg = this.getDragonEgg();
        if (egg == null) return;

        Location loc = egg.getLocation().add(0, 1, 0);
        TextDisplay display = getOrCreateEggDisplay(loc);
        EntityGlow glow = new EntityGlow(egg);

        display.text(MessageBuilder.build(getPlayerInRangeText(loc)));
        eggBreakStateBar.name(MessageBuilder.build(getPlayerInRangeText(loc)));

        if (this.getPlayerInRange(loc) == 1) {
            glow.setGlowing(NamedTextColor.DARK_PURPLE);
            return;
        }
        glow.setGlowing(NamedTextColor.DARK_RED);
    }

    public TextDisplay getOrCreateEggDisplay(Location location) {
        for (Entity entity : location.getWorld().getEntities()) {
            if (entity instanceof TextDisplay textDisplay &&
                    textDisplay.getScoreboardTags().contains(dragonEggTag)) {
                return textDisplay;
            }
        }

        TextDisplay textDisplay = new TextDisplayBuilder(location)
                .setTextMiniMessage(MessageBuilder.build(this.getPlayerInRangeText(location)))
                .setAlignment(TextDisplay.TextAlignment.CENTER)
                .setBillboard(TextDisplay.Billboard.CENTER)
                .setInvisibleBackground(true)
                .setShadowed(true)
                .setTag(this.dragonEggTag)
                .setRotation(location.getYaw(), 0)
                .build();

        Transformation transformation = textDisplay.getTransformation();
        transformation.getScale().set(2f, 2f, 2f);

        textDisplay.setTransformation(transformation);

        return textDisplay;
    }

    public boolean isPlayerInRange(Player player, Location location) {
        return location.getNearbyEntities(20, 20, 20).stream()
                .anyMatch(entity -> entity instanceof Player target && target.getName().equals(player.getName()));
    }

    public void teleportDragonEgg(Location location) {
        BlockDisplay egg = this.getDragonEgg();
        Interaction interaction = this.getDragonEggInteraction();
        TextDisplay display = this.getDragonEggTextDisplay();

        if (egg == null || interaction == null || display == null) return;

        egg.remove();
        interaction.remove();
        display.remove();

        spawnArena(location);
    }

    public Location getRandomAirLocation(Location center) {
        World world = center.getWorld();
        Random random = new Random();

        int radiusX = 15;
        int radiusY = 7;
        int radiusZ = 15;

        for (int i = 0; i < 200; i++) {
            int x = center.getBlockX() + random.nextInt(radiusX * 2 + 1) - radiusX;
            int y = center.getBlockY() + random.nextInt(radiusY * 2 + 1) - radiusY;
            int z = center.getBlockZ() + random.nextInt(radiusZ * 2 + 1) - radiusZ;

            Block block = world.getBlockAt(x, y, z);

            if (!block.getType().isAir()) continue;

            Block below = block.getRelative(0, -1, 0);
            if (below.getY() <= world.getMinHeight()) continue;

            return block.getLocation().add(0.5, 0.5, 0.5);
        }

        return null;
    }

    public void spawnDragonEggTeleportParticles(Location loc) {
        World world = loc.getWorld();

        world.spawnParticle(Particle.FLASH, loc, 1, Color.PURPLE);

        world.spawnParticle(
                Particle.PORTAL,
                loc,
                100,
                0.7, 0.7, 0.7,
                0.3
        );

        world.spawnParticle(
                Particle.DRAGON_BREATH,
                loc,
                40,
                0.4, 0.4, 0.4,
                0.01,
                0.0f
        );

        world.spawnParticle(
                Particle.SMOKE,
                loc,
                12,
                0.2, 0.2, 0.2,
                0.01
        );

        for (int i = 0; i < 15; i++) {
            Location rand = loc.clone().add(
                    (Math.random() - 0.5) * 1.4,
                    (Math.random() - 0.5) * 1.2,
                    (Math.random() - 0.5) * 1.4
            );

            world.spawnParticle(Particle.END_ROD, rand, 1);
        }
    }

    public void spawnDragonEggSphere(Location center) {
        World world = center.getWorld();
        double radius = 20.0;

        int rings = 40;
        int pointsPerRing = 50;

        for (int i = 0; i < rings; i++) {
            double theta = Math.PI * i / (rings - 1);

            double ringRadius = Math.sin(theta) * radius;
            double y = Math.cos(theta) * radius;

            for (int j = 0; j < pointsPerRing; j++) {
                double phi = 2 * Math.PI * j / pointsPerRing;

                double x = Math.cos(phi) * ringRadius;
                double z = Math.sin(phi) * ringRadius;

                Location particleLoc = center.clone().add(x, y, z);

                world.spawnParticle(
                        Particle.DUST,
                        particleLoc,
                        3,
                        0.05, 0.05, 0.05,
                        0.01,
                        new Particle.DustOptions(Color.FUCHSIA, 1.0f)
                );
            }
        }
    }




    public BossBar getEggBreakStateBar() {
        return eggBreakStateBar;
    }

    public String getDragonEggBlockDisplayTag() {
        return dragonEggBlockDisplayTag;
    }

    public String getDragonEggTag() {
        return dragonEggTag;
    }

    public String getDragonEggInteractionTag() {
        return dragonEggInteractionTag;
    }
}
