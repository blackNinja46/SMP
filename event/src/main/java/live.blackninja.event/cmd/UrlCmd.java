package live.blackninja.event.cmd;

import live.blackninja.smp.Core;
import live.blackninja.smp.builder.MessageBuilder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public record UrlCmd(Core core) implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (!(sender instanceof Player player)) {
            return true;
        }

        if (!player.hasPermission("ninjasmp.event.cmd.urls")) {
            player.sendMessage(Core.NO_PERMS);
            return true;
        }

        player.sendMessage(MessageBuilder.build("<white>Dashboard:</white> <click:open_url:'http://176.9.122.20:7867/'><color:#00aeff>http://176.9.122.20:7867/</color></click>"));
        player.sendMessage(MessageBuilder.build("<white>BlueMaps:</white> <click:open_url:'http://176.9.122.20:8100/'><color:#00aeff>http://176.9.122.20:8100/</color></click>"));
        return false;
    }
}
