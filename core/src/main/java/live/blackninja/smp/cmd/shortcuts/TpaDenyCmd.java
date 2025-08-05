package live.blackninja.smp.cmd.shortcuts;

import live.blackninja.smp.Core;
import live.blackninja.smp.builder.MessageBuilder;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public record TpaDenyCmd(Core core) implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
        if (!(sender instanceof Player)) return true;

        Player player = (Player) sender;

        if (args.length == 0) {
            player.sendMessage(MessageBuilder.buildOld(Core.PREFIX + "§7Bitte gib auch den Spielernamen an: %b/tpadeny <Spieler>"));
            return true;
        }

        Player target = Bukkit.getPlayerExact(args[0]);
        player.performCommand("tpa accept " + target.getName());


        return false;
    }
}
