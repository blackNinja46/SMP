package live.blackninja.smp.cmd.staff;

import live.blackninja.smp.Core;
import live.blackninja.smp.builder.MessageBuilder;
import live.blackninja.smp.manger.TimeOutManger;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public record TimeOutCmd(Core core) implements CommandExecutor, TabCompleter {

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
            player.sendMessage(MessageBuilder.buildOld(TimeOutManger.PREFIX + "§7Benutze %b/timeout [Spieler] [Grund] [Dauer] [Seconds | Minutes | Hours | Days]"));
            player.sendMessage(MessageBuilder.buildOld(TimeOutManger.PREFIX + "%yTipp: §7Benutze %b_ §7für %bLeerzeichen§7."));
            return true;
        }

        if (args.length != 4) {
            player.sendMessage(MessageBuilder.buildOld(TimeOutManger.PREFIX + "§7Benutze %b/timeout [Spieler] [Grund] [Dauer] [Seconds | Minutes | Hours | Days]"));
            player.sendMessage(MessageBuilder.buildOld(TimeOutManger.PREFIX + "%yTipp: §7Benutze %b_ §7für %bLeerzeichen§7."));
            return false;
        }

        Player target = Bukkit.getPlayer(args[0]);

        if (target == null) {
            player.sendMessage(MessageBuilder.buildOld(TimeOutManger.PREFIX + "§7Dieser Spieler %rexistiert §7nicht!"));
            return true;
        }

        String reason = args[1].replace("_", " ");
        long duration = Long.parseLong(args[2]);

        long currentTime = System.currentTimeMillis();
        long finalDuration = switch (args[3].toLowerCase()) {
            case "s", "seconds" -> currentTime + (duration * 1000);
            case "m", "minutes" -> currentTime + (duration * 1000 * 60);
            case "h", "hours" -> currentTime + (duration * 1000 * 60 * 60);
            case "d", "days" -> currentTime + (duration * 1000 * 60 * 60 * 24);
            default -> 0;
        };

        timeOutManger.timeOut(target.getUniqueId(), reason, finalDuration);
        target.kick(timeOutManger.getTimeOutMessage(reason, timeOutManger.getFormatedDuration(finalDuration)));
        player.sendMessage(MessageBuilder.buildOld(TimeOutManger.PREFIX + "§7Der Spieler %b" + target.getName() + " §7wurde bis zum " + timeOutManger.getFormatedDuration(finalDuration) + " §7gebannt!"));
        player.sendMessage(MessageBuilder.buildOld(TimeOutManger.PREFIX + "§7Grund: %y" + reason));
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        List<String> tc = new ArrayList<>();

        if (!(sender instanceof Player)) return null;

        if (!sender.hasPermission("ninjasmp.cmd.timeout")) {
            return null;
        }

        if (args.length == 1) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                tc.add(player.getName());
            }
        }else if (args.length == 2) {
            tc.add("Cheating");
            tc.add("Hacking");
            tc.add("XRay");
            tc.add("Beleidigung");
            tc.add("Skin/Username");
            tc.add("Griefing");
        } else if (args.length == 4) {
            tc.add("seconds");
            tc.add("s");
            tc.add("minutes");
            tc.add("m");
            tc.add("hours");
            tc.add("h");
            tc.add("days");
            tc.add("d");
        }


        return tc;
    }
}
