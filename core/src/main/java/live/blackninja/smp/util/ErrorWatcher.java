package live.blackninja.smp.util;

import live.blackninja.smp.Core;
import live.blackninja.smp.builder.MessageBuilder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ErrorWatcher {
    private static final Set<UUID> activeDebugPlayers = new HashSet<>();

    public static void enableDebug(Player player) {
        activeDebugPlayers.add(player.getUniqueId());
        player.sendMessage(MessageBuilder.buildOld(Core.PREFIX + "§7Der %bDebug-Mode §7ist nun %gaktiviert"));
    }

    public static void disableDebug(Player player) {
        activeDebugPlayers.remove(player.getUniqueId());
        player.sendMessage(MessageBuilder.buildOld(Core.PREFIX + "§7Der %bDebug-Mode §7ist nun %rdeaktiviert"));
    }

    public static boolean isDebugging(Player player) {
        return activeDebugPlayers.contains(player.getUniqueId());
    }

    public static void broadcastError(String message) {
        Bukkit.getOnlinePlayers().stream()
                .filter(ErrorWatcher::isDebugging)
                .forEach(p -> p.sendMessage(MessageBuilder.buildOld("§8[%rError§8] §7" + message)));
    }
}
