package live.blackninja.economy.cmd;

import live.blackninja.economy.Economy;
import live.blackninja.economy.gui.AuctionGUI;
import live.blackninja.economy.manger.Auction;
import live.blackninja.economy.manger.AuctionManager;
import live.blackninja.smp.Core;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public record AuctionCmd(Economy economy) implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Nur Spieler können diesen Befehl benutzen!");
            return true;
        }

        AuctionManager auctionManager = economy.getAuctionManager();

        if (args.length == 0) {
            AuctionGUI.open(player, economy);
            return true;
        }

        if (args[0].equalsIgnoreCase("sell")) {
            if (args.length < 4) {
                player.sendMessage("§cVerwendung: /ah sell <Kaufpreis> <Startpreis> <DauerInMinuten>");
                return true;
            }
            try {
                int finalPrice = Integer.parseInt(args[1]);
                int startPrice = Integer.parseInt(args[2]);
                int durationMinutes = Integer.parseInt(args[3]);

                ItemStack item = player.getInventory().getItemInMainHand();
                if (item == null || item.getType() == Material.AIR) {
                    player.sendMessage("§cDu musst ein Item in der Hand halten!");
                    return true;
                }

                if (auctionManager.isBanned(item.getType())) {
                    player.sendMessage("§cDieses Item ist im Auktionshaus gebannt!");
                    return true;
                }

                if (startPrice < 0 || finalPrice < 0) {
                    player.sendMessage("§cDer Preis muss positiv sein!");
                    return true;
                }

                Auction auction = new Auction(player.getUniqueId(), item.clone(), startPrice, finalPrice, System.currentTimeMillis() + (durationMinutes * 60 * 1000L));
                auctionManager.addAuction(auction);

                player.getInventory().setItemInMainHand(null);
                player.sendMessage("§7Dein §9Item §7wurde erfolgreich ins §6Auktionshaus §7gestellt!");
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1.5f);

                auctionManager.addLog(player.getName() + " hat " + item.getType().name() + " für " + finalPrice + " Diamanten angeboten.");

            } catch (NumberFormatException e) {
                player.sendMessage("§cBitte gib gültige Zahlen für Preis und Dauer an!");
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("withdraw")) {
            List<Auction> toRemove = new ArrayList<>();
            for (Auction auction : auctionManager.getAuctions()) {
                if (auction.getSeller().equals(player.getUniqueId())) {
                    if (auction.getEndTime() > System.currentTimeMillis()) {
                        toRemove.add(auction);
                    }
                }
            }

            if (toRemove.isEmpty()) {
                player.sendMessage("§cDu hast keine aktiven Auktionen zum Zurückziehen.");
            } else {
                for (Auction auction : toRemove) {
                    auctionManager.removeAuction(auction);
                    ItemStack item = auction.getItem();
                    HashMap<Integer, ItemStack> notStored = player.getInventory().addItem(item);
                    if (!notStored.isEmpty()) {
                        for (ItemStack leftover : notStored.values()) {
                            player.getWorld().dropItemNaturally(player.getLocation(), leftover);
                        }
                    }
                }
                player.sendMessage("§aDu hast " + toRemove.size() + " Auktion(en) zurückgezogen.");
                auctionManager.addLog(player.getName() + " hat " + toRemove.size() + " Auktion(en) manuell zurückgezogen.");
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("paydebt")) {
            if (auctionManager.payDebts(player)) {
                player.sendMessage("§aAlle deine offenen Auktionsschulden wurden bezahlt.");
            } else {
                player.sendMessage("§cKeine offenen Zahlungen oder nicht genug Diamanten!");
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("cancel")) {
            AuctionGUI.cancelBidMode(player);
            return true;
        }

        if (!player.hasPermission("ninjasmp.economy.cmd.auction.admin")) {
            player.sendMessage(Core.NO_PERMS);
            return true;
        }

        if (args[0].equalsIgnoreCase("ban")) {
            ItemStack item = player.getInventory().getItemInMainHand();
            if (item == null || item.getType() == Material.AIR) {
                player.sendMessage("§cDu musst ein Item in der Hand halten!");
                return true;
            }
            auctionManager.banItem(item.getType());
            player.sendMessage("§aItem " + item.getType().name() + " wurde gebannt!");

            auctionManager.addLog(player.getName() + " hat das Item" + item.getType().name() + " gebannt.");
            return true;
        }

        if (args[0].equalsIgnoreCase("unban")) {
            ItemStack item = player.getInventory().getItemInMainHand();
            if (item == null || item.getType() == Material.AIR) {
                player.sendMessage("§cDu musst ein Item in der Hand halten!");
                return true;
            }
            auctionManager.unbanItem(item.getType());
            player.sendMessage("§aItem " + item.getType().name() + " wurde wieder freigegeben!");

            auctionManager.addLog(player.getName() + " hat das Item" + item.getType().name() + " entbannt.");
            return true;
        }

        if (args[0].equalsIgnoreCase("logs")) {
            if (!player.hasPermission("auctionhouse.logs")) {
                player.sendMessage("§cDazu hast du keine Rechte!");
                return true;
            }
            List<String> logs = auctionManager.getLogs();
            if (logs.isEmpty()) {
                player.sendMessage("§eKeine Logs vorhanden.");
            } else {
                player.sendMessage("§8--- §6Auktionshaus Logs §8---");
                for (String log : logs) {
                    player.sendMessage(log);
                }
            }
            return true;
        }

        if (!player.hasPermission("ninjasmp.economy.cmd.auction.admin")) {
            player.sendMessage("§cUnbekannter Befehl. Verwende /ah, /ah sell, /ah withdraw, /ah paydebt.");
            return true;
        }
        player.sendMessage("§cUnbekannter Befehl. Verwende /ah, /ah sell, /ah withdraw, /ah paydebt, /ah ban oder /ah unban, /ah logs.");
        return true;
    }

    private static final List<String> SUBCOMMANDS = List.of("sell", "cancel", "withdraw", "paydebt");
    private static final List<String> SUBCOMMANDS_ADMIN = List.of("ban", "unban", "logs");

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            if (sender.hasPermission("ninjasmp.economy.cmd.auction.admin")) {
                for (String sub : SUBCOMMANDS_ADMIN) {
                    if (sub.toLowerCase().startsWith(args[0].toLowerCase())) {
                        completions.add(sub);
                    }
                }
            }
            for (String sub : SUBCOMMANDS) {
                if (sub.toLowerCase().startsWith(args[0].toLowerCase())) {
                    completions.add(sub);
                }
            }
        }
        return completions;
    }
}
