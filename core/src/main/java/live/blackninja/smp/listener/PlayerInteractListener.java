package live.blackninja.smp.listener;

import live.blackninja.smp.Core;
import live.blackninja.smp.builder.MessageBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.ArrayList;

public record PlayerInteractListener(Core core) implements Listener {

    public static ArrayList<String> scale = new ArrayList<>();

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (event.getClickedBlock().getType() == Material.END_PORTAL_FRAME) {
                event.setCancelled(true);
                player.sendMessage(MessageBuilder.buildOld(Core.PREFIX + "%7Das §dEnd %7wurde noch %rnicht %7geöffnet!"));
            }
        }
    }

    public static void setScale(ArrayList<String> scale) {
        scale = scale;
    }

    public static ArrayList<String> getScale() {
        return scale;
    }
}
