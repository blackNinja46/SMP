package live.blackninja.smp.cmd;

import live.blackninja.smp.Core;
import live.blackninja.smp.gui.LeaderboardGUI;
import live.blackninja.smp.gui.StatsGUI;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public record LeaderboardCmd(Core core) implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
        if (!(sender instanceof Player)) return true;

        Player player = (Player) sender;

        LeaderboardGUI.open(player, core);


        return false;
    }
}
