package live.blackninja.webhook.listener;

import live.blackninja.smp.builder.MessageBuilder;
import live.blackninja.webhook.Webhook;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.HashMap;
import java.util.UUID;

public class PlayerChatListener implements Listener {

    private final HashMap<UUID, ChatDataManger> chatDataMap = new HashMap<>();

    private final int maxMessages = 5;
    private final long timeWindow = 1000 * 3;

    private final Webhook webhook;

    public PlayerChatListener(Webhook webhook) {
        this.webhook = webhook;
    }

    @EventHandler
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();

        ChatDataManger data = chatDataMap.getOrDefault(playerId, new ChatDataManger());
        long now = System.currentTimeMillis();

        data.removeOldMessages(now - timeWindow);

        data.addMessage(now);

        if (data.getMessageCount() > maxMessages) {
            event.setCancelled(true);
            player.sendMessage(MessageBuilder.build(Webhook.PREFIX + "<gray>Du hast die <%r>maximale Anzahl <gray>an <%y>Nachricht <gray>erreicht! Bitte <%y>warte <gray>einen Moment."));
        } else {
            chatDataMap.put(playerId, data);
            if (!webhook.getConfigManger().isEnabled()) {
                return;
            }
            webhook.getBot().getSyncChatManger().sendBotMessage(player, event.getMessage());
        }

    }


}
