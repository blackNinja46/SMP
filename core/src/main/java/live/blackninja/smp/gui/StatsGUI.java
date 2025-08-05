package live.blackninja.smp.gui;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import live.blackninja.smp.Core;
import live.blackninja.smp.builder.ItemBuilder;
import live.blackninja.smp.builder.MessageBuilder;
import live.blackninja.smp.manger.HomeManger;
import live.blackninja.smp.manger.StatType;
import live.blackninja.smp.manger.StatsManger;
import live.blackninja.smp.util.IntegerFormat;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class StatsGUI {

    public static void open(Player player, Core core) {
        SmartInventory.builder()
                .manager(core.getInventoryManager())
                .title("§7sᴛᴀᴛs ғʀᴏᴍ " + player.getName())
                .size(4, 9)
                .provider(new InventoryProvider() {
                    @Override
                    public void init(Player player, InventoryContents contents) {
                        setContent(player, contents, core);
                    }

                    @Override
                    public void update(Player player, InventoryContents contents) {
                        setContent(player, contents, core);
                    }
                })
                .build().open(player);
    }

    private static void setContent(Player player, InventoryContents contents, Core core) {
        StatsManger statsManger = core.getStatsManger();

        contents.set(1, 1, ClickableItem.empty(new ItemBuilder(Material.DIAMOND_SWORD)
                .setDisplayName(MessageBuilder.buildOld("§x§6§6§f§f§9§6ᴋɪʟʟs"))
                .addLoreLine("§7" + statsManger.getStat(player.getUniqueId(), StatType.KILLS))
                .addFlags(ItemFlag.HIDE_ATTRIBUTES)
                .build()));

        contents.set(1, 2, ClickableItem.empty(new ItemBuilder(Material.SKELETON_SKULL)
                .setDisplayName(MessageBuilder.buildOld("§x§6§6§f§f§9§6ᴅᴇᴀᴛʜs"))
                .addLoreLine("§7" + statsManger.getStat(player.getUniqueId(), StatType.DEATHS))
                .build()));

        contents.set(1, 3, ClickableItem.empty(new ItemBuilder(Material.CLOCK)
                .setDisplayName(MessageBuilder.buildOld("§x§6§6§f§f§9§6ᴘʟᴀʏᴛɪᴍᴇ"))
                .addLoreLine("§7" + IntegerFormat.getFormattedTime(statsManger.getStat(player.getUniqueId(), StatType.PLAYTIME_IN_MINUTES) * 60))
                .build()));

        contents.set(1, 4, ClickableItem.empty(new ItemBuilder(Material.STONE)
                .setDisplayName(MessageBuilder.buildOld("§x§6§6§f§f§9§6ʙʟᴏᴄᴋs ᴘʟᴀᴄᴇᴅ"))
                .addLoreLine("§7" + statsManger.getStat(player.getUniqueId(), StatType.BLOCKS_PLACED))
                .build()));

        contents.set(1, 5, ClickableItem.empty(new ItemBuilder(Material.COBBLESTONE)
                .setDisplayName(MessageBuilder.buildOld("§x§6§6§f§f§9§6ʙʟᴏᴄᴋs ʙʀᴏᴋᴇɴ"))
                .addLoreLine("§7" + statsManger.getStat(player.getUniqueId(), StatType.BLOCKS_BROKEN))
                .build()));

        contents.set(1, 6, ClickableItem.empty(new ItemBuilder(Material.IRON_BOOTS)
                .setDisplayName(MessageBuilder.buildOld("§x§6§6§f§f§9§6ᴅɪsᴛᴀɴᴄᴇ ᴡᴀʟᴋᴇᴅ"))
                .addLoreLine("§7" + Float.toString(((float) statsManger.getStat(player.getUniqueId(), StatType.DISTANCE_WALKED) / 1000)) + " km")
                .addFlags(ItemFlag.HIDE_ATTRIBUTES)
                .build()));

        contents.set(1, 7, ClickableItem.empty(new ItemBuilder(Material.ZOMBIE_HEAD)
                .setDisplayName(MessageBuilder.buildOld("§x§6§6§f§f§9§6ᴍᴏʙs ᴋɪʟʟs"))
                .addLoreLine("§7" + statsManger.getStat(player.getUniqueId(), StatType.MOBS_KILLED))
                .build()));
    }
}


