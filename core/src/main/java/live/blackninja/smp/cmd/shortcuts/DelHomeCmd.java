package live.blackninja.smp.cmd.shortcuts;

import live.blackninja.smp.Core;
import live.blackninja.smp.builder.MessageBuilder;
import live.blackninja.smp.manger.HomeManger;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public record DelHomeCmd(Core core) implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
        if (!(sender instanceof Player)) return true;

        Player player = (Player) sender;

        if (args.length == 0) {
            player.sendMessage(MessageBuilder.buildOld(Core.PREFIX + "§7Bitte gib auch den Spielernamen an: %b/delhome <Name>"));
            return true;
        }

        String homeName = args[0];
        player.performCommand("home delete " + homeName);


        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
        if (!(sender instanceof Player)) {
            return Collections.emptyList();
        }
        Player player = (Player) sender;
        HomeManger homeManger = core.getSmpManger().getHomeManger();
        List<String> tc = new ArrayList<>();

        if (args.length == 1) {
            Set<String> homes = homeManger.getHomes(player.getName());
            if (homes == null) {
                homes = Collections.emptySet();
            }
            tc.addAll(homes);
        }

        return tc;
    }
}
