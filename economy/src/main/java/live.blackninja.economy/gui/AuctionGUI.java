package live.blackninja.economy.gui;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.content.Pagination;
import fr.minuskube.inv.content.SlotIterator;
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

import java.util.*;

public class AuctionGUI {

    private static final Map<Player, AuctionSortMode> auctionSortMode = new HashMap<>();
    private static final Map<Player, Auction> bidMode = new HashMap<>();

    private static int page = 1;

    public static void open(Player player, Economy economy) {

        auctionSortMode.putIfAbsent(player, AuctionSortMode.PRICE_UP);

        SmartInventory.builder()
                .manager(economy.getInventoryManager())
                .size(6, 9)
                .title("§9Auction House §8(§7" + page + "§8) §8| §cBETA")
                .provider(new InventoryProvider() {
                    @Override
                    public void init(Player player, InventoryContents contents) {
                        contents.fillRow(0, ClickableItem.empty(new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setDisplayName(" ").build()));
                        contents.fillRow(5, ClickableItem.empty(new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setDisplayName(" ").build()));

                        Pagination pagination = contents.pagination();

                        contents.set(5, 2, ClickableItem.of(new ItemBuilder("http://textures.minecraft.net/texture/542fde8b82e8c1b8c22b22679983fe35cb76a79778429bdadabc397fd15061")
                                .setDisplayName("§7Seite Zurück")
                                .build(), event -> {
                            pagination.previous();
                            page = pagination.getPage();
                        }));

                        contents.set(5, 6, ClickableItem.of(new ItemBuilder("http://textures.minecraft.net/texture/406262af1d5f414c597055c22e39cce148e5edbec45559a2d6b88c8d67b92ea6")
                                .setDisplayName("§7Seite Vorwärts")
                                .build(), event -> {
                            pagination.next();
                        }));

                        setContent(player, contents, economy, pagination);
                    }

                    @Override
                    public void update(Player player, InventoryContents contents) {
                        setContent(player, contents, economy, contents.pagination());
                    }
                }).build().open(player);

    }

    private static void setContent(Player player, InventoryContents contents, Economy economy, Pagination pagination) {
        AuctionManager auctionManager = economy.getAuctionManager();

        contents.set(5, 4, ClickableItem.of(new ItemBuilder(Material.HOPPER)
                .setDisplayName("§aSortieren")
                .addLoreLine("§7Aktueller Modus: §b" + auctionSortMode.get(player).getDisplayName())
                .build(), event -> {
            toggleSortMode(player);
        }));

        ClickableItem[] items = new ClickableItem[auctionManager.getAuctions().size()];

        sortAuction(auctionManager.getAuctions(), player);

        for (int i = 0; i < auctionManager.getAuctions().size(); i++) {
            Auction auction = auctionManager.getAuctions().get(i);
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

            if (auction.getSeller().equals(player.getUniqueId())) {
                lore.add(MessageBuilder.buildOld("§fKlicke §8- §7Zurückziehen"));
            }else {
                lore.add(MessageBuilder.buildOld("§fLinksklick §8- §7Bieten"));
                lore.add(MessageBuilder.buildOld("§fRechtsklick §8- §7Sofortkauf"));
            }

            lore.add(MessageBuilder.buildOld(" "));
            lore.add(MessageBuilder.buildOld("§7%<§8§m                               §8§7%>"));

            item.setLore(lore);

            items[i] = ClickableItem.of(item, event -> {
                if (auction.getSeller().equals(player.getUniqueId())) {
                    AuctionConfirmGUI.open(player, economy, auction, false);
                    return;
                }
                if (event.isLeftClick()) {
                    player.closeInventory();
                    if (bidMode.containsKey(player)) {
                        return;
                    }
                    addBidMode(player, auction);
                    return;
                }
                if (event.isRightClick()) {
                    AuctionConfirmGUI.open(player, economy, auction, true);
                }
            });
        }

        pagination.setItems(items);
        pagination.setItemsPerPage(36);

        pagination.addToIterator(contents.newIterator(SlotIterator.Type.HORIZONTAL, 1, 0));
    }

    private static void toggleSortMode(Player player) {
        AuctionSortMode currentMode = auctionSortMode.getOrDefault(player, AuctionSortMode.PRICE_UP);

        switch (currentMode) {
            case PRICE_UP -> auctionSortMode.put(player, AuctionSortMode.PRICE_DOWN);
            case PRICE_DOWN -> auctionSortMode.put(player, AuctionSortMode.SELF_AUCTION);
            case SELF_AUCTION -> auctionSortMode.put(player, AuctionSortMode.TIME);
            case TIME -> auctionSortMode.put(player, AuctionSortMode.PRICE_UP);
        }
    }

    private static void sortAuction(List<Auction> auctions, Player viewer) {
        AuctionSortMode sortMode = auctionSortMode.getOrDefault(viewer, AuctionSortMode.PRICE_UP);

        if (sortMode == AuctionSortMode.SELF_AUCTION) {
            auctions.removeIf(auction -> !auction.getSeller().equals(viewer.getUniqueId()));
            auctions.sort(Comparator.comparingLong(Auction::getEndTime));
            return;
        }

        auctions.sort((a1, a2) -> {
            return switch (sortMode) {
                case PRICE_UP -> Integer.compare(a1.getStartPrice(), a2.getStartPrice());
                case PRICE_DOWN -> Integer.compare(a2.getStartPrice(), a1.getStartPrice());
                case TIME -> Long.compare(a1.getEndTime(), a2.getEndTime());
                default -> 0;
            };
        });
    }

    public static void addBidMode(Player player, Auction auction) {
        bidMode.put(player, auction);

        player.sendMessage(MessageBuilder.build("<gray>Bitte gebe ein <blue>Gebot </blue>an welches das aktuelle Gebot <red>überbietet</red>!</gray> <click:run_command:'/ah cancel'><dark_gray>[</dark_gray><dark_red>Abbrechen</dark_red><dark_gray>]</dark_gray></click>"));
    }

    public static void cancelBidMode(Player player) {
        bidMode.remove(player);

        player.sendMessage(MessageBuilder.build("<gray>Du hast die <blue>Eingabe </blue>abgebrochen!</gray>"));
    }

    public static Map<Player, Auction> getBidMode() {
        return bidMode;
    }
}

