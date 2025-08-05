package live.blackninja.smp.cmd;

import live.blackninja.smp.Core;
import live.blackninja.smp.builder.MessageBuilder;
import live.blackninja.smp.gui.ConfirmDeleteHomeGUI;
import live.blackninja.smp.gui.HomesGUI;
import live.blackninja.smp.manger.HomeManger;
import org.bukkit.Location;
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

public record HomeCmd(Core core) implements CommandExecutor, TabCompleter {
    
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) return true;

        Player player = (Player) sender;
        HomeManger homeManger = core.getSmpManger().getHomeManger();

        if (args.length == 0) {
            HomesGUI.open(player, core);
            return true;
        }

        if (args[0].equalsIgnoreCase("set")) {
            if (args.length != 2) {
                player.sendMessage(MessageBuilder.buildOld(Core.PREFIX + "§7Benutze %b/home set [Home]"));
                return true;
            }

            if (homeManger.getCountForPlayer(player.getName()) <= 0) {
                player.sendMessage(MessageBuilder.buildOld(Core.PREFIX + "§7Du hast die %ymaximale §7Anzahl an %bHomes §7erreicht!"));
                return true;
            }

            String name = args[1];

            if (homeManger.existsHome(player.getName(), name)) {
                player.sendMessage(MessageBuilder.buildOld(Core.PREFIX + "§7Dieses Home %rexistiert §7schon!"));
                return true;
            }

            homeManger.setHome(player.getName(), name, player.getLocation());
            player.sendMessage(MessageBuilder.buildOld(Core.PREFIX + "§7Das Home %b" + name.replace("&", "§") + " §7wurde %gerfolgreich §7erstellt"));
            return true;
        }

        if (args[0].equalsIgnoreCase("delete")) {
            if (args.length != 2) {
                player.sendMessage(MessageBuilder.buildOld(Core.PREFIX + "§7Benutze %b/home delete [Home]"));
                return true;
            }

            String name = args[1];

            if (!homeManger.existsHome(player.getName(), name)) {
                player.sendMessage(MessageBuilder.buildOld(Core.PREFIX + "§7Dieses Home konnte %rnicht §7gefunden werden!"));
                return true;
            }

            ConfirmDeleteHomeGUI.open(player, name, core);
            return true;
        }
        if (args[0].equalsIgnoreCase("tp")) {
            if (args.length != 2) {
                player.sendMessage(MessageBuilder.buildOld(Core.PREFIX + "§7Benutze §b/home tp [Home]"));
                return true;
            }

            String name = args[1];
            Location location = homeManger.getHome(player.getName(), name);

            if (!homeManger.existsHome(player.getName(), name)) {
                player.sendMessage(MessageBuilder.buildOld(Core.PREFIX + "§7Dieses Home konnte %rnicht §7gefunden werden!"));
                return true;
            }

            core.getSmpManger().teleport(player, location);
            player.sendMessage(MessageBuilder.buildOld(Core.PREFIX + "§7§oDu wirst zu deinem Home teleportiert..."));
            return true;
        }
        if (args[0].equalsIgnoreCase("rename")) {
            if (args.length != 3) {
                player.sendMessage(MessageBuilder.buildOld(Core.PREFIX + "§7Benutze %b/home rename [Home] [Neuer Name]"));
                player.sendMessage(MessageBuilder.buildOld("§7Du kannst auch Farben mit %b& §7verwenden!"));
                return true;
            }
            String oldName = args[1];
            String newName = args[2];
            if (!homeManger.existsHome(player.getName(), oldName)) {
                player.sendMessage(MessageBuilder.buildOld(Core.PREFIX + "§7Dieses Home konnte %rnicht §7gefunden werden!"));
                return true;
            }
            if (homeManger.existsHome(player.getName(), newName)) {
                player.sendMessage(MessageBuilder.buildOld(Core.PREFIX + "§7Dieses Home %rexistiert §7schon!"));
                return true;
            }
            homeManger.renameHome(player.getName(), oldName, newName);
            player.sendMessage(MessageBuilder.buildOld(Core.PREFIX + "§7Das Home %b" + oldName.replace("&", "§") + " §7wurde in %b" + newName.replace("&", "§") + " §7umbenannt"));
            return true;
        }
        if (args[0].equalsIgnoreCase("list")) {

            Set<String> homes = homeManger.getHomes(player.getName());
            player.sendMessage(MessageBuilder.buildOld("§7Liste aller %bHomes §7(%b" + homes.size() + "§7)§8: "));
            for (String home : homes) {
                player.sendMessage(MessageBuilder.buildOld("§8- %b" + home.replace("&", "§")));
            }
            return true;
        }
        if (args[0].equalsIgnoreCase("help")) {
            player.sendMessage(MessageBuilder.buildOld(Core.PREFIX + "§7Benutze %b/home < set | delete | list | tp > [Home]"));
            return true;
        }



        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            return Collections.emptyList();
        }
        Player player = (Player) sender;
        HomeManger homeManger = core.getSmpManger().getHomeManger();
        List<String> tc = new ArrayList<>();

        if (args.length == 1) {
            List<String> subs = List.of("set", "delete", "tp", "rename", "list", "help");
            tc.addAll(subs);
        } else if (args.length == 2) {
            Set<String> homes = homeManger.getHomes(player.getName());
            if (homes == null) {
                homes = Collections.emptySet();
            }
            tc.addAll(homes);
        }

        return tc;
    }

}
