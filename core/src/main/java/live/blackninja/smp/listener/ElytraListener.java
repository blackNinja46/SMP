package live.blackninja.smp.listener;

import live.blackninja.smp.Core;
import live.blackninja.smp.builder.ItemBuilder;
import live.blackninja.smp.builder.MessageBuilder;
import live.blackninja.smp.manger.ElytraManger;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.checkerframework.checker.units.qual.C;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public record ElytraListener(Core core) implements Listener {

    private static final Set<UUID> glided = new HashSet<>();

    @EventHandler
    public void onEntityToggleGlide(EntityToggleGlideEvent event) {
        ElytraManger elytraManger = core.getSmpManger().getElytraManger();
        if (!(event.getEntity() instanceof Player)) return;

        Player player = (Player) event.getEntity();
        UUID uuid = player.getUniqueId();

        if (event.isGliding()) {
            glided.add(uuid);
        } else {
            if (glided.remove(uuid)) {
                if (!player.getInventory().getChestplate().getItemMeta().getDisplayName().equals("§bTemporäre Elytra")) {
                    return;
                }
                player.getInventory().setChestplate(null);
                player.sendMessage(MessageBuilder.buildOld(Core.PREFIX + "§7Deine %btemporäre Elytra §7ist nun §cverbraucht§7!"));
            }
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (
                player.getLocation().add(0, -1, 0).getBlock().getType() == Material.SLIME_BLOCK &&
                player.getLocation().add(0, -3, 0).getBlock().getType() == Material.TARGET &&
                player.getLocation().add(0, -4, 0).getBlock().getType() == Material.BEDROCK
        ) {
            player.setVelocity(player.getLocation().getDirection().setY(5));
            player.playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 1.0f, 1.0f);
            if (Objects.requireNonNull(player.getInventory().getChestplate()).isEmpty()) {
                player.getInventory().setChestplate(new ItemBuilder(Material.ELYTRA).setDisplayName("§bTemporäre Elytra").build());
            }
        }
    }

}
