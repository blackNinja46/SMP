package live.blackninja.smp.cmd;

import live.blackninja.smp.Core;
import live.blackninja.smp.builder.MessageBuilder;
import live.blackninja.smp.manger.StatusManger;
import org.apache.logging.log4j.util.MessageSupplier;
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

public record StatusCmd(Core core) implements CommandExecutor, TabCompleter {
    
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) return true;

        Player player = (Player) sender;
        StatusManger statusManger = core.getSmpManger().getStatusManger();

        if (args.length == 0) {
            player.sendMessage(MessageBuilder.buildOld(Core.PREFIX + "§7Benutze %b/status < add | remove | set | list >"));
            return true;
        }

        switch (args[0]) {
            case "add" -> {
                if (!player.hasPermission("ninjasmp.status.admin")) {
                    player.sendMessage(Core.NO_PERMS);
                    return true;
                }

                if (args.length != 3) {
                    player.sendMessage(MessageBuilder.buildOld(Core.PREFIX + "§7Benutze %b/status add [Name] [Display]"));
                    return true;
                }

                String name = args[1];
                String display = args[2];

                if (statusManger.existStatus(name)) {
                    player.sendMessage(MessageBuilder.buildOld(Core.PREFIX + "§7Dieser Status %yexistiert §7bereits!"));
                    return true;
                }

                String finalDisplay = display.replace("&", "§");
                statusManger.addStatus(name ,finalDisplay);
                player.sendMessage(MessageBuilder.buildOld(Core.PREFIX + "§7Du hast %gerfolgreich §7den Status %y" + name + " §7hinzugefügt"));
            }
            case "remove" -> {
                if (!player.hasPermission("ninjasmp.status.admin")) {
                    player.sendMessage(Core.NO_PERMS);
                    return true;
                }

                if (args.length != 2) {
                    player.sendMessage(MessageBuilder.buildOld(Core.PREFIX + "§7Benutze %b/status remove [Name] "));
                    return true;
                }

                String name = args[1];

                if (!statusManger.existStatus(name)) {
                    player.sendMessage(MessageBuilder.buildOld(Core.PREFIX + "§7Dieser Status %yexistiert §7nicht!"));
                    return true;
                }
                statusManger.removeStatus(name);
                player.sendMessage(MessageBuilder.buildOld(Core.PREFIX + "§7Du hast erfolgreich §7den Status %y" + name + " %renfernt"));
            }
            case "set" -> {
                if (args.length != 2) {
                    player.sendMessage(MessageBuilder.buildOld(Core.PREFIX + "§7Benutze %b/status set [Status]"));
                    return true;
                }

                if (args[1].equalsIgnoreCase("none")) {
                    statusManger.removeStatusPlayer(player);
                    player.sendMessage(MessageBuilder.buildOld(Core.PREFIX + "§7Dein Status wurde %rentfernt"));
                    return true;
                }

                String name = args[1];

                if (!statusManger.existStatus(name)) {
                    player.sendMessage(MessageBuilder.buildOld(Core.PREFIX + "§7Dieser Status %yexistiert §7nicht!"));
                    return true;
                }
                statusManger.setStatusPlayer(player, name);
                player.sendMessage(MessageBuilder.buildOld(Core.PREFIX + "§7Dein Status wurde zu %b" + name + " §7geändert"));
            }
            case "list" -> {
                Set<String> statusList = statusManger.getStatusList();
                if (statusList == null) {
                    player.sendMessage(MessageBuilder.buildOld(Core.PREFIX + "§7Es wurden noch %rkein §7Status erstellt. §7Verwende %b/status add §7um ein %yStatus §7zu %gerstellen§7!"));
                    return true;
                }
                statusList.remove("Players");
                player.sendMessage(MessageBuilder.buildOld("§7Alle erstellten %bStatus §7(%b" + statusList.size() + "§7)§8:"));
                for (String status : statusList) {
                    player.sendMessage(MessageBuilder.buildOld(" §8%. %b" + status + " §8| §7[" + statusManger.getStatusDisplay(status) + "§7]"));
                }
            }
        }

        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        List<String> tc = new ArrayList<>();
        StatusManger statusManger = core.getSmpManger().getStatusManger();
        if (args.length == 1) {
            tc.add("set");
            tc.add("list");
            if (!sender.hasPermission("ninjasmp.status.admin")) {
                return null;
            }
            tc.add("add");
            tc.add("remove");
        }else if (args.length == 2) {
            tc.add("none");
            Set<String> statusList = statusManger.getStatusList();
            if (statusList.isEmpty()) {
                return null;
            }
            statusList.remove("Players");
            tc.addAll(statusList);
        }

        return tc;
    }
}
