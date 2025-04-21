package live.blackninja.smp.cmd;

import live.blackninja.smp.Core;
import live.blackninja.smp.builder.MessageBuilder;
import live.blackninja.smp.manger.SMPManger;
import net.kyori.adventure.resource.ResourcePackInfo;
import net.kyori.adventure.resource.ResourcePackRequest;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.util.List;
import java.util.UUID;

public record ResourcePackCmd(Core core) implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) return false;

        Player player = (Player) sender;
        SMPManger smpManger = core.getSmpManger();

        if (!(player.hasPermission("smp.command.resourcepack"))) {
            player.sendMessage(Core.NO_PERMS);
            return false;
        }

        if (args.length == 0) {
            player.sendMessage(MessageBuilder.buildOld(Core.PREFIX + "§7Benutze %b/resourcepack [ load | setUrl | setHash ]"));
            return false;
        }

        switch (args[0]) {
            case "load": {
                String url = smpManger.getConfig().getConfig().getString("ResourcePack.URL");
                String hash = smpManger.getConfig().getConfig().getString("ResourcePack.Hash");

                if (url == null || hash == null) {
                    player.sendMessage(MessageBuilder.buildOld(Core.PREFIX + "§7Die %bConfig §7ist §cnicht §7vollständig!"));
                    return false;
                }

                for (Player players : Bukkit.getOnlinePlayers()) {
                    players.sendResourcePacks(ResourcePackRequest.resourcePackRequest().packs(ResourcePackInfo.resourcePackInfo(UUID.randomUUID(), URI.create(url), hash)).build());
                }
                player.sendMessage(MessageBuilder.buildOld(Core.PREFIX + "%gAlle Spieler §7erhalten nun das %bServer ResourcePack§7!"));
                break;
            }
            case "setUrl": {
                if (args.length != 2) {
                    player.sendMessage(MessageBuilder.buildOld(Core.PREFIX + "§7Benutze %b/resourcepack setUrl (Url)"));
                    return false;
                }
                String url = args[1];
                smpManger.getConfig().getConfig().set("ResourcePack.URL", url);
                smpManger.getConfig().save();
                player.sendMessage(MessageBuilder.buildOld(Core.PREFIX + "§7Die %bConfig §7wurde %gaktualisiert§7!"));
                break;
            }
            case "setHash": {
                if (args.length != 2) {
                    player.sendMessage(MessageBuilder.buildOld(Core.PREFIX + "§7Benutze %b/resourcepack setHash (Url)"));
                    return false;
                }
                String hash = args[1];
                smpManger.getConfig().getConfig().set("ResourcePack.Hash", hash);
                smpManger.getConfig().save();
                player.sendMessage(MessageBuilder.buildOld(Core.PREFIX + "§7Die %bConfig §7wurde %gaktualisiert§7!"));
                break;
            }
        }


        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return List.of("load", "setUrl", "setHash");
    }
}
