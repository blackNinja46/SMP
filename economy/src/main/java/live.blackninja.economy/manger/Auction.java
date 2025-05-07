package live.blackninja.economy.manger;

import org.bukkit.inventory.ItemStack;
import java.util.UUID;

public class Auction {
    private UUID seller;
    private ItemStack item;
    private int startPrice;
    private int finalPrice;
    private int highestBid;
    private UUID highestBidder;
    private long endTime;

    public Auction(UUID seller, ItemStack item, int startPrice, int finalPrice, long endTime) {
        this.seller = seller;
        this.item = item;
        this.startPrice = startPrice;
        this.highestBid = startPrice;
        this.finalPrice = finalPrice;
        this.endTime = endTime;
    }

    public UUID getSeller() { return seller; }
    public ItemStack getItem() { return item; }
    public int getStartPrice() { return startPrice; }
    public int getHighestBid() { return highestBid; }
    public UUID getHighestBidder() { return highestBidder; }
    public long getEndTime() { return endTime; }

    public void setHighestBid(int highestBid) { this.highestBid = highestBid; }
    public void setHighestBidder(UUID highestBidder) { this.highestBidder = highestBidder; }

    public int getFinalPrice() {
        return finalPrice;
    }

    public void setFinalPrice(int finalPrice) {
        this.finalPrice = finalPrice;
    }
}
