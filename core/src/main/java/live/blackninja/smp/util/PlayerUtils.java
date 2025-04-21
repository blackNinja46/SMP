package live.blackninja.smp.util;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PlayerUtils {

    private Player player;
    private OfflinePlayer offlinePlayer;
    private String playerName;

    public PlayerUtils(String name) {
        this.playerName = name;

        this.player = Bukkit.getPlayer(playerName);
        this.offlinePlayer = Bukkit.getOfflinePlayer(playerName);
    }

    public UUID getUUID() {
        if (player.isOnline() && player != null) {
            return player.getUniqueId();
        }
        return offlinePlayer.getUniqueId();
    }

    public String getName() {
        if (player.isOnline() && player != null) {
            return player.getName();
        }
        return offlinePlayer.getName();
    }

    public Player getPlayer() {
        if (player.isOnline() && player != null) {
            return player.getPlayer();
        }
        return offlinePlayer.getPlayer();
    }
}
