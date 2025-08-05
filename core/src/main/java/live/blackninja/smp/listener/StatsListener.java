package live.blackninja.smp.listener;

import live.blackninja.smp.Core;
import live.blackninja.smp.manger.StatType;
import live.blackninja.smp.manger.StatsManger;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.UUID;

public record StatsListener(Core core, StatsManger statsManger) implements Listener {


    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {

        if (!(event.getEntity() instanceof Player) && event.getEntity().getKiller() != null) {
            statsManger.addStat(event.getEntity().getKiller().getUniqueId(), StatType.MOBS_KILLED, 1);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();

        if (event.getBlock().getType().isBlock()) {
            statsManger.addStat(playerUUID, StatType.BLOCKS_BROKEN, 1);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();

        if (event.getBlockPlaced().getType().isBlock()) {
            statsManger.addStat(playerUUID, StatType.BLOCKS_PLACED, 1);
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();

        if (event.getFrom().distance(event.getTo()) > 1) {
            statsManger.addStat(playerUUID, StatType.DISTANCE_WALKED, 1);
        }
    }

}
