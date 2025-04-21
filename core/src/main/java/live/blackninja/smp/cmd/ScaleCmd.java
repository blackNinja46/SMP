package live.blackninja.smp.cmd;

import live.blackninja.smp.Core;
import live.blackninja.smp.builder.MessageBuilder;
import live.blackninja.smp.listener.PlayerInteractListener;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public record ScaleCmd(Core core) implements CommandExecutor, TabCompleter {
    
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) return true;

        Player player = (Player) sender;
        if (!player.hasPermission("ninjasmp.cmd.scale")) {
            player.sendMessage(Core.NO_PERMS);
            return true;
        }

        if (args.length == 0) {
            player.sendMessage(MessageBuilder.buildOld(Core.PREFIX + "§7Benutze %b/home < large | small >"));
            return true;
        }

        if (args[0].equalsIgnoreCase("small")) {
            player.getAttribute(Attribute.SCALE).setBaseValue(0.7);
            PlayerInteractListener.getScale().add(player.getName());
            player.sendMessage(MessageBuilder.buildOld(Core.PREFIX + "§7Du bist nun %bklein§7!"));
            return true;
        }

        if (args[0].equalsIgnoreCase("large")) {
            player.getAttribute(Attribute.SCALE).setBaseValue(1.3);
            PlayerInteractListener.getScale().add(player.getName());
            player.sendMessage(MessageBuilder.buildOld(Core.PREFIX + "§7Du bist nun %bgroß§7!"));
            return true;
        }

        if (args[0].equalsIgnoreCase("normal")) {
            player.getAttribute(Attribute.SCALE).setBaseValue(1);
            PlayerInteractListener.getScale().remove(player.getName());
            player.sendMessage(MessageBuilder.buildOld(Core.PREFIX + "§7Du hast nun wieder die %bnormale §7größe!"));
            return true;
        }
        player.sendMessage(MessageBuilder.buildOld(Core.PREFIX + "§7Benutze %b/home < large | small >"));

        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        List<String> tc = new ArrayList<>();

        Player player = (Player) sender;

        if (args.length == 1) {
            if (!player.hasPermission("ninjasmp.cmd.scale")) {
                return null;
            }
            tc.add("small");
            tc.add("large");
            tc.add("normal");
        }

        return tc;
    }
}
