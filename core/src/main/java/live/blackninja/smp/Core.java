package live.blackninja.smp;

import live.blackninja.smp.builder.MessageBuilder;
import live.blackninja.smp.cmd.*;
import live.blackninja.smp.listener.*;
import live.blackninja.smp.manger.SMPManger;
import live.blackninja.smp.manger.addon.AddonManger;
import live.blackninja.smp.util.CommandUtils;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class Core extends JavaPlugin {

    public static final String PREFIX = "§8| §f\uEfe1 §8%> ";
    public static final String NO_PERMS = MessageBuilder.buildOld(PREFIX + "%r%x §7| §7Dafür hast du %rkeine §7Rechte!");

    private AddonManger addonManger;
    private SMPManger smpManger;

    @Override
    public void onEnable() {

        addonManger = new AddonManger(this);
        smpManger = new SMPManger(this);

        smpManger.getElytraManger().load();

        registerCommands();
        registerListeners(getServer().getPluginManager());
    }

    private void registerCommands() {
        new CommandUtils("home", new HomeCmd(this), new HomeCmd(this), this);
        new CommandUtils("tpa", new TpaCmd(this), new TpaCmd(this), this);
        new CommandUtils("smp", new SMPCmd(this), new SMPCmd(this), this);
        new CommandUtils("spawn", new SpawnCmd(this), this);
        new CommandUtils("status", new StatusCmd(this), new StatusCmd(this), this);
        new CommandUtils("voteban", new VoteBanCmd(this), new VoteBanCmd(this), this);
        new CommandUtils("hubschrauber", new HubschrauberCmd(this), this);
        new CommandUtils("resourcepack", new ResourcePackCmd(this), new ResourcePackCmd(this), this);
        new CommandUtils("timeout", new TimeOutCmd(this), new TimeOutCmd(this), this);
        new CommandUtils("untimeout", new UnTimeOutCmd(this), new UnTimeOutCmd(this), this);
        new CommandUtils("ecsee", new EcseeCmd(this), this);
        new CommandUtils("invsee", new InvseeCmd(this), this);
    }

    private void registerListeners(PluginManager pluginManager) {
        pluginManager.registerEvents(new PlayerConnectionListener(this), this);
        pluginManager.registerEvents(new PlayerInteractListener(this), this);
        pluginManager.registerEvents(new PlayerChatListener(this), this);
        pluginManager.registerEvents(new ServerPingListener(this), this);
        pluginManager.registerEvents(new ResourcePackListener(this), this);
        pluginManager.registerEvents(new TimeOutListener(this), this);
        pluginManager.registerEvents(new InvSeeListener(this), this);
    }

    @Override
    public void onDisable() {
        smpManger.getElytraManger().save();
    }

    public AddonManger getAddonManger() {
        return addonManger;
    }

    public SMPManger getSmpManger() {
        return smpManger;
    }
}
