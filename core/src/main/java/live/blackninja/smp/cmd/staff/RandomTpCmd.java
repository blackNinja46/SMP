package live.blackninja.smp.cmd.staff;

import live.blackninja.smp.Core;
import live.blackninja.smp.builder.MessageBuilder;
import live.blackninja.smp.config.StaffTypes;
import live.blackninja.smp.manger.StaffManger;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public record RandomTpCmd(Core core) implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
        if ((!(sender instanceof Player))) {
            return true;
        }

        Player player = (Player) sender;
        Random random = new Random();
        List<Player> onlinePlayers = new ArrayList<>(Bukkit.getOnlinePlayers());

        if (!player.hasPermission("ninjasmp.staff.cmd.randomTp")) {
            player.sendMessage(Core.NO_PERMS);
            return true;
        }

        if (args.length == 0) {

            onlinePlayers.remove(player);

            if (onlinePlayers.isEmpty()) {
                player.sendMessage("§cEs sind keine anderen Spieler online.");
                return true;
            }

            Player TPtarget = onlinePlayers.get(random.nextInt(onlinePlayers.size()));

            player.teleport(TPtarget.getLocation());
            player.sendMessage(MessageBuilder.buildOld(StaffManger.STAFF_PREFIX + "§7Du wurdest zu §e" + TPtarget.getName() + " §7teleportiert."));
            return true;
        }

        Player target = core.getServer().getPlayer(args[0]);
        if (target == null) {
            player.sendMessage(StaffManger.STAFF_PREFIX + "§cDer Spieler ist nicht online.");
            return true;
        }
        onlinePlayers.remove(player);

        if (onlinePlayers.isEmpty()) {
            player.sendMessage("§cEs sind keine anderen Spieler online.");
            return true;
        }

        Player TPtarget = onlinePlayers.get(random.nextInt(onlinePlayers.size()));

        target.teleport(TPtarget.getLocation());
        target.sendMessage(MessageBuilder.buildOld(StaffManger.STAFF_PREFIX + "§7Du wurdest zu §e" + TPtarget.getName() + " §7teleportiert."));
        player.sendMessage(MessageBuilder.buildOld(StaffManger.STAFF_PREFIX + "§7Du hast §e" + target.getName() + " §7zu §e" + TPtarget.getName() + " §7teleportiert."));

        return false;
    }
}
