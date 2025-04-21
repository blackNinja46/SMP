package live.blackninja.smp.cmd;

import live.blackninja.smp.Core;
import live.blackninja.smp.builder.MessageBuilder;
import live.blackninja.smp.manger.SMPManger;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public record SpawnCmd(Core core) implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) return true;

        Player player = (Player) sender;
        SMPManger smpManger = core.getSmpManger();

        if (smpManger.getConfig().getConfig().contains("SpawnLocation")) {
            smpManger.teleport(player, smpManger.getConfig().getLocation("SpawnLocation"));
            player.sendMessage(MessageBuilder.buildOld(Core.PREFIX + "§7Du wurdest %gerfolgreich §7zum %ySpawn §7teleportiert"));
        }else {
            player.sendMessage(MessageBuilder.buildOld(Core.PREFIX + "§7Es konnte %rkein §7Spawn gefunden werden! Bitte setze einen mit %b/smp spawn§7!"));
        }

        return false;
    }
}
