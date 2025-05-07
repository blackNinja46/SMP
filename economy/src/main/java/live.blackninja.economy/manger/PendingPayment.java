package live.blackninja.economy.manger;

import org.bukkit.inventory.ItemStack;

public class PendingPayment {

    private ItemStack item;
    private int amount;
    private long createdAt;

    public PendingPayment(ItemStack item, int amount) {
        this.item = item;
        this.amount = amount;
        this.createdAt = System.currentTimeMillis();
    }

    public ItemStack getItem() {
        return item;
    }

    public int getAmount() {
        return amount;
    }

    public long getCreatedAt() {
        return createdAt;
    }
}