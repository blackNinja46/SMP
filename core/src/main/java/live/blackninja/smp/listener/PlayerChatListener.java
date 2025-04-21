package live.blackninja.smp.listener;

import live.blackninja.smp.Core;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public record PlayerChatListener(Core core) implements Listener {

    @EventHandler
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        event.setFormat("§f" + player.getDisplayName() + "§8: §f" + event.getMessage().replace("&", "§")
                .replace(":fire:", "\uEfaa"));

    }
}
