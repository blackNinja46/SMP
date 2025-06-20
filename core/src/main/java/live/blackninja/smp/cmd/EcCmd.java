package live.blackninja.smp.cmd;

import live.blackninja.smp.Core;
import live.blackninja.smp.builder.MessageBuilder;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public record EcCmd(Core core) implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) return true;

        Player player = (Player) sender;

        if (!core.getSmpManger().getDelayedOpeningManger().isEndOpened()) {
            player.sendMessage(MessageBuilder.buildOld(Core.PREFIX + "§7Das §dEnd §7wurde noch %rnicht §7geöffnet!"));
            return false;
        }

        Player target = Bukkit.getPlayer(args[0]);
        player.openInventory(target.getEnderChest());

        return false;
    }
}
