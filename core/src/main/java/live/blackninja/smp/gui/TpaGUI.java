package live.blackninja.smp.gui;

import com.sun.source.doctree.SeeTree;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import live.blackninja.smp.Core;
import live.blackninja.smp.builder.ItemBuilder;
import live.blackninja.smp.builder.MessageBuilder;
import live.blackninja.smp.manger.StatType;
import live.blackninja.smp.manger.StatsManger;
import live.blackninja.smp.manger.TpaManger;
import live.blackninja.smp.util.IntegerFormat;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;

public class TpaGUI {

    public static void open(Player player, Core core, String targetPlayerName, boolean isRequestConfirm) {
        SmartInventory.builder()
                .manager(core.getInventoryManager())
                .title(isRequestConfirm ? "§7ᴄᴏɴғɪʀᴍ ʀᴇǫᴜᴇsᴛ" : "§7ᴀᴄᴄᴇᴘᴛ ʀᴇǫᴜᴇsᴛ")
                .size(3, 9)
                .provider(new InventoryProvider() {
                    @Override
                    public void init(Player player, InventoryContents contents) {
                        Player target = core.getServer().getPlayer(targetPlayerName);
                        TpaManger tpaManger = core.getSmpManger().getTpaManger();

                        if (target == null || !target.isOnline()) {
                            return;
                        }

                        contents.set(1, 3, ClickableItem.empty(new ItemBuilder(getLocationMaterial(target))
                                .setDisplayName(MessageBuilder.buildOld("§x§6§6§f§f§9§6ʟᴏᴄᴀᴛɪᴏɴ"))
                                .addLoreLine("§7" + getLocation(target))
                                .build()));

                        contents.set(1, 5, ClickableItem.empty(new ItemBuilder(Material.PLAYER_HEAD)
                                .setSkullOwner(target)
                                .setDisplayName(MessageBuilder.buildOld("§x§6§6§f§f§9§6ᴘʟᴀʏᴇʀ"))
                                .addLoreLine("§7" + targetPlayerName)
                                .build()));

                        setContent(player, contents, core, targetPlayerName, isRequestConfirm);
                    }

                    @Override
                    public void update(Player player, InventoryContents contents) {
                        setContent(player, contents, core, targetPlayerName, isRequestConfirm);
                    }
                })
                .build().open(player);
    }

    private static void setContent(Player player, InventoryContents contents, Core core, String targetPlayerName, boolean isRequestConfirm) {
        Player target = core.getServer().getPlayer(targetPlayerName);
        TpaManger tpaManger = core.getSmpManger().getTpaManger();

        if (target == null || !target.isOnline()) {
            return;
        }

        if (isRequestConfirm) {
            contents.set(1, 1, ClickableItem.of(new ItemBuilder(Material.RED_STAINED_GLASS_PANE)
                    .setDisplayName(MessageBuilder.buildOld("%rᴄᴀɴᴄᴇʟ"))
                    .setLore(MessageBuilder.buildOld("§fKlicke um die teleportation abzubrechen"))
                    .build(), e -> {
                player.closeInventory();
                player.sendActionBar("§7Anfrage abgebrochen");
                player.playSound(player, Sound.BLOCK_TRIPWIRE_CLICK_ON, 1.0f, 1.0f);
            }));

            contents.set(1, 7, ClickableItem.of(new ItemBuilder(Material.LIME_STAINED_GLASS_PANE)
                    .setDisplayName(MessageBuilder.buildOld("%gᴄᴏɴғɪʀᴍ"))
                    .setLore(MessageBuilder.buildOld("§fKlicke um " + targetPlayerName + " eine Anfrage zu senden"))
                    .build(), e -> {
                tpaManger.sendTpaRequest(target, player);
                player.closeInventory();
                player.sendActionBar("§7Anfrage bestätigt");
                player.playSound(player, Sound.BLOCK_TRIPWIRE_CLICK_ON, 1.0f, 1.0f);
                player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
            }));
            return;
        }

        contents.set(1, 1, ClickableItem.of(new ItemBuilder(Material.RED_STAINED_GLASS_PANE)
                .setDisplayName(MessageBuilder.buildOld("%rᴄᴀɴᴄᴇʟ"))
                .setLore(MessageBuilder.buildOld("§fKlicke um die teleportation abzubrechen"))
                .build(), e -> {
            tpaManger.denyRequest(target, player);
            player.closeInventory();
            player.sendActionBar("§7Anfrage abgebrochen");
            player.playSound(player, Sound.BLOCK_TRIPWIRE_CLICK_ON, 1.0f, 1.0f);
        }));

        contents.set(1, 7, ClickableItem.of(new ItemBuilder(Material.LIME_STAINED_GLASS_PANE)
                .setDisplayName(MessageBuilder.buildOld("%gᴄᴏɴғɪʀᴍ"))
                .setLore(MessageBuilder.buildOld("§fKlicke um " + targetPlayerName + " zu dir zu teleportieren"))
                .build(), e -> {
            tpaManger.acceptRequest(target, player);
            player.closeInventory();
            player.sendActionBar("§7Anfrage akzeptiert");
            player.playSound(player, Sound.BLOCK_TRIPWIRE_CLICK_ON, 1.0f, 1.0f);
            player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
        }));
    }

    private static Material getLocationMaterial(Player player) {
        switch (player.getLocation().getWorld().getEnvironment()) {
            case NORMAL -> {
                return Material.GRASS_BLOCK;
            }
            case NETHER -> {
                return Material.NETHERRACK;
            }
            case THE_END -> {
                return Material.END_STONE;
            }
        }
        return Material.BEDROCK;
    }

    private static String getLocation(Player player) {
        switch (player.getLocation().getWorld().getEnvironment()) {
            case NORMAL -> {
                return "Overworld";
            }
            case NETHER -> {
                return "Nether";
            }
            case THE_END -> {
                return "End";
            }
        }
        return "Unknown";
    }

}
