package live.blackninja.economy;

import fr.minuskube.inv.InventoryManager;
import live.blackninja.economy.cmd.AuctionCmd;
import live.blackninja.economy.listener.AuctionListener;
import live.blackninja.economy.manger.AuctionManager;
import live.blackninja.smp.Core;
import live.blackninja.smp.manger.addon.Addon;
import live.blackninja.smp.manger.addon.Addons;
import live.blackninja.smp.util.CommandUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class Economy extends JavaPlugin implements Addon {

    private Core core;
    private AuctionManager auctionManager;
    private InventoryManager inventoryManager;

    @Override
    public void onEnable() {
        core = (Core) getServer().getPluginManager().getPlugin("SMP");

        if (core == null) {
            getLogger().severe("Core-Plugin nicht gefunden! Deaktiviere SMP-Economy System...");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        core.getAddonManger().registerAddon(Addons.ECONOMY, this);

        auctionManager = new AuctionManager(this);
        auctionManager.loadAuctions();
        auctionManager.startAuctionChecker();
        auctionManager.loadLogs();

        inventoryManager = new InventoryManager(this);
        inventoryManager.init();

        saveDefaultConfig();

        registerCommands();
        registerListener(Bukkit.getPluginManager());
    }

    private void registerListener(PluginManager pluginManager) {
        pluginManager.registerEvents(new AuctionListener(this), this);
    }

    private void registerCommands() {
        new CommandUtils("auctionhouse", new AuctionCmd(this), new AuctionCmd(this), this);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        auctionManager.saveAuctions();
    }

    @Override
    public void useComponent() {
        // Implementiere hier die Logik für die Verwendung des Addons
        // Beispiel: core.getAddonManger().useComponent(this);
    }

    public AuctionManager getAuctionManager() {
        return auctionManager;
    }

    public InventoryManager getInventoryManager() {
        return inventoryManager;
    }
}