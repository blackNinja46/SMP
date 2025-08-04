package live.blackninja.smp.manger;

import io.papermc.paper.event.player.AsyncChatEvent;
import live.blackninja.smp.Core;
import live.blackninja.smp.builder.ItemBuilder;
import live.blackninja.smp.builder.MessageBuilder;
import live.blackninja.smp.cmd.staff.*;
import live.blackninja.smp.config.StaffConfig;
import live.blackninja.smp.config.StaffTypes;
import live.blackninja.smp.util.CommandUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Random;
import java.util.UUID;

public class StaffManger {

    public Core core;

    public static final String STAFF_PREFIX = "§8[%r⚡§8] ";

    private StaffConfig config;

    public StaffManger(Core core) {
        this.core = core;

        config = new StaffConfig("staff");

        registerCommands();
    }

    public void registerCommands() {
        new CommandUtils("fly", new FlyCmd(core), core);
        new CommandUtils("freeze", new FreezeCmd(core), core);
        new CommandUtils("heal", new HealCmd(core), core);
        new CommandUtils("randomtp", new RandomTpCmd(core), core);
        new CommandUtils("staffchat", new StaffChatCmd(core), core);
        new CommandUtils("staff", new StaffCmd(core), core);
        new CommandUtils("vanish", new VanishCmd(core), core);
    }

    public void toggleVanish(Player player) {
        if (!config.containsList(StaffTypes.VANISH, player.getUniqueId())) {
            config.addList(StaffTypes.VANISH, player.getUniqueId());
            for (Player players : Bukkit.getOnlinePlayers()) {
                if (players != player) {
                    players.hidePlayer(core, player);
                }
            }
            player.sendMessage(MessageBuilder.buildOld(STAFF_PREFIX + "§7Du bist jetzt §cUnsichtbar§7."));
            return;
        }
        config.removeList(StaffTypes.VANISH, player.getUniqueId());
        for (Player players : Bukkit.getOnlinePlayers()) {
            if (players != player) {
                players.showPlayer(core, player);
            }
        }
        player.sendMessage(MessageBuilder.buildOld(STAFF_PREFIX + "§7Du bist jetzt §aSichtbar§7."));
    }

    public void toggleFly(Player player) {
        if (!config.containsList(StaffTypes.FLY, player.getUniqueId())) {
            config.addList(StaffTypes.FLY, player.getUniqueId());
            player.setAllowFlight(true);
            player.sendMessage(MessageBuilder.buildOld(STAFF_PREFIX + "§7Du kannst jetzt §afliegen§7."));
            return;
        }
        config.removeList(StaffTypes.FLY, player.getUniqueId());
        player.setAllowFlight(false);
        player.sendMessage(MessageBuilder.buildOld(STAFF_PREFIX + "§7Du kannst jetzt §anicht mehr fliegen§7."));
    }

    public void toggleFreeze(Player target) {
        if (!config.containsList(StaffTypes.FREEZE, target.getUniqueId())) {
            config.addList(StaffTypes.FREEZE, target.getUniqueId());
            target.sendMessage(
                    "\n" +
                    "§c§lDu bist aktuell Eingefroren!" +
                    "\n" +
                    "\n§7→ Du kannst dich nicht bewegen oder interagieren." +
                    "\n§7→ Moderatoren kontrollieren deine Aktionen." +
                    "\n§7→ Joine unserem Discord für Support: §6https://discord.blackninja.live\n"
            );
            return;
        }
        config.removeList(StaffTypes.FREEZE, target.getUniqueId());
        target.sendMessage(MessageBuilder.buildOld(STAFF_PREFIX + "§a§lDu bist nun wieder Frei!"));
    }

    public void toggleStaffChat(Player player) {
        if (!config.containsList(StaffTypes.STAFF_CHAT, player.getUniqueId())) {
            config.addList(StaffTypes.STAFF_CHAT, player.getUniqueId());
            player.sendMessage(MessageBuilder.buildOld(STAFF_PREFIX + "§7Du bist jetzt im §cStaff Chat§7."));
            return;
        }
        config.removeList(StaffTypes.STAFF_CHAT, player.getUniqueId());
        player.sendMessage(MessageBuilder.buildOld(STAFF_PREFIX + "§7Du bist jetzt nicht mehr im §aStaff Chat§7."));
    }

    public void toggleStaffMode(Player player) {
        if (!config.containsList(StaffTypes.STAFF_MODE, player.getUniqueId())) {
            config.addList(StaffTypes.STAFF_MODE, player.getUniqueId());
            joinStaffMode(player);
            player.sendMessage(MessageBuilder.buildOld(STAFF_PREFIX + "§7Du bist jetzt im §cStaff Modus§7."));
            return;
        }
        config.removeList(StaffTypes.STAFF_MODE, player.getUniqueId());
        leaveStaffMode(player);
        player.sendMessage(MessageBuilder.buildOld(STAFF_PREFIX + "§7Du bist jetzt nicht mehr im §aStaff Modus§7."));
    }

