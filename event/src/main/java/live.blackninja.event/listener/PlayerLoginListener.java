package live.blackninja.event.listener;

import live.blackninja.smp.Core;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

public record PlayerLoginListener(Core core) implements Listener {

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();
        if (!core.getSmpManger().getConfig().getConfig().getBoolean("ServerState")) {
            if (player.hasPermission("ninjasmp.event.whitelist.bypass")) {
                return;
            }

            MiniMessage miniMessage = MiniMessage.miniMessage();
            event.disallow(PlayerLoginEvent.Result.KICK_WHITELIST, miniMessage.deserialize("<dark_gray>*--------------*</dark_gray> <color:#4498db>SMP Event</color> <dark_gray>*--------------*</dark_gray>\n \n<color:#ff004c>Der Server ist akutell geschlossen!</color>\n<color:#ff004c>Für Informationen zu</color> <color:#ffc800>Season 5</color>\n<color:#ff004c>besuche unseren Discord.</color>\n\n<dark_gray>*----------------------------------------*</dark_gray>"));
        }

    }

}
