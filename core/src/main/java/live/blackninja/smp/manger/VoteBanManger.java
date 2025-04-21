package live.blackninja.smp.manger;

import live.blackninja.smp.Core;
import live.blackninja.smp.builder.MessageBuilder;
import live.blackninja.smp.config.TimeOutConfig;
import live.blackninja.smp.util.IntegerFormat;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.apache.logging.log4j.util.MessageSupplier;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Boss;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

public class VoteBanManger {

    private UUID currentPlayer;
    private ArrayList<String> havePlayerVoted = new ArrayList<>();
    private Core core;
    private boolean isVoteRunning;
    private int votesYes;
    private int votesNo;
    private int taskID;
    private int time;
    private long duration;
    private String reason;

    private BossBar bossBar;
    public static final String VOTE_BAN_PREFIX = MessageBuilder.buildOld("§8| §cVB §8%> §r");

    public VoteBanManger(Core core) {
        this.core = core;

        bossBar = BossBar.bossBar(Component.text("Error :/"), 1, BossBar.Color.WHITE, BossBar.Overlay.PROGRESS);
    }

    public void startVote(UUID targetUUID) {
        currentPlayer = targetUUID;
        isVoteRunning = true;



        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            onlinePlayer.showBossBar(bossBar);
            onlinePlayer.playSound(onlinePlayer, Sound.BLOCK_NOTE_BLOCK_BELL, 5, 1);
        }

