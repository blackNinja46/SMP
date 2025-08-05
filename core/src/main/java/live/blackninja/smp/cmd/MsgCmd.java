package live.blackninja.smp.cmd;

import live.blackninja.smp.Core;
import live.blackninja.smp.builder.MessageBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public record MsgCmd(Core core) implements CommandExecutor {

    private static final Map<UUID, UUID> lastMessagedMap = new HashMap<>();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String @NotNull [] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("Nur Spieler können diesen Befehl nutzen.");
            return true;
        }

        Player playerSender = (Player) sender;

        if (args.length == 0) {
            playerSender.sendMessage(MessageBuilder.buildOld(Core.PREFIX + "§7Benutze: /msg [Spieler] <Nachricht>"));
            return true;
        }

        Player target;
        String message;

        if (args.length >= 2) {
            Player potentialTarget = Bukkit.getPlayerExact(args[0]);

            if (potentialTarget != null && potentialTarget.isOnline()) {
                target = potentialTarget;

                StringBuilder msgBuilder = new StringBuilder();
                for (int i = 1; i < args.length; i++) {
                    msgBuilder.append(args[i]).append(" ");
                }
                message = msgBuilder.toString().trim();

                lastMessagedMap.put(playerSender.getUniqueId(), target.getUniqueId());
                lastMessagedMap.put(target.getUniqueId(), playerSender.getUniqueId());

            } else {
                UUID last = lastMessagedMap.get(playerSender.getUniqueId());
                if (last == null) {
                    playerSender.sendMessage(MessageBuilder.buildOld(Core.PREFIX + "§7Du hast noch nie jemanden angeschrieben. Benutze: /msg [Spieler] <Nachricht>"));
                    return true;
                }

                target = Bukkit.getPlayer(last);
                if (target == null || !target.isOnline()) {
                    playerSender.sendMessage(MessageBuilder.buildOld(Core.PREFIX + "§7Dein letzter Chat-Partner ist nicht mehr online."));
                    return true;
                }

                StringBuilder msgBuilder = new StringBuilder();
                for (String arg : args) {
                    msgBuilder.append(arg).append(" ");
                }
                message = msgBuilder.toString().trim();
            }
        }

        else {
            UUID last = lastMessagedMap.get(playerSender.getUniqueId());
            if (last == null) {
                playerSender.sendMessage(MessageBuilder.buildOld(Core.PREFIX + "§7Du hast noch nie jemanden angeschrieben. Benutze: /msg [Spieler] <Nachricht>"));
                return true;
            }

            target = Bukkit.getPlayer(last);
            if (target == null || !target.isOnline()) {
                playerSender.sendMessage(MessageBuilder.buildOld(Core.PREFIX + "§7Dein letzter Chat-Partner ist nicht mehr online."));
                return true;
            }

            message = args[0];
        }

        playerSender.sendMessage(MessageBuilder.buildOld("%bDU §x§a§1§b§7§c§2-> %b" + target.getName() + "§x§a§1§b§7§c§2: §f" + message));
        target.sendMessage(MessageBuilder.buildOld("%b" + playerSender.getName() + " §x§a§1§b§7§c§2 -> %bDIR§x§a§1§b§7§c§2: §f" + message));

        playerSender.playSound(playerSender, Sound.ENTITY_CHICKEN_EGG, 2.0f, 2.0f);
        target.playSound(target, Sound.ENTITY_CHICKEN_EGG, 2.0f, 2.0f);

        return true;
    }
}