    public void getOnlineStaffMembers(Player player) {
        player.sendMessage(MessageBuilder.buildOld(STAFF_PREFIX + "§7Online §cStaff Mitglieder§7:"));
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (onlinePlayer.hasPermission("ninjasmp.staff.bypass")) {
                player.sendMessage(MessageBuilder.buildOld("§7- §b" + onlinePlayer.getName()));
            }
        }
    }

    public void getFrozenPlayers(Player player) {
        player.sendMessage(MessageBuilder.buildOld(STAFF_PREFIX + "§7Aktuell Eingefrorene Spieler:"));
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (config.containsList(StaffTypes.FREEZE, onlinePlayer.getUniqueId())) {
                player.sendMessage(MessageBuilder.buildOld("§7- §b" + onlinePlayer.getName()));
            }
        }
    }

    public void onJoin(Player player) {
        if (config.containsList(StaffTypes.VANISH, player.getUniqueId())) {
            for (Player players : Bukkit.getOnlinePlayers()) {
                if (players != player) {
                    players.hidePlayer(core, player);
                }
            }
            return;
        }
        if (config.containsList(StaffTypes.FLY, player.getUniqueId())) {
            player.setAllowFlight(true);
            return;
        }
        if (config.containsList(StaffTypes.FREEZE, player.getUniqueId())) {
            player.sendMessage(
                    "\n" +
                    "§c§lDu bist aktuell Eingefroren!" +
                    "\n" +
                    "\n§7→ Du kannst dich nicht bewegen oder interagieren." +
                    "\n§7→ Moderatoren kontrollieren deine Aktionen." +
                    "\n§7→ Joine unserem Discord für Support: §6https://discord.blackninja.live\n"
            );
            return;
        }
        if (config.containsList(StaffTypes.STAFF_MODE, player.getUniqueId())) {
            joinStaffMode(player);
            return;
        }
        if (config.containsList(StaffTypes.STAFF_CHAT, player.getUniqueId())) {
            player.sendMessage(MessageBuilder.buildOld(STAFF_PREFIX + "§7Du bist noch im §cStaff Chat§7."));
        }
    }

    public void saveInventory(Player player) {
        UUID uuid = player.getUniqueId();
        config.getConfig().set("StaffInventory." + uuid + ".Content", player.getInventory().getContents());
        config.getConfig().set("StaffInventory." + uuid + ".ArmorContent", player.getInventory().getArmorContents());
        config.getConfig().set("StaffInventory." + uuid + ".ExtraContent", player.getInventory().getExtraContents());
        config.save();
    }

    public void loadInventory(Player player) {
        UUID uuid = player.getUniqueId();
        if (config.getConfig().contains("StaffInventory." + uuid)) {
            ItemStack[] contents = config.getConfig().getObject("StaffInventory." + uuid + ".Content", ItemStack[].class);
            ItemStack[] armor = config.getConfig().getObject("StaffInventory." + uuid + ".ArmorContent", ItemStack[].class);
            ItemStack[] extra = config.getConfig().getObject("StaffInventory." + uuid + ".ExtraContent", ItemStack[].class);

            if (contents != null) player.getInventory().setContents(contents);
            if (armor != null) player.getInventory().setArmorContents(armor);
            if (extra != null) player.getInventory().setExtraContents(extra);
        }
    }


    public void joinStaffMode(Player player) {
        saveInventory(player);
        player.getInventory().clear();

        if (config.containsList(StaffTypes.VANISH, player.getUniqueId())) {
            player.getInventory().setItem(0, new ItemBuilder(Material.LIME_DYE)
                    .setDisplayName("§cVanish")
                    .setLore("§7Klicke um den §cVanish §7Modus zu toggeln")
                    .build()
            );
        } else {
            player.getInventory().setItem(0, new ItemBuilder(Material.GRAY_DYE)
                    .setDisplayName("§cVanish")
                    .setLore("§7Klicke um den §cVanish §7Modus zu toggeln")
                    .build()
            );
        }

        player.getInventory().setItem(1, new ItemBuilder(Material.ICE)
                .setDisplayName("§bFreeze")
                .setLore("§7Klicke einen Spieler an, um ihn einzufrieren")
                .build()
        );

        player.getInventory().setItem(4, new ItemBuilder(Material.ORANGE_CARPET)
                .setDisplayName("§bBetter View")
                .setLore("§7Rechtsklick auf einen Spieler, um ihn einzufrieren")
                .setLore("§7Linksklick um alle Eingefrorenen Spieler zu sehen")
                .build()
        );

        player.getInventory().setItem(7, new ItemBuilder(Material.PLAYER_HEAD)
                .setDisplayName("§9Ramdom Teleport")
                .setLore("§7Klicke um dich zu einem zufälligen Spieler zu teleportieren")
                .build()
        );

        player.getInventory().setItem(8, new ItemBuilder(Material.CLOCK)
                .setDisplayName("§eStaff List")
                .setLore("§7Klicke um die §cOnline Staff Mitglieder §7anzuzeigen")
                .build()
        );
    }

    public void leaveStaffMode(Player player) {
        player.getInventory().clear();
        loadInventory(player);
    }


    public StaffConfig getConfig() {
        return config;
    }
}
