package live.blackninja.smp.cmd.staff;

import live.blackninja.smp.Core;
import live.blackninja.smp.builder.MessageBuilder;
import live.blackninja.smp.config.StaffTypes;
import live.blackninja.smp.manger.StaffManger;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public record StaffCmd(Core core) implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
        if ((!(sender instanceof Player))) {
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("ninjasmp.staff.cmd.staffmode")) {
            player.sendMessage(Core.NO_PERMS);
            return true;
        }

        if (args.length == 0) {
            core.getStaffManager().toggleStaffMode(player);
            return true;
        }

        Player target = core.getServer().getPlayer(args[0]);
        if (target == null) {
            player.sendMessage(StaffManger.STAFF_PREFIX + "§cDer Spieler ist nicht online.");
            return true;
        }
        core.getStaffManager().toggleStaffMode(target);
        if (core.getStaffManager().getConfig().containsList(StaffTypes.STAFF_MODE, target.getUniqueId())) {
            player.sendMessage(MessageBuilder.buildOld(StaffManger.STAFF_PREFIX + "§7Der Spieler §d" + target.getName() + " §7befindet sich nun nicht mehr §7im %bStaff-Modus§7."));
            return true;
        }
        player.sendMessage(MessageBuilder.buildOld(StaffManger.STAFF_PREFIX + "§7Der Spieler §d" + target.getName() + " §7befindet sich nun §7im %bStaff-Modus§7."));


        return false;
    }
}
