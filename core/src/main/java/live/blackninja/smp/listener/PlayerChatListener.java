package live.blackninja.smp.listener;

import io.papermc.paper.chat.ChatRenderer;
import io.papermc.paper.event.player.AsyncChatEvent;
import live.blackninja.smp.Core;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public record PlayerChatListener(Core core) implements Listener {

    @EventHandler
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        event.setFormat("§f" + player.getDisplayName() + "§8: §x§a§1§b§7§c§2" + event.getMessage().replace("&", "§"));
    }


}
