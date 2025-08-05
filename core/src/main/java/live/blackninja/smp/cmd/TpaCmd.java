package live.blackninja.smp.cmd;

import live.blackninja.smp.Core;
import live.blackninja.smp.builder.MessageBuilder;
import live.blackninja.smp.gui.TpaGUI;
import live.blackninja.smp.manger.TpaManger;
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


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Nur Spieler können diesen Befehl nutzen.");
            return true;
        }

        Player player = (Player) sender;
        TpaManger tpaManger = core.getSmpManger().getTpaManger();
        Map<UUID, UUID> tpaRequests = tpaManger.getTpaRequests(); // Ziel -> Anfragender

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("accept") || args[0].equalsIgnoreCase("deny")) {
                player.sendMessage(MessageBuilder.buildOld(Core.PREFIX + "§7Bitte gib auch den Spielernamen an: %b/tpa " + args[0] + " <Spieler>"));
                return true;
            }

            if (args[0].equalsIgnoreCase("cancel")) {
                UUID targetUUID = player.getUniqueId();

                if (!tpaRequests.containsValue(targetUUID)) {
                    player.sendMessage(MessageBuilder.buildOld(Core.PREFIX + "§7Du hast keine %bAnfrage §7zum %rAbbrechen"));
                    return true;
                }
                player.sendMessage(MessageBuilder.buildOld(Core.PREFIX + "§7Deine %bAnfrage §7wurde zurückgezogen"));
                for (UUID requesterUUID : tpaRequests.keySet()) {
                    if (tpaRequests.get(requesterUUID).equals(targetUUID)) {
                        Player requester = Bukkit.getPlayer(requesterUUID);
                        if (requester != null && requester.isOnline()) {
                            requester.sendMessage(MessageBuilder.buildOld(Core.PREFIX + "§7Die %bTPA-Anfrage §7von %b" + player.getName() + " §7wurde zurückgezogen"));
                            tpaManger.cancelTpaRequest(requesterUUID);
                        }
                    }
                }

                return true;
            }

            // /tpa <Spieler>
            Player target = Bukkit.getPlayerExact(args[0]);
            if (target == null || !target.isOnline()) {
                player.sendMessage(MessageBuilder.buildOld(Core.PREFIX + "§7Dieser Spieler %rexistiert §7nicht!"));
                return true;
            }

            if (player.getUniqueId().equals(target.getUniqueId())) {
                player.sendMessage(MessageBuilder.buildOld(Core.PREFIX + "§7Du kannst dir %rnicht §7selber eine %bAnfrage §7senden"));
                return true;
            }

            if (tpaRequests.containsValue(player.getUniqueId())) {
                player.sendMessage(MessageBuilder.buildOld(Core.PREFIX + "§7Du hast bereits eine %rlaufende §7Anfrage!"));
                return true;
            }

            // Sende die TPA-Anfrage
            TpaGUI.open(player, core, target.getName(), true);

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
                player.sendMessage(MessageBuilder.buildOld("§7Keine %bAnfrage §7von " + requesterName + " §7gefunden."));
                return true;
            }

            if (subcommand.equalsIgnoreCase("accept")) {
                TpaGUI.open(player, core, requesterName, false);
                return true;

            } else {
                player.sendMessage("§cUngültiger Sub-Befehl: " + subcommand);
                return true;
            }
        }

        MessageBuilder.buildOld(Core.PREFIX + "§7Benutze %b/tpa <[Spieler] | accept | deny>");
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (!(sender instanceof Player)) return Collections.emptyList();

        Player player = (Player) sender;
        TpaManger tpaManger = core.getSmpManger().getTpaManger();
        Map<UUID, UUID> tpaRequests = tpaManger.getTpaRequests();

        if (args.length == 1) {
            String partial = args[0].toLowerCase();
            List<String> completions = new ArrayList<>();

            // Subcommands
            if ("accept".startsWith(partial)) completions.add("accept");
            if ("deny".startsWith(partial)) completions.add("deny");
            if ("cancel".startsWith(partial)) completions.add("cancel");

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

