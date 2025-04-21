package live.blackninja.smp.manger;

import live.blackninja.smp.Core;
import org.bukkit.entity.Player;

public class RankManger {

    private Core core;

    public RankManger(Core core) {
        this.core = core;
    }

    public void updateRanks(String playerName) {
        Player player = core.getServer().getPlayer(playerName);
        StatusManger statusManger = core.getSmpManger().getStatusManger();
        if (player.hasPermission("ninjasmp.rank.admin")) {
            if (statusManger.hasPlayerStatus(player)) {
                player.setPlayerListName("§f\uEfd1 §x§f§f§0§0§0§0" + player.getName() + " §8[" + statusManger.getStatusDisplay(statusManger.getStatus(player)) + "§8]");
                player.setDisplayName("§f\uEfd1 §x§f§f§0§0§0§0" + player.getName() + " §8[" + statusManger.getStatusDisplay(statusManger.getStatus(player)) + "§8]");
                return;
            }
            player.setPlayerListName("§f\uEfd1 §x§f§f§0§0§0§0" + player.getName());
            player.setDisplayName("§f\uEfd1 §x§f§f§0§0§0§0" + player.getName());
            return;
        }
        if (player.hasPermission("ninjasmp.rank.moderator")) {
            if (statusManger.hasPlayerStatus(player)) {
                player.setPlayerListName("§f\uEfd2 §x§f§f§0§0§0§0" + player.getName() + " §8[" + statusManger.getStatusDisplay(statusManger.getStatus(player)) + "§8]");
                player.setDisplayName("§f\uEfd2 §x§f§f§0§0§0§0" + player.getName() + " §8[" + statusManger.getStatusDisplay(statusManger.getStatus(player)) + "§8]");
                return;
            }
            player.setPlayerListName("§f\uEfd2 §x§f§f§0§0§0§0" + player.getName());
            player.setDisplayName("§f\uEfd2 §x§f§f§0§0§0§0" + player.getName());
            return;
        }

        if (player.hasPermission("ninjasmp.rank.vip")) {
            if (statusManger.hasPlayerStatus(player)) {
                player.setPlayerListName("§f\uEfd3 §x§D§6§0§0§D§3" + player.getName() + " §8[" + statusManger.getStatusDisplay(statusManger.getStatus(player)) + "§8]");
                player.setDisplayName("§f\uEfd3 §x§D§6§0§0§D§3" + player.getName() + " §8[" + statusManger.getStatusDisplay(statusManger.getStatus(player)) + "§8]");
                return;
            }
            player.setPlayerListName("§f\uEfd3 §x§D§6§0§0§D§3" + player.getName());
            player.setDisplayName("§f\uEfd3 §x§D§6§0§0§D§3" + player.getName());
            return;
        }

        if (player.hasPermission("ninjasmp.rank.ninja+")) {
            if (statusManger.hasPlayerStatus(player)) {
                player.setPlayerListName("§f\uEfd4 §x§0§0§A§E§D§3" + player.getName() + " §8[" + statusManger.getStatusDisplay(statusManger.getStatus(player)) + "§8]");
                player.setDisplayName("§f\uEfd4 §x§0§0§A§E§D§3" + player.getName() + " §8[" + statusManger.getStatusDisplay(statusManger.getStatus(player)) + "§8]");
                return;
            }
            player.setPlayerListName("§f\uEfd4 §x§0§0§A§E§D§3" + player.getName());
            player.setDisplayName("§f\uEfd4 §x§0§0§A§E§D§3" + player.getName());
            return;
        }
        if (statusManger.hasPlayerStatus(player)) {
            player.setPlayerListName("§f\uEfd5 §x§f§7§c§7§0§5" + player.getName() + " §8[" + statusManger.getStatusDisplay(statusManger.getStatus(player)) + "§8]");
            player.setDisplayName("§f\uEfd5 §x§f§7§c§7§0§5" + player.getName() + " §8[" + statusManger.getStatusDisplay(statusManger.getStatus(player)) + "§8]");
            return;
        }
        player.setPlayerListName("§f\uEfd5 §x§f§7§c§7§0§5" + player.getName());
        player.setDisplayName("§f\uEfd5 §x§f§7§c§7§0§5" + player.getName());
        return;


    }


}
