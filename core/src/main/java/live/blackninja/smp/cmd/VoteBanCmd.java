package live.blackninja.smp.cmd;

import live.blackninja.smp.Core;
import live.blackninja.smp.builder.MessageBuilder;
import live.blackninja.smp.manger.TimeOutManger;
import live.blackninja.smp.manger.VoteBanManger;
import live.blackninja.smp.util.scoreboard.FastBoardBase;
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

public record VoteBanCmd(Core core) implements CommandExecutor, TabCompleter {
    
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) return true;

        Player player = (Player) sender;
        VoteBanManger voteBanManger = core.getSmpManger().getVoteBanManger();

        if (args.length == 0) {
            if (player.hasPermission("ninjasmp.voteban.admin")) {
                player.sendMessage(MessageBuilder.buildOld(VoteBanManger.VOTE_BAN_PREFIX + "§7Benutze %b/voteban < yes | no | create >"));
                return true;
            }
            player.sendMessage(MessageBuilder.buildOld(VoteBanManger.VOTE_BAN_PREFIX + "§7Benutze %b/voteban < yes | no >"));
            return true;
        }

        switch (args[0]) {
            case "create" -> {
                if (!player.hasPermission("ninjasmp.voteban.admin")) {
                    player.sendMessage(MessageBuilder.buildOld(Core.NO_PERMS));
                    return true;
                }
                if (args.length != 6) {
                    player.sendMessage(MessageBuilder.buildOld(VoteBanManger.VOTE_BAN_PREFIX + "§7Benutze %b/voteban create [Spieler] [Dauer der Abstimmung in Minuten] [Grund] [Dauer] [Seconds | Minutes | Hours | Days]"));
                    player.sendMessage(MessageBuilder.buildOld(VoteBanManger.VOTE_BAN_PREFIX + "%yTipp: §7Benutze %b_ §7für %bLeerzeichen§7."));
                    return true;
                }

                if (voteBanManger.isVoteRunning()) {
                    player.sendMessage(MessageBuilder.buildOld(VoteBanManger.VOTE_BAN_PREFIX + "§7Es %rläuft §7bereits eine %bAbstimmung§7!"));
                    return true;
                }

                Player target = Bukkit.getPlayer(args[1]);
                int time = Integer.parseInt(args[2]);
                String reason = args[3].replace("_", " ");
                long duration = Long.parseLong(args[4]);

                long currentTime = System.currentTimeMillis();
                long finalDuration = switch (args[5].toLowerCase()) {
                    case "s", "seconds" -> currentTime + (duration * 1000);
                    case "m", "minutes" -> currentTime + (duration * 1000 * 60);
                    case "h", "hours" -> currentTime + (duration * 1000 * 60 * 60);
                    case "d", "days" -> currentTime + (duration * 1000 * 60 * 60 * 24);
                    default -> 0;
                };

                if (target == null) {
                    player.sendMessage(MessageBuilder.buildOld(VoteBanManger.VOTE_BAN_PREFIX + "§7Dieser Spieler %rexistiert §7nicht!"));
                    return true;
                }

                voteBanManger.setTime(time * 60);
                voteBanManger.setReason(reason);
                voteBanManger.setDuration(finalDuration);
                voteBanManger.startVote(target.getUniqueId());
                voteBanManger.sendVoteMessage(player);
            }
            case "cancel" -> {
                if (!player.hasPermission("ninjasmp.voteban.admin")) {
                    player.sendMessage(MessageBuilder.buildOld(Core.NO_PERMS));
                    return true;
                }

                if (!voteBanManger.isVoteRunning()) {
                    player.sendMessage(MessageBuilder.buildOld(VoteBanManger.VOTE_BAN_PREFIX + "§7Aktuell läuft %rkeine §7Abstimmung!"));
                    return true;
                }

                voteBanManger.cancelVote();
            }
            case "yes" -> {
                if (!voteBanManger.isVoteRunning()) {
                    player.sendMessage(MessageBuilder.buildOld(VoteBanManger.VOTE_BAN_PREFIX + "§7Aktuell läuft %rkeine §7Abstimmung!"));
                    return true;
                }

                if (voteBanManger.getHavePlayerVoted().contains(player.getName())) {
                    player.sendMessage(MessageBuilder.buildOld(VoteBanManger.VOTE_BAN_PREFIX + "§7Du kannst nur %yeinmal §7abstimmen!"));
                    return true;
                }

                if (voteBanManger.getCurrentPlayer() == player.getUniqueId()) {
                    player.sendMessage(MessageBuilder.buildOld(VoteBanManger.VOTE_BAN_PREFIX + "§7Du darfst %rnicht §7selber an der %bAbstimmung §7teilnehmen!"));
                    return true;
                }

                voteBanManger.voteForYes(player);
                player.sendMessage(MessageBuilder.buildOld(VoteBanManger.VOTE_BAN_PREFIX + "§7Du hast nun für %gJA §7abgestimmt"));
            }
            case "no" -> {
                if (!voteBanManger.isVoteRunning()) {
                    player.sendMessage(MessageBuilder.buildOld(VoteBanManger.VOTE_BAN_PREFIX + "§7Aktuell läuft %rkeine §7Abstimmung!"));
                    return true;
                }

                if (voteBanManger.getHavePlayerVoted().contains(player.getName())) {
                    player.sendMessage(MessageBuilder.buildOld(VoteBanManger.VOTE_BAN_PREFIX + "§7Du kannst nur %yeinmal §7abstimmen!"));
                    return true;
                }

                if (voteBanManger.getCurrentPlayer() == player.getUniqueId()) {
                    player.sendMessage(MessageBuilder.buildOld(VoteBanManger.VOTE_BAN_PREFIX + "§7Du darfst %rnicht §7selber an der %bAbstimmung §7teilnehmen!"));
                    return true;
                }

                voteBanManger.voteForNo(player);
                player.sendMessage(MessageBuilder.buildOld(VoteBanManger.VOTE_BAN_PREFIX + "§7Du hast nun für %rNEIN §7abgestimmt"));
            }
        }

        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        List<String> tc = new ArrayList<>();

        if (args.length == 1) {
            tc.add("yes");
            tc.add("no");
            if (sender.hasPermission("ninjasmp.voteban.admin")) {
                tc.add("create");
                tc.add("cancel");
            }
        }else if (args.length == 2) {
            if (!sender.hasPermission("ninjasmp.voteban.admin")) {
                return null;
            }
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if (onlinePlayer.hasPermission("ninjasmp.team")) {
                    return null;
                }
                tc.add(onlinePlayer.getName());
            }
        }else if (args.length == 3) {
            if (!sender.hasPermission("ninjasmp.voteban.admin")) {
                return null;
            }
            tc.add("1");
            tc.add("2");
            tc.add("5");
            tc.add("10");
        }else if (args.length == 4) {
            if (!sender.hasPermission("ninjasmp.voteban.admin")) {
                return null;
            }
            tc.add("Cheating");
            tc.add("Hacking");
            tc.add("XRay");
            tc.add("Beleidigung");
            tc.add("Skin/Username");
            tc.add("Griefing");
        } else if (args.length == 6) {
            if (!sender.hasPermission("ninjasmp.voteban.admin")) {
                return null;
            }
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
