package live.blackninja.smp.listener;

import live.blackninja.smp.Core;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

public record ServerPingListener(Core core) implements Listener {

    @EventHandler
    public void onServerListPing(ServerListPingEvent event) {
        event.setMotd("       §8§l▎ §x§0§0§A§E§F§F§lB§x§0§7§B§2§F§B§ll§x§0§E§B§5§F§7§la§x§1§4§B§9§F§3§lc§x§1§B§B§D§E§E§lk§x§2§2§C§1§E§A§lN§x§2§9§C§4§E§6§li§x§3§0§C§8§E§2§ln§x§3§6§C§C§D§E§lj§x§3§D§C§F§D§A§la§x§4§4§D§3§D§6§l.§x§4§B§D§7§D§1§lL§x§5§1§D§B§C§D§li§x§5§8§D§E§C§9§lv§x§5§F§E§2§C§5§le §8§l» §r§7SMP-Server §8§l[§r§x§0§0§a§e§f§f1.21§8§l]\n" +
                "         §x§E§4§9§4§3§A\uD83D\uDD25 §x§E§4§8§7§3§Aᴍ§x§E§5§8§0§3§9ɪ§x§E§5§7§9§3§9ɴ§x§E§5§7§3§3§9ᴇ§x§E§5§6§C§3§9ᴄ§x§E§6§6§6§3§9ʀ§x§E§6§5§F§3§8ᴀ§x§E§6§5§8§3§8ꜰ§x§E§6§5§2§3§8ᴛ §x§E§7§4§4§3§7s§x§E§7§3§E§3§7ᴍ§x§E§7§3§7§3§7ᴘ §x§E§0§3§6§4§6s§x§D§C§3§6§4§Eᴇ§x§D§9§3§5§5§5ᴀ§x§D§5§3§5§5§Ds§x§D§1§3§5§6§4ᴏ§x§C§E§3§4§6§Cɴ §x§C§6§3§4§7§B5 §x§B§F§3§3§8§A\uD83D\uDD25");
    }
}
