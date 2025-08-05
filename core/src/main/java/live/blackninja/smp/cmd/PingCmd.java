package live.blackninja.smp.cmd;

import live.blackninja.smp.Core;
import live.blackninja.smp.builder.MessageBuilder;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public record PingCmd(Core core) implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
        if (!(sender instanceof Player)) return true;

        Player player = (Player) sender;

        if (args.length == 0) {
            player.sendMessage(MessageBuilder.buildOld(Core.PREFIX + "§7Dein Ping: %b" + player.getPing() + "ms"));
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);

        if (target == null || !target.isOnline()) {
            player.sendMessage(Core.PREFIX + "§cDer Spieler ist nicht online.");
            return true;
        }

        player.sendMessage(MessageBuilder.buildOld(Core.PREFIX + "§7Ping von %b" + target.getName() + "§7: %b" + target.getPing() + "ms"));


        return false;
    }
}
