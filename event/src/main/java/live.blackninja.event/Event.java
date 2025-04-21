package live.blackninja.event;

import live.blackninja.event.cmd.ServerStateCmd;
import live.blackninja.event.cmd.UrlCmd;
import live.blackninja.smp.Core;
import live.blackninja.smp.listener.ServerPingListener;
import live.blackninja.smp.manger.addon.Addon;
import live.blackninja.smp.manger.addon.Addons;
import live.blackninja.smp.util.CommandUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class Event extends JavaPlugin implements Addon {

    public static final String PREFIX = "§8| §x§0§8§4§C§F§BS§x§1§A§4§F§F§9M§x§2§C§5§2§F§6P§x§3§E§5§5§F§4-§x§5§0§5§8§F§1E§x§6§2§5§A§E§Fv§x§7§4§5§D§E§Ce§x§8§6§6§0§E§An§x§9§8§6§3§E§7t §8%> ";
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
        pluginManager.registerEvents(new ServerPingListener(core), this);
    }

    private void registerCommands() {
        new CommandUtils("switch-event-server-state", new ServerStateCmd(core), new ServerStateCmd(core), core);
        new CommandUtils("get-server-urls", new UrlCmd(core), core);
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