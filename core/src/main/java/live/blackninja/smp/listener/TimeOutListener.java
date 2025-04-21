package live.blackninja.smp.listener;

import live.blackninja.smp.Core;
import live.blackninja.smp.manger.TimeOutManger;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

import java.util.UUID;

public record TimeOutListener(Core core) implements Listener {

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        TimeOutManger timeOutManger = core.getSmpManger().getTimeOutManger();

        if (!timeOutManger.isPlayerExist(uuid)) {
            return;
        }

        long expires = timeOutManger.getDuration(uuid);

        if (System.currentTimeMillis() < expires) {
            String reason = timeOutManger.getReason(uuid);
            String formatted = timeOutManger.getFormatedDuration(expires);
            event.disallow(
                    PlayerLoginEvent.Result.KICK_OTHER,
                    timeOutManger.getTimeOutMessage(reason, formatted)
            );
            return;
        }

        timeOutManger.unTimeOut(uuid);
    }


}
