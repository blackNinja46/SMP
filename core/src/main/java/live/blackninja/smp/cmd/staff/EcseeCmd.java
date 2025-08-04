package live.blackninja.smp.cmd.staff;

import live.blackninja.smp.Core;
import live.blackninja.smp.builder.MessageBuilder;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public record EcseeCmd(Core core) implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) return true;

        Player player = (Player) sender;

        if (!player.hasPermission("ninjasmp.cmd.ecsee")) {
            player.sendMessage(Core.NO_PERMS);
            return true;
        }

        if (args.length == 0) {
            player.sendMessage(MessageBuilder.buildOld(Core.PREFIX + "§7Benutze %b/ecsee [Spieler]"));
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        player.openInventory(target.getEnderChest());

        return false;
    }
}
