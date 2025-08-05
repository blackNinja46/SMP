package live.blackninja.smp.gui;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import live.blackninja.smp.Core;
import live.blackninja.smp.builder.ItemBuilder;
import live.blackninja.smp.builder.MessageBuilder;
import live.blackninja.smp.manger.HomeManger;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.nio.charset.CoderResult;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class HomesGUI {

    public static void open(Player player, Core core) {
        SmartInventory.builder()
                .manager(core.getInventoryManager())
                .title("§7ʜᴏᴍᴇѕ")
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
        HomeManger homeManager = core.getSmpManger().getHomeManger();
        String playerName = player.getName();

        Set<String> existingHomes = homeManager.getHomes(playerName);
        int maxHomes = homeManager.getMaxHomes();

        // Liste mit maxHomes leeren Feldern
        List<String> homeSlots = new ArrayList<>(Collections.nCopies(maxHomes, null));

        // Bestehende Homes auf Positionen zuweisen
        for (String homeName : existingHomes) {
            // Versuche eine Position anhand des Namens zu finden: z. B. "Home-1"
            int slotIndex = -1;

            if (homeName.toLowerCase().startsWith("home-")) {
                try {
                    slotIndex = Integer.parseInt(homeName.substring(5)) - 1;
                    if (slotIndex < 0 || slotIndex >= maxHomes) slotIndex = -1;
                } catch (NumberFormatException ignored) {}
            }

            // Falls kein Slot über den Namen ableitbar, suche freie Position
            if (slotIndex == -1) {
                for (int i = 0; i < maxHomes; i++) {
                    if (homeSlots.get(i) == null) {
                        slotIndex = i;
                        break;
                    }
                }
            }

            if (slotIndex != -1) {
                homeSlots.set(slotIndex, homeName);
            }
        }

        // GUI-Befüllung anhand fixer Slots
        for (int i = 0; i < maxHomes && i < 7; i++) {
            int col = i + 1; // GUI-Spalte, 1 frei lassen

            String homeName = homeSlots.get(i);
            if (homeName != null) {
                // Bestehendes Home
                ItemStack tpItem = new ItemBuilder(Material.LIGHT_BLUE_BED)
                        .setDisplayName(MessageBuilder.buildOld("%b" + homeName.replace("&", "§")))
                        .addLoreLine(MessageBuilder.buildOld("§fKlicke, um zu diesem Home %bteleportiert §fzu werden"))
                        .build();

                contents.set(1, col, ClickableItem.of(tpItem, event -> {
                    Location location = homeManager.getHome(player.getName(), homeName);
                    core.getSmpManger().teleport(player, location);
                    player.sendMessage(MessageBuilder.buildOld(Core.PREFIX + "§7§oDu wirst zu deinem Home teleportiert..."));
                    player.closeInventory();
                    player.playSound(player, Sound.BLOCK_TRIPWIRE_CLICK_ON, 1.0f, 1.0f);
                }));

                ItemStack delItem = new ItemBuilder(Material.BLUE_DYE)
                        .setDisplayName(MessageBuilder.buildOld("%b" + homeName.replace("&", "§")))
                        .addLoreLine(MessageBuilder.buildOld("§fKlicke, um dieses Home zu %rlöschen"))
                        .build();

                contents.set(2, col, ClickableItem.of(delItem, event -> {
                    ConfirmDeleteHomeGUI.open(player, homeName, core);
                    player.playSound(player, Sound.BLOCK_TRIPWIRE_CLICK_ON, 1.0f, 1.0f);
                }));

            } else {
                // Leerer Slot – Home erstellen
                String newHomeName = "Home-" + (i + 1);

                ItemStack emptyTpItem = new ItemBuilder(Material.LIGHT_GRAY_BED)
                        .setDisplayName("§7ɴᴏ ʜᴏᴍᴇ ѕᴇᴛ")
                        .addLoreLine("§fKlicke, um dieses Home zu erstellen")
                        .build();

                contents.set(1, col, ClickableItem.of(emptyTpItem, event -> {
                    homeManager.setHome(playerName, newHomeName, player.getLocation());
                    player.sendActionBar("§7Home erstellt");
                    open(player, core);
                    player.playSound(player, Sound.BLOCK_TRIPWIRE_CLICK_ON, 1.0f, 1.0f);
                }));

                ItemStack createItem = new ItemBuilder(Material.GRAY_DYE)
                        .setDisplayName("§7ɴᴏ ʜᴏᴍᴇ ѕᴇᴛ")
                        .addLoreLine("§fKlicke, um dieses Home zu erstellen")
                        .build();

                contents.set(2, col, ClickableItem.of(createItem, event -> {
                    homeManager.setHome(playerName, newHomeName, player.getLocation());
                    player.sendActionBar("§7Home erstellt");
                    open(player, core);
                    player.playSound(player, Sound.BLOCK_TRIPWIRE_CLICK_ON, 1.0f, 1.0f);
                }));
            }
        }
    }

}


