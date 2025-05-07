package live.blackninja.economy.gui;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import live.blackninja.economy.Economy;
import live.blackninja.economy.manger.Auction;
import live.blackninja.economy.manger.AuctionManager;
import live.blackninja.smp.builder.ItemBuilder;
import live.blackninja.smp.builder.MessageBuilder;
import live.blackninja.smp.util.IntegerFormat;
import live.blackninja.smp.util.uuid.UUIDFetcher;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class AuctionConfirmGUI {

    public static void open(Player player, Economy economy, Auction auction, boolean isBuying) {
        SmartInventory.builder()
                .manager(economy.getInventoryManager())
                .size(3, 9)
                .title("§9Auction House §8(§7Bestätigung§8)")
                .provider(new InventoryProvider() {
                    @Override
                    public void init(Player player, InventoryContents contents) {
                        contents.fill(ClickableItem.empty(new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setDisplayName(" ").build()));

                        AuctionManager auctionManager = economy.getAuctionManager();

                        contents.set(1, 1, ClickableItem.of(new ItemBuilder("http://textures.minecraft.net/texture/beb588b21a6f98ad1ff4e085c552dcb050efc9cab427f46048f18fc803475f7")
                                .setDisplayName("§cAbbrechen")
                                .build(), event -> {
                            player.closeInventory();
                        }));

                        contents.set(1, 7, ClickableItem.of(new ItemBuilder("http://textures.minecraft.net/texture/a92e31ffb59c90ab08fc9dc1fe26802035a3a47c42fee63423bcdb4262ecb9b6")
                                .setDisplayName(isBuying ? "§aKaufen" : "§aZurücknehmen")
                                .build(), event -> {
                            if (isBuying) {
                                if (economy.getAuctionManager().getPlayerDiamonds(player) < auction.getFinalPrice()) {
                                    player.sendMessage(MessageBuilder.buildOld("§cDu hast nicht genug Geld!"));
                                    return;
                                }
                                if (auction.getEndTime() < System.currentTimeMillis()) {
                                    player.sendMessage(MessageBuilder.buildOld("§cDie Auktion ist bereits abgelaufen!"));
                                    return;
                                }
                                player.closeInventory();
                                player.playSound(player.getLocation(), "block.note_block.pling", 1, 1);
                                economy.getAuctionManager().removePlayerDiamonds(player, auction.getFinalPrice());
                                player.getInventory().addItem(auction.getItem());
                                auctionManager.removeAuction(auction);
                                player.sendMessage(MessageBuilder.buildOld("§7Du hast die §9Auktion §7für §b" + auction.getFinalPrice() + " §7Diamanten gekauft!"));
                                return;
                            }
                            Player seller = economy.getServer().getPlayer(auction.getSeller());
                            auctionManager.addItemToPlayer(player, auction.getItem());
                            auctionManager.removeAuction(auction);
                            player.closeInventory();
                            player.sendMessage(MessageBuilder.buildOld("§7Du hast die §9Auktion §7erfolgreich zurückgezogen!"));
                            if (seller != null && seller.isOnline()) {
                                auctionManager.addPlayerDiamonds(player, auction.getFinalPrice());
                                return;
                            }
                            auctionManager.addPendingPayout(auction.getSeller(), auction.getFinalPrice());
                        }));

                        setContent(player, economy, contents, auction, isBuying);
                    }

                    @Override
                    public void update(Player player, InventoryContents contents) {
                        setContent(player, economy, contents, auction, isBuying);
                    }
                }).build().open(player);
    }

    private static void setContent(Player player, Economy economy, InventoryContents contents, Auction auction, boolean isBuying) {
        ItemStack item = auction.getItem().clone();

        List<String> lore = new ArrayList<>();
        long finalDuration = ((auction.getEndTime() - System.currentTimeMillis()) / 1000);

        lore.add(MessageBuilder.buildOld(" "));
        lore.add(MessageBuilder.buildOld("§7%<§8§m                               §8§7%>"));
        lore.add(MessageBuilder.buildOld(" "));
        lore.add(MessageBuilder.buildOld("§7Sofortkauf Preis: %b§l" + auction.getFinalPrice() + " §7Diamanten"));
        lore.add(MessageBuilder.buildOld("§7Aktuelles Gebot: %b§l" + auction.getHighestBid() + " §7Diamanten"));
        lore.add(MessageBuilder.buildOld("§7Verbleibende Zeit: §a§l" + IntegerFormat.getFormattedTimeLong(finalDuration)));
        lore.add(MessageBuilder.buildOld("§7Höchster Bieter: §9" + (auction.getHighestBidder() != null ? UUIDFetcher.getName(auction.getHighestBidder()) : "Keiner")));
        lore.add(MessageBuilder.buildOld("§7Verkauf durch: §9" + UUIDFetcher.getName(auction.getSeller())));

        lore.add(MessageBuilder.buildOld(" "));
        lore.add(MessageBuilder.buildOld("§7%<§8§m                               §8§7%>"));

        item.setLore(lore);

        contents.set(1, 4, ClickableItem.empty(item));
    }

}
