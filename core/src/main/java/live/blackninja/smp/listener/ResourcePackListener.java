package live.blackninja.smp.listener;

import live.blackninja.smp.Core;
import live.blackninja.smp.builder.MessageBuilder;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;

public record ResourcePackListener(Core core) implements Listener {

    @EventHandler
    public void onPlayerResourcePackStatus(PlayerResourcePackStatusEvent event) {
        Player player = event.getPlayer();

        if (event.getStatus() == PlayerResourcePackStatusEvent.Status.SUCCESSFULLY_LOADED) {
            player.sendMessage(MessageBuilder.buildOld("§8| §9RP §8%> §7Das Resourcepack wurde erfolgreich geladen."));
        } else if (event.getStatus() == PlayerResourcePackStatusEvent.Status.DISCARDED) {
            player.sendMessage(MessageBuilder.buildOld("§8| §9RP §8%> §7Du hast das Resourcepack abgelehnt."));
        } else if (event.getStatus() == PlayerResourcePackStatusEvent.Status.FAILED_DOWNLOAD) {
            player.sendMessage(MessageBuilder.buildOld("§8| §9RP §8%> §7Fehler beim Download des Resourcepacks."));
        }else if (event.getStatus() == PlayerResourcePackStatusEvent.Status.ACCEPTED) {
            player.sendMessage(MessageBuilder.buildOld("§8| §9RP §8%> §7Du hast das Resourcepack akzeptiert."));
        }

    }


}
