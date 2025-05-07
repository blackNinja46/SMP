package live.blackninja.event;

import live.blackninja.event.cmd.ServerStateCmd;
import live.blackninja.event.cmd.UrlCmd;
import live.blackninja.smp.Core;
import live.blackninja.event.listener.PlayerLoginListener;
import live.blackninja.smp.manger.addon.Addon;
import live.blackninja.smp.manger.addon.Addons;
import live.blackninja.smp.util.CommandUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class Event extends JavaPlugin implements Addon {

    public static final String PREFIX = "<dark_gray>| SMPEvent <dark_gray>%> ";
    private Core core;

    @Override
    public void onEnable() {
        core = (Core) getServer().getPluginManager().getPlugin("SMP");

        if (core == null) {
            getLogger().severe("Core-Plugin nicht gefunden! Deaktiviere SMP-Event System...");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        core.getAddonManger().registerAddon(Addons.EVENT, this);

        registerCommands();
        registerListener(Bukkit.getPluginManager());
    }

    private void registerListener(PluginManager pluginManager) {
        pluginManager.registerEvents(new PlayerLoginListener(core), this);
    }

    private void registerCommands() {
        new CommandUtils("switch-event-server-state", new ServerStateCmd(core), new ServerStateCmd(core), this);
        new CommandUtils("get-server-urls", new UrlCmd(core), this);
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @Override
    public void useComponent() {
        // Implementiere hier die Logik für die Verwendung des Addons
        // Beispiel: core.getAddonManger().useComponent(this);
    }
}