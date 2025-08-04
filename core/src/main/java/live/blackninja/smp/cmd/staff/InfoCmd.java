package live.blackninja.smp.cmd.staff;

import live.blackninja.smp.Core;
import live.blackninja.smp.builder.MessageBuilder;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public record InfoCmd(Core core) implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (!(sender instanceof Player)) {
            return false;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("ninjasmp.cmd.info")) {
            player.sendMessage(MessageBuilder.buildOld(Core.NO_PERMS));
            return false;
        }

        player.sendMessage(MessageBuilder.build(
                "<dark_gray>*----------------------------------------------------*</dark_gray>\n" +
                "\n" +
                "<color:#4498db>SMP Core System</color> " + Bukkit.getVersion() + "\n" +
                "\n" +
                "<gray>Core Status</gray> <dark_gray>→</dark_gray> " + getStatusText("SMP") + "\n" +
                "<gray>MC-Version</gray> <dark_gray>→</dark_gray> " + Bukkit.getMinecraftVersion() + "\n" +
                "\n" +
                "<color:#ffc800>Addons: </color>\n" +
                "<dark_gray>-</dark_gray> <gray>Event</gray> <dark_gray>→</dark_gray> " + getStatusText("SMPEvent") + "\n" +
                "<dark_gray>-</dark_gray> <gray>Webhook</gray> <dark_gray>→</dark_gray> " + getStatusText("SMPWebhook") + "\n" +
                "<dark_gray>-</dark_gray> <gray>Economy</gray> <dark_gray>→</dark_gray> " + getStatusText("SMPEconomy") + "\n" +
                "\n" +
                "<dark_gray>*----------------------------------------------------*</dark_gray>"
        ));

        return false;
    }

    private boolean getStatus(String plugin) {
        return Bukkit.getServer().getPluginManager().getPlugin(plugin) != null && Bukkit.getPluginManager().isPluginEnabled(plugin);
    }

    private String getStatusText(String plugin) {
        if (getStatus(plugin)) {
            return "<color:#00ff00>Online</color>";
        } else {
            return "<color:#ff0000>Offline</color>";
        }
    }
}

