package live.blackninja.smp.listener;

import live.blackninja.smp.Core;
import live.blackninja.smp.builder.MessageBuilder;
import live.blackninja.smp.manger.ElytraManger;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public record PlayerConnectionListener(Core core) implements Listener {

    private static int onlinePlayers = 0;

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        onlinePlayers++;

        event.setJoinMessage(MessageBuilder.buildOld(Core.PREFIX + " %b" + player.getDisplayName() + " §7hat den SMP betreten"));

        core.getSmpManger().initPlayer(player.getName());
        sendResourcepack(player);

        Bukkit.getWorld(player.getWorld().getName()).spawnParticle(Particle.FIREWORK, player.getLocation(), 5);
        sendWelcomeMessage(player);

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            onlinePlayer.setPlayerListHeader(" \n \n \n \n \n\uEfff");
            onlinePlayer.setPlayerListFooter(MessageBuilder.buildOld(" \n       §f" + onlinePlayers + " Players       \n§x§0§0§a§e§f§fsᴍᴘ.ʙʟᴀᴄᴋɴɪɴᴊᴀ.ʟɪᴠᴇ\n "));
        }

    }

    private void sendWelcomeMessage(Player player) {
        String smp = "§x§5§C§D§A§C§7ᴍ§x§5§B§D§7§C§8ɪ§x§5§A§D§5§C§9ɴ§x§5§9§D§2§C§Aᴇ§x§5§8§D§0§C§Bᴄ§x§5§7§C§D§C§Bʀ§x§5§6§C§A§C§Cᴀ§x§5§5§C§8§C§Dғ§x§5§4§C§5§C§Eᴛ §x§5§2§C§0§C§Fs§x§5§2§B§D§D§0ᴍ§x§5§1§B§A§D§1ᴘ §x§4§F§B§5§D§2s§x§4§E§B§2§D§3ᴇ§x§4§D§B§0§D§4ᴀ§x§4§C§A§D§D§5s§x§4§B§A§B§D§6ᴏ§x§4§A§A§8§D§6ɴ §x§4§8§A§3§D§85";
        player.sendMessage(MessageBuilder.buildOld("§8*----------------------------------------------------*"));
        player.sendMessage(MessageBuilder.buildOld(smp));
        player.sendMessage(MessageBuilder.buildOld(" "));
        player.sendMessage(MessageBuilder.buildOld("§fWillkommen zurück %b" + player.getName() + "§f!"));
        player.sendMessage(MessageBuilder.buildOld(" "));
        player.sendMessage(MessageBuilder.buildOld("§fAlle Informationen, exclusive der Serverregeln, findest du auf:"));
        player.sendMessage(MessageBuilder.build("<color:#14a9ff><click:open_url:'https://discord.gg/b3uAQaB2'>https://discord.blackninja.live</click></color>"));
        player.sendMessage(MessageBuilder.buildOld(" "));
        player.sendMessage(MessageBuilder.buildOld("§8*----------------------------------------------------*"));
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        onlinePlayers--;

        event.setQuitMessage(MessageBuilder.buildOld(Core.PREFIX + " %b" + player.getDisplayName() + " §7hat den SMP verlassen"));

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            onlinePlayer.setPlayerListHeader(" \n \n \n \n \n\uEfff");
            onlinePlayer.setPlayerListFooter(MessageBuilder.buildOld(" \n §f" + onlinePlayers + " Players \n§x§0§0§a§e§f§fsᴍᴘ.ʙʟᴀᴄᴋɴɪɴᴊᴀ.ʟɪᴠᴇ\n "));
        }
    }

    public void sendResourcepack(Player player) {
        if (!core.getSmpManger().getConfig().getConfig().contains("ResourcePack")) {
            return;
        }
        String url = core.getSmpManger().getConfig().getConfig().getString("ResourcePack.URL");
        String hash = core.getSmpManger().getConfig().getConfig().getString("ResourcePack.Hash");
        if (url == null || hash == null) {
            player.sendMessage(MessageBuilder.buildOld("§8| §9RP §8%> §cResourcepack URL or Hash is null!"));
            return;
        }
        player.setResourcePack(url, hash, true);
    }
}
