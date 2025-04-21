package live.blackninja.smp.cmd;

import fr.minuskube.inv.SmartInventory;
import live.blackninja.smp.Core;
import live.blackninja.smp.builder.MessageBuilder;
import live.blackninja.smp.manger.InvSeeHolder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

public record InvseeCmd(Core core) implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) return true;

        Player player = (Player) sender;

        if (!player.hasPermission("ninjasmp.cmd.invsee")) {
            player.sendMessage(Core.NO_PERMS);
            return true;
        }

        if (args.length == 0) {
            player.sendMessage(MessageBuilder.buildOld(Core.PREFIX + "§7Benutze %b/invsee [Spieler]"));
            return true;
        }

        Player viewer = (Player) sender;
        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null || !target.isOnline()) {
            viewer.sendMessage("§cSpieler nicht gefunden.");
            return true;
        }

        Inventory inv = Bukkit.createInventory(new InvSeeHolder(target.getUniqueId()), 54, MessageBuilder.buildOld("§8| §7Inventar von %b" + target.getName()));

        ItemStack[] contents = target.getInventory().getContents();
        for (int i = 0; i < contents.length; i++) {
            inv.setItem(i, contents[i]);
        }

        ItemStack glass = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta meta = glass.getItemMeta();
        meta.setDisplayName(" ");
        glass.setItemMeta(meta);
        for (int i = 36; i < 45; i++) {
            inv.setItem(i, glass);
        }

        inv.setItem(45, target.getInventory().getHelmet());
        inv.setItem(46, target.getInventory().getChestplate());
        inv.setItem(47, target.getInventory().getLeggings());
        inv.setItem(48, target.getInventory().getBoots());
        inv.setItem(49, target.getInventory().getItemInOffHand());

        viewer.openInventory(inv);
        return true;
    }
}
