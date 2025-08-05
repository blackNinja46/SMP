package live.blackninja.smp.gui;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import live.blackninja.smp.Core;
import live.blackninja.smp.builder.ItemBuilder;
import live.blackninja.smp.builder.MessageBuilder;
import live.blackninja.smp.manger.HomeManger;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class ConfirmDeleteHomeGUI {

    public static void open(Player player, String home, Core core) {
        SmartInventory.builder()
                .manager(core.getInventoryManager())
                .title("§7Confirm Delete")
                .size(3, 9)
                .provider(new InventoryProvider() {
                    @Override
                    public void init(Player player, InventoryContents inventoryContents) {
                        HomeManger homeManger = core.getSmpManger().getHomeManger();

                        inventoryContents.set(1, 2, ClickableItem.of(new ItemBuilder(Material.RED_STAINED_GLASS_PANE)
                                .setDisplayName(MessageBuilder.buildOld("%rᴄᴀɴᴄᴇʟ"))
                                .setLore(MessageBuilder.buildOld("§fKlicke um abzubrechen"))
                                .build(), e -> {
                            HomesGUI.open(player, core);
                            player.playSound(player, Sound.BLOCK_TRIPWIRE_CLICK_ON, 1.0f, 1.0f);
                        }));

                        inventoryContents.set(1, 4, ClickableItem.empty(new ItemBuilder(Material.BLUE_DYE)
                                .setDisplayName(MessageBuilder.buildOld("%b" + home.replace("&", "§")))
                                .build()));

                        inventoryContents.set(1, 6, ClickableItem.of(new ItemBuilder(Material.LIME_STAINED_GLASS_PANE)
                                .setDisplayName(MessageBuilder.buildOld("%gᴄᴏɴғɪʀᴍ"))
                                .setLore(MessageBuilder.buildOld("§fKlicke um zu löschen"))
                                .build(), e -> {
                            homeManger.deleteHome(player.getName(), home);
                            HomesGUI.open(player, core);
                            player.sendActionBar("§7Home gelöscht");
                            player.playSound(player, Sound.BLOCK_TRIPWIRE_CLICK_ON, 1.0f, 1.0f);
                        }));

                    }

                    @Override
                    public void update(Player player, InventoryContents inventoryContents) {

                    }
                })
                .build().open(player);
    }
}