        taskID = Bukkit.getScheduler().runTaskTimer(core, new Runnable() {
            @Override
            public void run() {

               switch (time) {
                   case 60 -> {
                        Bukkit.getOnlinePlayers().forEach(player -> player.sendMessage(MessageBuilder.buildOld(VOTE_BAN_PREFIX + "§7Die laufende %bAbstimmung §7endet in %b60 Sekunden§7!")));
                   }
                   case 10 -> {
                       Bukkit.getOnlinePlayers().forEach(player -> player.sendMessage(MessageBuilder.buildOld(VOTE_BAN_PREFIX + "§7Die laufende %bAbstimmung §7endet in %b10 Sekunden§7!")));
                   }
                   case 5 -> {
                       Bukkit.getOnlinePlayers().forEach(player -> player.sendMessage(MessageBuilder.buildOld(VOTE_BAN_PREFIX + "§7Die laufende %bAbstimmung §7endet in %b5 Sekunden§7!")));
                   }
                   case 4 -> {
                       Bukkit.getOnlinePlayers().forEach(player -> player.sendMessage(MessageBuilder.buildOld(VOTE_BAN_PREFIX + "§7Die laufende %bAbstimmung §7endet in %b4 Sekunden§7!")));
                   }
                   case 3 -> {
                       Bukkit.getOnlinePlayers().forEach(player -> player.sendMessage(MessageBuilder.buildOld(VOTE_BAN_PREFIX + "§7Die laufende %bAbstimmung §7endet in %b3 Sekunden§7!")));
                   }
                   case 2 -> {
                       Bukkit.getOnlinePlayers().forEach(player -> player.sendMessage(MessageBuilder.buildOld(VOTE_BAN_PREFIX + "§7Die laufende %bAbstimmung §7endet in %b2 Sekunden§7!")));
                   }
                   case 1 -> {
                       Bukkit.getOnlinePlayers().forEach(player -> player.sendMessage(MessageBuilder.buildOld(VOTE_BAN_PREFIX + "§7Die laufende %bAbstimmung §7endet in %b1 Sekunden§7!")));
                   }
                   case 0 -> {
                       Bukkit.getOnlinePlayers().forEach(player -> player.sendMessage(MessageBuilder.buildOld(VOTE_BAN_PREFIX + "§7Die laufende %bAbstimmung §7ist nun %bzuende§7!")));
                       endVote();
                   }
               }
               time--;
                Component bossbarName = MiniMessage.miniMessage().deserialize("<color:#b7cede>Laufende Abstimmung: <color:#00ddff>" + Bukkit.getPlayer(targetUUID).getName() + " <color:#00ddff>/vote yes/no <dark_gray>|</dark_gray> <b><color:#00ddff>" + IntegerFormat.getFormattedTime(time) + "</b><white>");
                bossBar.name(bossbarName);
            }
        }, 0, 20).getTaskId();
    }

    public void voteForYes(Player player) {
        votesYes++;
        havePlayerVoted.add(player.getName());
    }

    public void voteForNo(Player player) {
        votesNo++;
        havePlayerVoted.add(player.getName());
    }

    public void endVote() {
        Player target = Bukkit.getPlayer(currentPlayer);
        TimeOutManger timeOutManger = core.getSmpManger().getTimeOutManger();

        Bukkit.getScheduler().cancelTask(taskID);

        havePlayerVoted.clear();

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            onlinePlayer.hideBossBar(bossBar);
            onlinePlayer.playSound(onlinePlayer, Sound.BLOCK_NOTE_BLOCK_BELL, 5, 1);
        }

        if (votesYes > votesNo) {
            timeOutManger.timeOut(target.getUniqueId(), reason, duration);
            target.kick(timeOutManger.getTimeOutMessage(reason, timeOutManger.getFormatedDuration(duration)));

            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                onlinePlayer.sendMessage(MessageBuilder.buildOld(VOTE_BAN_PREFIX + "%gJa §7hat gewonnen! (%b" + votesYes + "§7) §8| §7Der Spieler %b" + target.getName() + " §7wurde %rgebannt§7!"));
                if (onlinePlayer.hasPermission("ninjasmp.team")) {}
            }
        }else if (votesYes == votesNo) {
            target.sendMessage(MessageBuilder.buildOld(VOTE_BAN_PREFIX + "§7Das war nochmal knapp! §7Du wurdest %rnicht §7gebannt!"));
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if (onlinePlayer != target) {
                    onlinePlayer.sendMessage(MessageBuilder.buildOld(VOTE_BAN_PREFIX + "%yUnentschieden! (%b" + votesYes + " §8| %b" + votesNo +"§7) §8| §7Der Spieler %b" + target.getName() + " §7wurde %rnicht §7gebannt"));
                }
            }
        }else {
            target.sendMessage(MessageBuilder.buildOld(VOTE_BAN_PREFIX + "§7Du wurdest %rnicht §7gebannt"));
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if (onlinePlayer != target) {
                    onlinePlayer.sendMessage(MessageBuilder.buildOld(VOTE_BAN_PREFIX + "%rNein §7hat gewonnen! (%b" + votesNo + "§7) §8| §7Der Spieler %b" + target.getName() + " " +
                            "§7wurde %rnicht §7gebannt"));
                }
            }
        }

        isVoteRunning = false;
        currentPlayer = null;
        votesYes = 0;
        votesNo = 0;
    }

    public void cancelVote() {

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            onlinePlayer.hideBossBar(bossBar);
            onlinePlayer.sendMessage(MessageBuilder.buildOld(VOTE_BAN_PREFIX + "§7Die %bAbstimmung §7wurde %rabgebrochen§7!"));
            onlinePlayer.playSound(onlinePlayer, Sound.BLOCK_NOTE_BLOCK_BELL, 5, 1);
        }

        Bukkit.getScheduler().cancelTask(taskID);

        havePlayerVoted.clear();

        isVoteRunning = false;
        currentPlayer = null;
        votesYes = 0;
        votesNo = 0;
    }

    public void sendVoteMessage(Player sender) {
        Player target = Bukkit.getPlayer(currentPlayer);
        TimeOutManger timeOutManger = core.getSmpManger().getTimeOutManger();

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            onlinePlayer.sendMessage(" ");
            onlinePlayer.sendMessage(MessageBuilder.buildOld(VOTE_BAN_PREFIX + "§7Der Spieler %b" + sender.getDisplayName() + " §7hat eine %bAbstimmung §7gestartet"));
            onlinePlayer.sendMessage(MessageBuilder.buildOld(VOTE_BAN_PREFIX + "§7Stimme jetzt dafür ab, ob der Spieler %b" + target.getDisplayName() + " §7gebannt werden soll!"));
            onlinePlayer.sendMessage(MessageBuilder.buildOld(VOTE_BAN_PREFIX + "§7Grund: %b" + reason + " §8| §7Dauer: bis zum %b" + timeOutManger.getFormatedDuration(duration)));
            onlinePlayer.sendMessage(MessageBuilder.build("<dark_gray>» <gray>Abstimmungs möglichkeiten:</gray> " +
                    "<hover:show_text:'<gray>Bannen'><click:run_command:'/voteban yes'><color:#00ff00><gray>[</gray>JA<gray>]</gray></color></click></hover> " +
                    "<dark_gray>|</dark_gray> " +
                    "<hover:show_text:'<gray>Nicht Bannen'><click:run_command:'/voteban no'><color:#ff0000><gray>[</gray>NEIN<gray>]</gray></color></click></hover>"));
            onlinePlayer.sendMessage(" ");
        }
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getTime() {
        return time;
    }

    public boolean isVoteRunning() {
        return isVoteRunning;
    }

    public ArrayList<String> getHavePlayerVoted() {
        return havePlayerVoted;
    }

    public void setCurrentPlayer(UUID currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public UUID getCurrentPlayer() {
        return currentPlayer;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getDuration() {
        return duration;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }
}
