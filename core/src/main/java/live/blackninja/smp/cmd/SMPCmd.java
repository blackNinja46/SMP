package live.blackninja.smp.cmd;

import live.blackninja.smp.Core;
import live.blackninja.smp.builder.MessageBuilder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public record SMPCmd(Core core) implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) return true;

        Player player = (Player) sender;

        if (!player.hasPermission("ninjasmp.cmd.smp")) {
            player.sendMessage(Core.NO_PERMS);
            return true;
        }

        if (args.length == 0) {
            player.sendMessage(MessageBuilder.buildOld(Core.PREFIX + "§7Benutze %b/smp < spawn >"));
            return true;
        }

        switch (args[0]) {
            case "spawn" -> {
                core.getSmpManger().getConfig().setLocation(player.getLocation(), "SpawnLocation");
                core.getSmpManger().getConfig().save();
                player.sendMessage(MessageBuilder.buildOld(Core.PREFIX + "§7Du %gerfolgreich §7den %ySpawn §7neu gesetzt"));
                break;
            }
            case "resetElytra" -> {
                if (args.length != 2) {
                    player.sendMessage(MessageBuilder.buildOld(Core.PREFIX + "§7Benutze %b/smp resetElytra [Spieler]"));
                    return true;
                }

                Player target = core.getServer().getPlayer(args[1]);

                if (target == null) {
                    player.sendMessage(MessageBuilder.buildOld(Core.PREFIX + "§7Der Spieler %y" + args[1] + " §7ist nicht online!"));
                    return true;
                }

                core.getSmpManger().getElytraManger().clear(target.getUniqueId());
                player.sendMessage(MessageBuilder.buildOld(Core.PREFIX + "§7Du hast die Elytra-Condfig von %b" + target.getName() + " §7zurückgesetzt"));

            }
        }

        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        List<String> tc = new ArrayList<>();

        Player player = (Player) sender;

        if (args.length == 1) {
            if (!player.hasPermission("ninjasmp.cmd.smp")) {
                return null;
            }
            tc.add("spawn");
            tc.add("resetElytra");
        }

        return tc;
    }
}
