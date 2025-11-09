package live.blackninja.smp.cmd.staff;

import live.blackninja.smp.Core;
import live.blackninja.smp.builder.MessageBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Instrument;
import org.bukkit.Note;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public record RestartCmd(Core core) implements CommandExecutor, TabCompleter {

    private static int taskID;
    private static int currentDelay;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) return true;

        Player player = (Player) sender;

        if (!player.hasPermission("ninjasmp.cmd.restart")) {
            player.sendMessage(MessageBuilder.buildOld(Core.NO_PERMS));
            return true;
        }

        if (args.length == 0) {
            restartServer(10);
            player.sendMessage(MessageBuilder.buildOld(Core.PREFIX + "§7Der %bServer §7startet nun in %b10 Sekunden §7neu..."));
            return true;
        }

        switch (args[0]) {
            case "after" -> {
                if (args.length != 2) {
                    player.sendMessage(MessageBuilder.buildOld(Core.PREFIX + "§7Benutze %b/restart after [Delay]"));
                    return true;
                }
                int delay = Integer.parseInt(args[1]);

                if (delay < 5) {
                    player.sendMessage(MessageBuilder.buildOld(Core.PREFIX + "§7Der %yWert d§7arf %cnicht §7kleiner als %y5 §7sein!"));
                    return true;
                }

                player.sendMessage(MessageBuilder.buildOld(Core.PREFIX + "§7Der %bServer §7startet nun in %b" + delay + " Sekunden §7neu..."));
                restartServer(delay);
            }
            case "instant" -> {
                player.sendMessage(MessageBuilder.buildOld(Core.PREFIX + "§7Der %bServer $7startet nun %rdirekt§7!"));
                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    onlinePlayer.kickPlayer(MessageBuilder.buildOld("§8%> §7Der §eServer i§7st in §ckürze §7wieder erreichbar!"));
                }
                Bukkit.getServer().shutdown();
            }
        }


        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        List<String> tc = new ArrayList<>();

        Player player = (Player) sender;

        if (args.length == 1) {
            if (!player.hasPermission("ninjasmp.cmd.restart")) {
                return null;
            }
            tc.add("after");
            tc.add("instant");
        }else if (args.length == 2) {
            tc.add("5");
            tc.add("10");
            tc.add("20");
            tc.add("30");
        }

        return tc;
    }

    public void restartServer(int delay) {
        currentDelay = delay;
        taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(core, new Runnable() {
            @Override
            public void run() {

                switch (currentDelay) {
                    case 120, 90, 60, 30, 20, 10, 5, 4, 3, 2, 1 -> {
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            player.sendTitle(MessageBuilder.buildOld("%rAchtung"), MessageBuilder.buildOld("%o⚠ §7Der Server startet in %r" + currentDelay + " §7Sekunden neu %o⚠"));
                            player.playNote(player.getLocation(), Instrument.BELL, Note.sharp(2, Note.Tone.F));
                        }
                    }
                    case 0 -> {
                        Bukkit.getScheduler().cancelTask(taskID);
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            player.kickPlayer(MessageBuilder.buildOld("§8%> §7Der §9Server §7ist in §ckürze §7wieder erreichbar!"));
                        }
                        Bukkit.getServer().shutdown();
                    }
                }

                currentDelay--;
            }
        }, 0, 20);


    }
}
