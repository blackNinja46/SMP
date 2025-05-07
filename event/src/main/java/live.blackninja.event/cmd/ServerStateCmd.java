package live.blackninja.event.cmd;

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

public record ServerStateCmd(Core core) implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (!(sender instanceof Player player)) {
            return true;
        }

        if (!player.hasPermission("ninjasmp.event.cmd.serverstate")) {
            player.sendMessage(Core.NO_PERMS);
            return true;
        }

        else if (args.length == 0) {
            player.sendMessage(MessageBuilder.buildOld("§8| §x§4§4§9§8§D§BS§x§3§C§9§2§E§0M§x§3§5§8§C§E§4P§x§2§D§8§6§E§9-§x§2§5§8§0§E§DE§x§1§D§7§A§F§2v§x§1§6§7§4§F§6e§x§0§E§6§E§F§Bn§x§0§6§6§8§F§Ft §8» §r%r%x §8| §7Benutze %b/switch-event-server-state {boolean}"));
            return true;
        }

        if (args[0].equalsIgnoreCase("true")) {
            player.sendMessage(MessageBuilder.buildOld("§8| §x§4§4§9§8§D§BS§x§3§C§9§2§E§0M§x§3§5§8§C§E§4P§x§2§D§8§6§E§9-§x§2§5§8§0§E§DE§x§1§D§7§A§F§2v§x§1§6§7§4§F§6e§x§0§E§6§E§F§Bn§x§0§6§6§8§F§Ft §8» §r%g%/ §8| §7Der §x§4§4§9§8§D§BEvent-Server §7ist jetzt %gaktiviert§7!"));
            this.switchState(true);
            return true;
        } else if (args[0].equalsIgnoreCase("false")) {
            player.sendMessage(MessageBuilder.buildOld("§8| §x§4§4§9§8§D§BS§x§3§C§9§2§E§0M§x§3§5§8§C§E§4P§x§2§D§8§6§E§9-§x§2§5§8§0§E§DE§x§1§D§7§A§F§2v§x§1§6§7§4§F§6e§x§0§E§6§E§F§Bn§x§0§6§6§8§F§Ft §8» §r%r%x §8| §7Der §x§4§4§9§8§D§BEvent-Server §7ist jetzt %rdeaktiviert§7!"));
            this.switchState(false);
            return true;
        }

        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        List<String> tc = new ArrayList();
        if (args.length == 1) {
            tc.add("false");
            tc.add("true");
        }

        return tc;
    }

    private void switchState(boolean state) {
        core.getSmpManger().getConfig().getConfig().set("ServerState", state);
        core.getSmpManger().getConfig().save();
    }
}
