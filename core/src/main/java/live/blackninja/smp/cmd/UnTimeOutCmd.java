package live.blackninja.smp.cmd;

import live.blackninja.smp.Core;
import live.blackninja.smp.builder.MessageBuilder;
import live.blackninja.smp.manger.TimeOutManger;
import live.blackninja.smp.util.uuid.UUIDFetcher;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public record UnTimeOutCmd(Core core) implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) return false;

        Player player = (Player) sender;
        TimeOutManger timeOutManger = core.getSmpManger().getTimeOutManger();

        if (!player.hasPermission("ninjasmp.cmd.timeout")) {
            player.sendMessage(Core.NO_PERMS);
            return false;
        }

        if (args.length == 0) {
            player.sendMessage(MessageBuilder.buildOld(TimeOutManger.PREFIX + "§7Benutze %b/untimeout [Spieler]"));
            return false;
        }

        String targetName = args[0];
        UUID targetUUID = UUIDFetcher.getUUID(targetName);
        timeOutManger.unTimeOut(targetUUID);

        if (timeOutManger.isPlayerExist(player.getUniqueId())) {
            player.sendMessage(MessageBuilder.buildOld(Core.PREFIX + "§7Der Spieler %b" + targetName + " §7ist nicht auf der Timeout-Liste!"));
            return false;
        }

        player.sendMessage(MessageBuilder.buildOld(Core.PREFIX + "§7Der Spieler %b" + targetName + " §7wurde §centbannt§7!"));
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        List<String> tc = new ArrayList<>();

        TimeOutManger timeOutManger = core.getSmpManger().getTimeOutManger();

        if (!(sender instanceof Player)) return null;

        if (!sender.hasPermission("ninjasmp.cmd.timeout")) {
            return null;
        }

        if (args.length == 1) {
            Set<String> timeOutedPlayer = timeOutManger.getTimeOutedPlayer();
            if (timeOutedPlayer.isEmpty()) {
            }else for (String s : timeOutedPlayer) {
                if (timeOutedPlayer.contains(s)) {
                    continue;
                }
                UUID uuid = UUIDFetcher.getUUID(s);
                tc.add(timeOutManger.getUsername(uuid));
            }
            return null;
        }
        return tc;
    }
}
