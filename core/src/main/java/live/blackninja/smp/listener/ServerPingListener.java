package live.blackninja.smp.listener;

import live.blackninja.smp.Core;
import live.blackninja.smp.builder.MessageBuilder;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

public record ServerPingListener(Core core) implements Listener {

    @EventHandler
    public void onServerListPing(ServerListPingEvent event) {
        event.motd(MessageBuilder.build("             <white>BlackNinja.LIVE</white> <dark_gray>-</dark_gray> <gray>SMP Server   </gray>  <dark_gray>[</dark_gray><color:#00aeff>" + this.getVersion() + "</color><dark_gray>]</dark_gray>\n" +
                this.getServerState()));
    }

    private String getVersion() {
        return this.core.getServer().getMinecraftVersion();
    }

    private String getServerState() {
        boolean serverState = core.getSmpManger().getConfig().getConfig().getBoolean("ServerState");
        boolean devServer = core.getSmpManger().getConfig().getConfig().getBoolean("DevServer");
        if (devServer) {
            return "                 <color:#bf134a>SMP Developer Server</color>";
        }
        if (serverState) {
            return "                  <gradient:#ff3d2b:#9e3211>\uD83D\uDDE1 1.21.11 Release \uD83D\uDDE1</gradient>";
        }
        return "                     <color:#ff1231>⚠ ᴍᴀɪɴᴛᴇɴᴀɴᴄᴇ ⚠</color>";
    }
}
