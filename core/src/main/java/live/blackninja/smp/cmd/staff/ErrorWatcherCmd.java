package live.blackninja.smp.cmd.staff;

import live.blackninja.smp.Core;
import live.blackninja.smp.builder.MessageBuilder;
import live.blackninja.smp.util.ErrorWatcher;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public record ErrorWatcherCmd(Core core) implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Nur Spieler können diesen Befehl nutzen.");
            return true;
        }

        if (!sender.hasPermission("ninjasmp.debug")) {
            sender.sendMessage(MessageBuilder.buildOld(Core.NO_PERMS));
            return true;
        }

        if (ErrorWatcher.isDebugging(player)) {
            ErrorWatcher.disableDebug(player);
        } else {
            ErrorWatcher.enableDebug(player);
        }

        return true;
    }
}

