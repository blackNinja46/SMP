package live.blackninja.smp.cmd;

import live.blackninja.smp.Core;
import live.blackninja.smp.builder.MessageBuilder;
import live.blackninja.smp.util.IntegerFormat;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.List;

public record TimerCmd(Core core) implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (!(sender instanceof Player)) return true;

        Player player = (Player) sender;

        if (!player.hasPermission("ninjasmp.cmd.timer")) {
            player.sendMessage(MessageBuilder.buildOld(Core.NO_PERMS));
            return true;
        }

        if (args.length == 0) {
            player.sendMessage("§7Benutze %b/timer <set | resume | pause>");
            return true;
        }

        switch (args[0]) {
            case "set" -> {
                if (args.length != 3) {
                    player.sendMessage(MessageBuilder.buildOld(Core.PREFIX + "§7Benutze %b/timer set <Sekunden> <nether|end>"));
                    return true;
                }
                String type = args[2].toLowerCase();
                try {
                    int seconds = Integer.parseInt(args[1]);
                    long timestamp = System.currentTimeMillis() / 1000 + seconds;

                    core.getSmpManger().getDelayedOpeningManger().setDate(type, timestamp);

                    player.sendMessage(MessageBuilder.buildOld(Core.PREFIX + "§7Der Timer für §b" + type + " §7wurde auf §b" + IntegerFormat.getFormattedTimeLong(timestamp) + " §7gesetzt."));

                } catch (NumberFormatException e) {
                    player.sendMessage(MessageBuilder.buildOld(Core.PREFIX + "§cUngültige Zahl! Bitte gib eine gültige Anzahl von Sekunden ein."));
                    return true;
                }
            }
            case "resume" -> {
                core.getSmpManger().getDelayedOpeningManger().startDelay();
                player.sendMessage(MessageBuilder.buildOld(Core.PREFIX + "§7Der %bTimer §7wurde gestartet."));
            }
            case "pause" -> {
                core.getSmpManger().getDelayedOpeningManger().pauseDelay();
                player.sendMessage(MessageBuilder.buildOld(Core.PREFIX + "§7Der %bTimer §7wurde pausiert."));
            }
            case "spawnDisplay" -> {
                core.getSmpManger().getDelayedOpeningManger().spawnTextDisplay(player.getLocation());
                player.sendMessage(MessageBuilder.buildOld(Core.PREFIX + "§7Der Text-Display wurde an deiner Position gesetzt."));
            }
            case "removeDisplay" -> {
                core.getSmpManger().getDelayedOpeningManger().removeTextDisplay();
                player.sendMessage(MessageBuilder.buildOld(Core.PREFIX + "§7Der Text-Display wurde entfernt."));
            }
            case "help" -> {
                player.sendMessage(MessageBuilder.buildOld(Core.PREFIX + "§7Verfügbare Befehle:"));
                player.sendMessage(MessageBuilder.buildOld("§7/timer set <Sekunden> <Typ> - Setzt den Timer für den angegebenen Typ."));
                player.sendMessage(MessageBuilder.buildOld("§7/timer resume - Startet den Timer."));
                player.sendMessage(MessageBuilder.buildOld("§7/timer pause - Pausiert den Timer."));
                player.sendMessage(MessageBuilder.buildOld("§7/timer spawnDisplay - Erstellt einen Text-Display an deiner Position."));
                player.sendMessage(MessageBuilder.buildOld("§7/timer removeDisplay - Entfernt den Text-Display."));
            }
        }

        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        return List.of("set", "resume", "pause", "spawnDisplay", "removeDisplay", "help");
    }
}
