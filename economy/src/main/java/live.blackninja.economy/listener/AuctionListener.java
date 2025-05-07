package live.blackninja.economy.listener;

import live.blackninja.economy.Economy;
import live.blackninja.economy.gui.AuctionGUI;
import live.blackninja.economy.manger.AuctionManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public record AuctionListener(Economy economy) implements Listener {

    @EventHandler
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        AuctionManager auctionManager = economy.getAuctionManager();

        if (AuctionGUI.getBidMode().containsKey(player)) {
            event.setCancelled(true);
            String message = event.getMessage();

            try {
                int bidAmount = Integer.parseInt(message);

                if (bidAmount <= AuctionGUI.getBidMode().get(player).getHighestBid()) {
                    player.sendMessage("§cDein Gebot muss höher sein als das aktuelle Gebot.");
                    return;
                }

                AuctionGUI.getBidMode().get(player).setHighestBid(bidAmount);
                AuctionGUI.getBidMode().get(player).setHighestBidder(player.getUniqueId());
                player.sendMessage("§aDu hast erfolgreich ein Gebot von §e" + bidAmount + "§a abgegeben.");
                player.playSound(player.getLocation(), "block.note_block.pling", 1, 1);
                AuctionGUI.getBidMode().remove(player);

            }catch (NumberFormatException e) {
                player.sendMessage("§cBitte gib eine gültige Zahl ein.");
                AuctionGUI.cancelBidMode(player);
                return;
            }

        }

    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        AuctionManager auctionManager = economy.getAuctionManager();

        if (auctionManager.hasUnclaimedItems(uuid)) {
            List<ItemStack> items = auctionManager.claimUnclaimedItems(uuid);
            for (ItemStack item : items) {
                HashMap<Integer, ItemStack> notStored = player.getInventory().addItem(item);
                if (!notStored.isEmpty()) {
                    for (ItemStack leftover : notStored.values()) {
                        player.getWorld().dropItemNaturally(player.getLocation(), leftover);
                    }
                }
            }
            player.sendMessage("§aDu hast Items vom Auktionshaus zurückbekommen!");
        }

        auctionManager.claimPendingPayout(player);
        auctionManager.claimPendingPayment(player);
    }


}
