package live.blackninja.smp.gui;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import live.blackninja.smp.Core;
import live.blackninja.smp.builder.ItemBuilder;
import live.blackninja.smp.builder.StatDisplayBuilder;
import live.blackninja.smp.manger.StatType;
import live.blackninja.smp.manger.StatsManger;
import live.blackninja.smp.util.IntegerFormat;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class LeaderboardGUI {

    public static void open(Player player, Core core) {
        SmartInventory.builder()
                .manager(core.getInventoryManager())
                .title("§7ʟᴇᴀᴅᴇʀʙᴏᴀʀᴅ")
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
        UUID uuid = player.getUniqueId();

        List<StatDisplayBuilder> stats = List.of(
                new StatDisplayBuilder(StatType.KILLS, Material.DIAMOND_SWORD, "ᴋɪʟʟs", 1, 1),
                new StatDisplayBuilder(StatType.DEATHS, Material.SKELETON_SKULL, "ᴅᴇᴀᴛʜs", 1, 2),
                new StatDisplayBuilder(StatType.PLAYTIME_IN_MINUTES, Material.CLOCK, "ᴘʟᴀʏᴛɪᴍᴇ", 1, 3),
                new StatDisplayBuilder(StatType.BLOCKS_PLACED, Material.STONE, "ʙʟᴏᴄᴋs ᴘʟᴀᴄᴇᴅ", 1, 4),
                new StatDisplayBuilder(StatType.BLOCKS_BROKEN, Material.COBBLESTONE, "ʙʟᴏᴄᴋs ʙʀᴏᴋᴇɴ", 1, 5),
                new StatDisplayBuilder(StatType.DISTANCE_WALKED, Material.IRON_BOOTS, "ᴅɪsᴛᴀɴᴄᴇ ᴡᴀʟᴋᴇᴅ", 1, 6),
                new StatDisplayBuilder(StatType.MOBS_KILLED, Material.ZOMBIE_HEAD, "ᴍᴏʙs ᴋɪʟʟᴇᴅ", 1, 7)
        );

        for (StatDisplayBuilder stat : stats) {
            int value = statsManger.getStat(uuid, stat.type);
            Map<UUID, Integer> allStats = statsManger.getAllStats(stat.type);

            List<Map.Entry<UUID, Integer>> sorted = new ArrayList<>(allStats.entrySet());
            sorted.sort((a, b) -> Integer.compare(b.getValue(), a.getValue()));

            int playerRank = -1;
            for (int i = 0; i < sorted.size(); i++) {
                if (sorted.get(i).getKey().equals(uuid)) {
                    playerRank = i + 1;
                    break;
                }
            }

            List<String> lore = new ArrayList<>();
            lore.add("§7Dein Rang: §f" + (playerRank != -1 ? "#" + playerRank : "Unplatziert"));
            lore.add("§7");

            int top = 0;
            for (Map.Entry<UUID, Integer> entry : sorted) {
                if (top >= 5) break;
                OfflinePlayer topPlayer = Bukkit.getOfflinePlayer(entry.getKey());
                String name = topPlayer.getName() != null ? topPlayer.getName() : "Unbekannt";
                String formattedValue = formatValue(stat.type, entry.getValue());
                lore.add("§f#" + (top + 1) + " §x§6§6§f§f§9§6" + name + " §x§a§1§b§7§c§2- §f" + formattedValue);
                top++;
            }

            lore.add("§7");

            ItemStack item = new ItemBuilder(stat.material)
                    .setDisplayName("§x§6§6§f§f§9§6" + stat.displayName)
                    .setLore(lore)
                    .addFlags(ItemFlag.HIDE_ATTRIBUTES)
                    .build();

            contents.set(stat.row, stat.column, ClickableItem.empty(item));
        }
    }

    private static String formatValue(StatType type, int value) {
        if (type == StatType.PLAYTIME_IN_MINUTES) {
            return IntegerFormat.getFormattedTime(value * 60);
        }
        if (type == StatType.DISTANCE_WALKED) {
            return String.format("%.2f km", value / 1000.0);
        }
        return String.valueOf(value);
    }




}


