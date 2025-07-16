package live.blackninja.smp.manger;

import live.blackninja.smp.Core;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class RankManger {

    private Core core;

    public RankManger(Core core) {
        this.core = core;
    }

    public void updateRanks(String playerName) {
        Player player = core.getServer().getPlayer(playerName);
        if (player == null) return;

        StatusManger statusManger = core.getSmpManger().getStatusManger();

        LuckPerms luckPerms = Bukkit.getServicesManager().getRegistration(LuckPerms.class).getProvider();
        User user = luckPerms.getUserManager().getUser(player.getUniqueId());
        if (user == null) return;

        String group = user.getPrimaryGroup();

        String symbol = "§f\uEfd5"; // Default Symbol (Spieler)
        String color = "§7";        // Default Farbe
        int weight = 1;

        switch (group.toLowerCase()) {
            case "admin":
                symbol = "§f\uEfd1";
                color = "§x§f§f§0§0§0§0";
                weight = 100;
                break;
            case "moderator":
                symbol = "§f\uEfd2";
                color = "§x§f§f§0§0§0§0";
                weight = 90;
                break;
            case "supporter":
                symbol = "§f\uEfd6";
                color = "§x§f§f§0§0§0§0";
                weight = 80;
                break;
            case "vip":
                symbol = "§f\uEfd3";
                color = "§x§D§6§0§0§D§3";
                weight = 70;
                break;
            case "ninja+":
            case "ninjaplus":
                symbol = "§f\uEfd4";
                color = "§x§0§0§A§E§D§3";
                weight = 60;
                break;
            case "cam":
                symbol = "§f\uEfd8";
                color = "§x§4§c§4§c§4§c";
                weight = 0;
        }

        String name = color + player.getName();
        String statusSuffix = "";
        if (statusManger.hasPlayerStatus(player)) {
            statusSuffix = " §8[§7" + statusManger.getStatusDisplay(statusManger.getStatus(player)) + "§8]";
        }

        player.setPlayerListName(symbol + " " + name + statusSuffix);
        player.setDisplayName(symbol + " " + name + statusSuffix);

        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        String teamName = String.format("%03d_%s", 999 - weight, player.getName().substring(0, Math.min(10, player.getName().length())));

        for (Team t : scoreboard.getTeams()) {
            if (t.hasEntry(player.getName())) t.removeEntry(player.getName());
        }

        Team team = scoreboard.getTeam(teamName);
        if (team == null) team = scoreboard.registerNewTeam(teamName);

        team.addEntry(player.getName());
        player.setScoreboard(scoreboard);
    }


}
