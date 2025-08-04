package live.blackninja.smp.cmd.staff;

import live.blackninja.smp.Core;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public record StaffChatCmd(Core core) implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
        if ((!(sender instanceof Player))) {
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("ninjasmp.staff.cmd.chat")) {
            player.sendMessage(Core.NO_PERMS);
            return true;
        }

        core.getStaffManager().toggleStaffChat(player);

        return false;
    }
}
