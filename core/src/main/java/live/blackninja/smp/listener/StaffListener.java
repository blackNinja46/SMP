package live.blackninja.smp.listener;

import io.papermc.paper.event.player.AsyncChatEvent;
import live.blackninja.smp.Core;
import live.blackninja.smp.builder.MessageBuilder;
import live.blackninja.smp.config.StaffTypes;
import live.blackninja.smp.manger.StaffManger;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public record StaffListener(Core core, StaffManger staffManger) implements Listener {



    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        staffManger.onJoin(player);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (staffManger.getConfig().containsList(StaffTypes.FREEZE, player.getUniqueId())) {
            event.setCancelled(true);
        }

        if (staffManger.getConfig().containsList(StaffTypes.STAFF_MODE, player.getUniqueId())) {
            if (event.getItem() != null && event.getItem().getType() == Material.GRAY_DYE || event.getItem() != null && event.getItem().getType() == Material.LIME_DYE) {
                staffManger.toggleVanish(player);
                if (!staffManger.getConfig().containsList(StaffTypes.VANISH, player.getUniqueId())) {
                    event.getItem().setType(Material.GRAY_DYE);
                } else {
                    event.getItem().setType(Material.LIME_DYE);
                }
                return;
            }
            if (event.getItem() != null && event.getItem().getType() == Material.ICE) {
                event.setCancelled(true);
                if (event.getAction().isLeftClick()) {
                    staffManger.getFrozenPlayers(player);
                    return;
                }
                return;
            }
            if (event.getItem() != null && event.getItem().getType() == Material.ORANGE_CARPET) {
                event.setCancelled(true);
                staffManger.toggleFly(player);
                return;
            }
            if (event.getItem() != null && event.getItem().getType() == Material.PLAYER_HEAD) {
                Random random = new Random();
                List<Player> onlinePlayers = new ArrayList<>(Bukkit.getOnlinePlayers());

                onlinePlayers.remove(player);

                if (onlinePlayers.isEmpty()) {
                    player.sendMessage("§cEs sind keine anderen Spieler online.");
                    return;
                }

                Player target = onlinePlayers.get(random.nextInt(onlinePlayers.size()));

                player.teleport(target.getLocation());
                player.sendMessage(MessageBuilder.buildOld(StaffManger.STAFF_PREFIX + "§7Du wurdest zu §e" + target.getName() + " §7teleportiert."));
                return;
            }
            if (event.getItem() != null && event.getItem().getType() == Material.CLOCK) {
                staffManger.getOnlineStaffMembers(player);
                return;
            }
        }
    }


    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();

        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }

        if (event.getRightClicked() instanceof Player target) {
            if (staffManger.getConfig().containsList(StaffTypes.STAFF_MODE, player.getUniqueId())) {
                if (player.getInventory().getItemInMainHand().getType() == Material.ICE) {
                    staffManger.toggleFreeze(target);
                    event.setCancelled(true);
                    if (!core.getStaffManager().getConfig().containsList(StaffTypes.FREEZE, target.getUniqueId())) {
                        player.sendMessage(MessageBuilder.buildOld(StaffManger.STAFF_PREFIX + "§7Der Spieler %b" + target.getName() + " §7ist nun wieder frei."));
                        return;
                    }
                    player.sendMessage(MessageBuilder.buildOld(StaffManger.STAFF_PREFIX + "§7Der Spieler %b" + target.getName() + " §7ist nun eingefroren."));
                    return;
                }
            }
        }
    }


    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (staffManger.getConfig().containsList(StaffTypes.FREEZE, player.getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        if (staffManger.getConfig().containsList(StaffTypes.STAFF_CHAT, player.getUniqueId())) {
            event.setCancelled(true);
            String message = event.getMessage();
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if (onlinePlayer.hasPermission("ninjasmp.staff.bypass")) {
                    onlinePlayer.sendMessage(MessageBuilder.buildOld("§7[§cStaff-Chat§7] §d" + player.getName() + "§8: §f" + message));
                }
            }
        }
    }

}
