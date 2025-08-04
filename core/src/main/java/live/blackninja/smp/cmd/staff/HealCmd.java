package live.blackninja.smp.cmd.staff;

import live.blackninja.smp.Core;
import live.blackninja.smp.builder.MessageBuilder;
import live.blackninja.smp.config.StaffTypes;
import live.blackninja.smp.manger.StaffManger;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public record HealCmd(Core core) implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
        if ((!(sender instanceof Player))) {
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("ninjasmp.staff.cmd.vanish")) {
            player.sendMessage(Core.NO_PERMS);
            return true;
        }

        if (args.length == 0) {
            player.sendMessage(MessageBuilder.buildOld(StaffManger.STAFF_PREFIX + "§7Du wurdest §ageheilt§7."));
            player.setHealth(player.getAttribute(Attribute.MAX_HEALTH).getValue());
            player.setFoodLevel(20);
            player.setSaturation(20);
            player.setFireTicks(0);
            return true;
        }

        Player target = core.getServer().getPlayer(args[0]);
        if (target == null) {
            player.sendMessage(StaffManger.STAFF_PREFIX + "§cDer Spieler ist nicht online.");
            return true;
        }
        target.setHealth(target.getAttribute(Attribute.MAX_HEALTH).getValue());
        target.setFoodLevel(20);
        target.setSaturation(20);
        target.setFireTicks(0);
        target.sendMessage(MessageBuilder.buildOld(StaffManger.STAFF_PREFIX + "§7Du wurdest §ageheilt§7."));
        player.sendMessage(MessageBuilder.buildOld(StaffManger.STAFF_PREFIX + "§7Du hast §b" + target.getName() + " §7geheilt."));


        return false;
    }
}
