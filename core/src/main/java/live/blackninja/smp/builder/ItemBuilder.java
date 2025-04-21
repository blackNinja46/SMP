package live.blackninja.smp.builder;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CrossbowMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.*;

public class ItemBuilder {

    private final ItemStack item;
    private final ItemMeta meta;

    public ItemBuilder(Material material) {
        item = new ItemStack(material);
        meta = item.getItemMeta();
    }

    public ItemBuilder(Material material, int amount) {
        item = new ItemStack(material, amount);
        meta = item.getItemMeta();
    }

    public ItemBuilder(String url) {
        item = getCustomSkull(url);
        meta = item.getItemMeta();
    }

    public ItemBuilder setDisplayName(String displayName) {
        assert displayName != null;
        meta.setDisplayName(displayName);
        return this;
    }

    public ItemBuilder setSkullOwner(Player owner) {
        assert owner != null;
        try {
            SkullMeta skull = (SkullMeta) item.getItemMeta();
            skull.setOwningPlayer(owner);
            item.setItemMeta(skull);
            return this;
        } catch (ClassCastException e) {
            return null;
        }
    }

    public ItemBuilder setLore(String... lore) {
        assert lore != null;
        meta.setLore(Arrays.asList(lore));
        return this;
    }

    public ItemBuilder addLoreLine(String lore) {
        assert lore != null;
        List<String> loreList;
        if (meta.hasLore() && meta.getLore() != null)
            loreList = new ArrayList<>(meta.getLore());
        else
            loreList = new ArrayList<>();
        loreList.add(lore);
        meta.setLore(loreList);
        return this;
    }

    public ItemBuilder addLoreLineWithBoolean(String lore, boolean aktiv) {
        if (aktiv) {
            assert lore != null;
            List<String> loreList;
            if (meta.hasLore() && meta.getLore() != null)
                loreList = new ArrayList<>(meta.getLore());
            else
                loreList = new ArrayList<>();
            loreList.add(lore);
            meta.setLore(loreList);
        }
            return this;
    }

    public ItemBuilder addFlags(ItemFlag flag) {
        meta.addItemFlags(flag);
        return this;
    }

    public ItemBuilder setLore(List<String> lore) {
        assert lore != null;
        meta.setLore(lore);
        return this;
    }

    public ItemBuilder setUnbreakable(boolean unbreakable) {
        meta.setUnbreakable(unbreakable);
        return this;
    }

    /*
    public ItemBuilder setLocalizedName(String localizedName) {
        assert localizedName != null;
        meta.setLocalizedName(localizedName);
        return this;
    }
    */

    public ItemBuilder setAmount(int amount) {
        item.setAmount(amount);
        return this;
    }

    public ItemBuilder setDurability(short durability) {
        item.setDurability(durability);
        return this;
    }

    public ItemBuilder setCharged(Material material) {
        CrossbowMeta crossbowMeta = (CrossbowMeta) meta;
        crossbowMeta.addChargedProjectile(new ItemBuilder(material).build());
        return this;
    }

    public ItemBuilder addEnchantment(Enchantment enchantment, int level) {
        assert enchantment != null;
        meta.addEnchant(enchantment, level, true);
        return this;
    }

    public ItemBuilder addEnchantment(Enchantment enchantment, int level, boolean enchanted) {
        assert enchantment != null;
        if (enchanted) {
            meta.addEnchant(enchantment, level, true);
        }
        return this;
    }

    public ItemBuilder setCustomModelData(int customModelData) {
        meta.setCustomModelData(customModelData);
        return this;
    }

    public ItemStack build() {
        item.setItemMeta(meta);
        return item;
    }

    public ItemBuilder setLeatherArmorColor(Color color) {
        LeatherArmorMeta leatherArmorMeta = (LeatherArmorMeta) meta;
        leatherArmorMeta.setColor(color);
        return (ItemBuilder) leatherArmorMeta;
    }

    public static ItemStack getCustomSkull(String textureUrl) {
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD, 1);

        PlayerProfile profile = Bukkit.createProfile(UUID.randomUUID(), null);

        String base64 = Base64.getEncoder().encodeToString((
                "{\"textures\":{\"SKIN\":{\"url\":\"" + textureUrl + "\"}}}"
        ).getBytes());
        profile.setProperty(new ProfileProperty("textures", base64));

        skull.editMeta(meta -> {
            if (meta instanceof SkullMeta skullMeta) {
                skullMeta.setPlayerProfile(profile);
            }
        });

        return skull;
    }

}
