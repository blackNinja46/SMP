package live.blackninja.smp.listener;

import live.blackninja.smp.Core;
import live.blackninja.smp.builder.MessageBuilder;
import live.blackninja.smp.manger.StatType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public record PlayerDeathListener(Core core) implements Listener {

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        event.setDeathMessage(MessageBuilder.buildOld("%r☠ " + event.getDeathMessage()));

        core.getStatsManger().addStat(event.getPlayer().getUniqueId(), StatType.DEATHS, 1);

        if (event.getPlayer().getKiller() instanceof Player killer) {
            core.getStatsManger().addStat(killer.getUniqueId(), StatType.KILLS, 1);
        }

    }


}
