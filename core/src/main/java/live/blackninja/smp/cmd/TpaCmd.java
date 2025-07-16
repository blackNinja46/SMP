package live.blackninja.smp.cmd;

import live.blackninja.smp.Core;
import live.blackninja.smp.builder.MessageBuilder;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public record TpaCmd(Core core) implements CommandExecutor, TabCompleter {

    private static Map<UUID, UUID> tpaRequests = new HashMap<>(); // Ziel -> Anfragender
    private static Map<UUID, Integer> tpaTimeouts = new HashMap<>(); // Zum Canceln der Ablauf-Tasks

    private static final String PREFIX = "§8[§f\uEfe2§8] §r";

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Nur Spieler können diesen Befehl nutzen.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("accept") || args[0].equalsIgnoreCase("deny")) {
                player.sendMessage(MessageBuilder.buildOld(Core.PREFIX + "§7Bitte gib auch den Spielernamen an: %b/tpa " + args[0] + " <Spieler>"));
                return true;
            }

            // /tpa <Spieler>
            Player target = Bukkit.getPlayerExact(args[0]);
            if (target == null || !target.isOnline()) {
                player.sendMessage(MessageBuilder.buildOld(Core.PREFIX + "§7Dieser Spieler %rexistiert §7nicht!"));
                return true;
            }

            if (player.getUniqueId().equals(target.getUniqueId())) {
                player.sendMessage(MessageBuilder.buildOld(Core.PREFIX + "§7Du kannst dir %rnicht §7selber eine %bTPA-Anfrage §7senden"));
                return true;
            }

            if (tpaRequests.containsValue(player.getUniqueId())) {
                player.sendMessage(MessageBuilder.buildOld(Core.PREFIX + "§7Du hast bereits eine %rlaufende §7Anfrage!"));
                return true;
            }

            tpaRequests.put(target.getUniqueId(), player.getUniqueId());

            player.sendMessage(MessageBuilder.buildOld(PREFIX + "§aTPA-Anfrage an %b" + target.getDisplayName() + " §7gesendet."));
            target.sendMessage(MessageBuilder.buildOld(PREFIX + "%b" + player.getDisplayName() + " §7möchte sich zu dir teleportieren."));
            target.sendMessage(MessageBuilder.build("<click:run_command:'/tpa accept " + player.getName() + "'><gray>[</gray><color:#05ff4c>Akzeptieren</color><gray>]</gray></click> <dark_gray>|</dark_gray> <click:run_command:'/tpa deny " + player.getName() + "'><gray>[</gray><color:#ff7230>Ablehnen</color><gray>]</gray></click>"));

            int taskId = Bukkit.getScheduler().scheduleSyncDelayedTask(core, () -> {
                if (tpaRequests.containsKey(target.getUniqueId()) &&
                        tpaRequests.get(target.getUniqueId()).equals(player.getUniqueId())) {
                    tpaRequests.remove(target.getUniqueId());
                    tpaTimeouts.remove(target.getUniqueId());
                    player.sendMessage("§7Deine %bTPA-Anfrage §7an %b" + target.getDisplayName() + " §7ist abgelaufen.");
                    target.sendMessage("§7Die %bTPA-Anfrage von %b" + player.getDisplayName() + " §7ist abgelaufen.");
                }
            }, 20L * 120); // 120 Sekunden

            tpaTimeouts.put(target.getUniqueId(), taskId);

            return true;
        }

        if (args.length == 2) {
            String subcommand = args[0];
            String requesterName = args[1];
            Player requester = Bukkit.getPlayerExact(requesterName);
            if (requester == null) {
                player.sendMessage(MessageBuilder.buildOld(Core.PREFIX + "§7Dieser Spieler %rexistiert §7nicht!"));
                return true;
            }

            UUID targetUUID = player.getUniqueId();
            UUID requesterUUID = requester.getUniqueId();

            if (!tpaRequests.containsKey(targetUUID) || !tpaRequests.get(targetUUID).equals(requesterUUID)) {
                player.sendMessage(MessageBuilder.buildOld("§7Keine %bTPA-Anfrage §7von " + requesterName + " §7gefunden."));
                return true;
            }

            if (subcommand.equalsIgnoreCase("accept")) {
                core.getSmpManger().teleport(requester, player.getLocation());
                requester.sendMessage(MessageBuilder.buildOld(PREFIX + "§7Deine %bTPA-Anfrage §7an %b" + player.getDisplayName() + " §7wurde %gakzeptiert"));
                player.sendMessage(MessageBuilder.buildOld(PREFIX + "§7Du hast die %bTPA-Anfrage §7von %b" + requester.getDisplayName() + " %gakzeptiert"));

                cancelTpaRequest(targetUUID);
                return true;

            } else if (subcommand.equalsIgnoreCase("deny")) {
                requester.sendMessage(MessageBuilder.buildOld(PREFIX + "§7Deine %bTPA-Anfrage an %b" + player.getDisplayName() + " §7wurde %rabgelehnt"));
                player.sendMessage(MessageBuilder.buildOld(PREFIX + "§7Du hast die %bTPA-Anfrage §7von %b" + requester.getDisplayName() + " %rabgelehnt"));

                cancelTpaRequest(targetUUID);
                return true;

            } else {
                player.sendMessage("§cUngültiger Sub-Befehl: " + subcommand);
                return true;
            }
        }

        MessageBuilder.buildOld(Core.PREFIX + "§7Benutze %b/tpa <[Spieler] | accept | deny>");
        return true;
    }

    private void cancelTpaRequest(UUID targetUUID) {
        tpaRequests.remove(targetUUID);
        if (tpaTimeouts.containsKey(targetUUID)) {
            Bukkit.getScheduler().cancelTask(tpaTimeouts.get(targetUUID));
            tpaTimeouts.remove(targetUUID);
        }
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (!(sender instanceof Player)) return Collections.emptyList();
        Player player = (Player) sender;

        if (args.length == 1) {
            String partial = args[0].toLowerCase();
            List<String> completions = new ArrayList<>();

            // Subcommands
            if ("accept".startsWith(partial)) completions.add("accept");
            if ("deny".startsWith(partial)) completions.add("deny");

            // Online-Spieler
            for (Player online : Bukkit.getOnlinePlayers()) {
                if (!online.equals(player) &&
                        online.getName().toLowerCase().startsWith(partial)) {
                    completions.add(online.getName());
                }
            }
            return completions;
        }

        if (args.length == 2) {
            String sub = args[0].toLowerCase();
            String partial = args[1].toLowerCase();
            if (sub.equals("accept") || sub.equals("deny")) {
                // Nur Spieler vorschlagen, die dir eine Anfrage geschickt haben
                return tpaRequests.entrySet().stream()
                        .filter(e -> e.getKey().equals(player.getUniqueId()))
                        .map(e -> Bukkit.getPlayer(e.getValue()))
                        .filter(Objects::nonNull)
                        .map(Player::getName)
                        .filter(name -> name.toLowerCase().startsWith(partial))
                        .collect(Collectors.toList());
            }
        }

        return Collections.emptyList();
    }
}

