package live.blackninja.smp.manger;

import live.blackninja.smp.Core;
import live.blackninja.smp.builder.MessageBuilder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TpaManger {

    private Core core;

    public TpaManger(Core core) {
        this.core = core;
    }

    public static final String PREFIX = "§8[§f\uEfe2§8] §r";

    public Map<UUID, UUID> tpaRequests = new HashMap<>(); // Ziel -> Anfragender
    private Map<UUID, Integer> tpaTimeouts = new HashMap<>(); // Zum Canceln der Ablauf-Tasks

    public void sendTpaRequest(Player target, Player player) {
        tpaRequests.put(target.getUniqueId(), player.getUniqueId());

        player.sendMessage(MessageBuilder.buildOld(PREFIX + "§7Anfrage an %b" + target.getName() + " §7gesendet."));
        target.sendMessage(MessageBuilder.buildOld(PREFIX + "%b" + player.getName() + " §7möchte sich zu dir teleportieren."));
        target.sendMessage(MessageBuilder.build("<click:run_command:'/tpa accept " + player.getName() + "'><hover:show_text:'<color:#00ddff>[KLICKE ZUM AKZEPTIEREN]</color>'><color:#00ddff>[KLICKE]</color></hover></click> <gray>or benutze</gray> <color:#00ddff>/tpaccept " + player.getName() + "</color>"));

        int taskId = Bukkit.getScheduler().scheduleSyncDelayedTask(core, () -> {
            if (tpaRequests.containsKey(target.getUniqueId()) &&
                    tpaRequests.get(target.getUniqueId()).equals(player.getUniqueId())) {
                tpaRequests.remove(target.getUniqueId());
                tpaTimeouts.remove(target.getUniqueId());
                player.sendMessage(MessageBuilder.buildOld("§7Deine %bAnfrage §7an %b" + target.getName() + " §7ist abgelaufen."));
                target.sendMessage(MessageBuilder.buildOld("§7Die %bAnfrage von %b" + player.getName() + " §7ist abgelaufen."));
            }
        }, 20L * 120); // 120 Sekunden

        tpaTimeouts.put(target.getUniqueId(), taskId);
    }

    public void cancelTpaRequest(UUID targetUUID) {
        tpaRequests.remove(targetUUID);
        if (tpaTimeouts.containsKey(targetUUID)) {
            Bukkit.getScheduler().cancelTask(tpaTimeouts.get(targetUUID));
            tpaTimeouts.remove(targetUUID);
        }
    }

    public void acceptRequest(Player requester, Player player) {
        core.getSmpManger().teleport(requester, player.getLocation());
        requester.sendMessage(MessageBuilder.buildOld(TpaManger.PREFIX + "§7Deine %bAnfrage §7an %b" + player.getName() + " §7wurde akzeptiert"));
        player.sendMessage(MessageBuilder.buildOld(TpaManger.PREFIX + "§7Du hast die %bAnfrage §7von %b" + requester.getName() + " §7akzeptiert"));

        cancelTpaRequest(player.getUniqueId());
    }

    public void denyRequest(Player requester, Player player) {
        requester.sendMessage(MessageBuilder.buildOld(TpaManger.PREFIX + "§7Deine %bAnfrage an %b" + player.getName() + " §7wurde abgelehnt"));
        player.sendMessage(MessageBuilder.buildOld(TpaManger.PREFIX + "§7Du hast die %bAnfrage §7von %b" + requester.getName() + " §7abgelehnt"));

        cancelTpaRequest(player.getUniqueId());
    }

    public Map<UUID, Integer> getTpaTimeouts() {
        return tpaTimeouts;
    }

    public Map<UUID, UUID> getTpaRequests() {
        return tpaRequests;
    }
}
