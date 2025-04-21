package live.blackninja.smp.cmd;

import live.blackninja.smp.Core;
import live.blackninja.smp.builder.MessageBuilder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public record HubschrauberCmd(Core core) implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) return true;

        Player player = (Player) sender;

        player.sendMessage(MessageBuilder.buildOld(Core.PREFIX + "§x§E§2§D§E§5§F§lH§x§E§2§D§5§5§F§lU§x§E§2§C§C§5§F§lB§x§E§2§C§3§5§F§lS§x§E§2§B§A§5§F§lC§x§E§2§B§1§5§F§lH§x§E§2§A§8§5§F§lR§x§E§2§9§F§5§F§lA§x§E§2§9§5§5§F§lU§x§E§2§8§C§5§F§lB§x§E§2§8§3§5§F§lE§x§E§2§7§A§5§F§lR§x§E§2§7§1§5§F§l!§x§E§2§6§8§5§F§l!§x§E§2§5§F§5§F§l! §7(Hat sich NiRei2021 gewünscht)"));

        return false;
    }
}
