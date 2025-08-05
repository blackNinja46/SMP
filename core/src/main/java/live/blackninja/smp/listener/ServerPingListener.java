package live.blackninja.smp.listener;

import live.blackninja.smp.Core;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

public record ServerPingListener(Core core) implements Listener {

    @EventHandler
    public void onServerListPing(ServerListPingEvent event) {
        event.setMotd("       §8§l▎ §x§0§0§A§E§F§F§lB§x§0§7§B§2§F§B§ll§x§0§E§B§5§F§7§la§x§1§4§B§9§F§3§lc§x§1§B§B§D§E§E§lk§x§2§2§C§1§E§A§lN§x§2§9§C§4§E§6§li§x§3§0§C§8§E§2§ln§x§3§6§C§C§D§E§lj§x§3§D§C§F§D§A§la§x§4§4§D§3§D§6§l.§x§4§B§D§7§D§1§lL§x§5§1§D§B§C§D§li§x§5§8§D§E§C§9§lv§x§5§F§E§2§C§5§le §8§l» §r§7SMP-Server §8§l[§r§x§0§0§a§e§f§f1.21§8§l]\n" +
                "                  §x§4§F§F§F§4§51§x§4§D§F§A§4§B.§x§4§C§F§5§5§02§x§4§A§F§0§5§61§x§4§9§E§A§5§C.§x§4§7§E§5§6§28 §x§4§4§D§B§6§Dʀ§x§4§4§D§B§6§Dᴇ§x§4§4§D§B§6§Dʟ§x§4§4§D§B§6§Dᴇ§x§4§4§D§B§6§Dᴀ§x§4§4§D§B§6§Ds§x§4§4§D§B§6§Dᴇ");
    }
}
