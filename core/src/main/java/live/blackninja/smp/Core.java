package live.blackninja.smp;

import fr.minuskube.inv.InventoryManager;
import live.blackninja.smp.builder.MessageBuilder;
import live.blackninja.smp.cmd.*;
import live.blackninja.smp.cmd.shortcuts.DelHomeCmd;
import live.blackninja.smp.cmd.shortcuts.SetHomeCmd;
import live.blackninja.smp.cmd.shortcuts.TpaAcceptCmd;
import live.blackninja.smp.cmd.shortcuts.TpaDenyCmd;
import live.blackninja.smp.cmd.staff.*;
import live.blackninja.smp.listener.*;
import live.blackninja.smp.manger.SMPManger;
import live.blackninja.smp.manger.StaffManger;
import live.blackninja.smp.manger.StatsManger;
import live.blackninja.smp.manger.addon.AddonManger;
import live.blackninja.smp.util.CommandUtils;
import live.blackninja.smp.util.ErrorWatcher;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.HappyGhast;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class Core extends JavaPlugin {

    public static final String PREFIX = "§8[%b⚡§8] ";
    public static final String NO_PERMS = MessageBuilder.buildOld(PREFIX + "%r✘ §7| §7Dafür hast du %rkeine §7Rechte!");

    private AddonManger addonManger;
    private SMPManger smpManger;
    private StaffManger staffManger;
    private InventoryManager inventoryManager;
    private StatsManger statsManger;

    @Override
    public void onEnable() {

        addonManger = new AddonManger(this);
        smpManger = new SMPManger(this);
        staffManger = new StaffManger(this);
        inventoryManager = new InventoryManager(this);
        statsManger = new StatsManger(this);

        inventoryManager.init();

        smpManger.getElytraManger().load();

        registerCommands();
        registerListeners(getServer().getPluginManager());

        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            String msg = throwable.getClass().getSimpleName() + ": " + throwable.getMessage();
            ErrorWatcher.broadcastError(msg);
        });
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
        new CommandUtils("ec", new EcCmd(this), this);
        new CommandUtils("invsee", new InvseeCmd(this), this);
        new CommandUtils("timer", new TimerCmd(this), this);
        new CommandUtils("restart", new RestartCmd(this), this);
        new CommandUtils("errorwatcher", new ErrorWatcherCmd(this), this);
        new CommandUtils("scale", new ScaleCmd(this), this);
        new CommandUtils("info", new InfoCmd(this), this);
        new CommandUtils("ping", new PingCmd(this), this);
        new CommandUtils("msg", new MsgCmd(this), this);
        new CommandUtils("stats", new StatsCmd(this), this);
        new CommandUtils("leaderboard", new LeaderboardCmd(this), this);

        //Shotcuts
        new CommandUtils("sethome", new SetHomeCmd(this), this);
        new CommandUtils("delhome", new DelHomeCmd(this), new DelHomeCmd(this), this);
        new CommandUtils("tpaaccept", new TpaAcceptCmd(this), this);
        new CommandUtils("tpadeny", new TpaDenyCmd(this), this);
    }

    private void registerListeners(PluginManager pluginManager) {
        pluginManager.registerEvents(new PlayerConnectionListener(this), this);
        pluginManager.registerEvents(new PlayerInteractListener(this), this);
        pluginManager.registerEvents(new PlayerChatListener(this), this);
        pluginManager.registerEvents(new ServerPingListener(this), this);
        pluginManager.registerEvents(new ResourcePackListener(this), this);
        pluginManager.registerEvents(new TimeOutListener(this), this);
        pluginManager.registerEvents(new InvSeeListener(this), this);
        pluginManager.registerEvents(new ElytraListener(this), this);
        pluginManager.registerEvents(new StaffListener(this, staffManger), this);
        pluginManager.registerEvents(new PlayerDeathListener(this), this);
        pluginManager.registerEvents(new StatsListener(this, statsManger), this);
        pluginManager.registerEvents(new DragonEggListener(this), this);
    }

    @Override
    public void onDisable() {
        //smpManger.getElytraManger().save();
        smpManger.getDelayedOpeningManger().save();

        resetHappyGhastFlySpeed();
    }

    public void resetHappyGhastFlySpeed() {
        for (World world : this.getServer().getWorlds()) {
            for (HappyGhast ghast : world.getEntitiesByClass(HappyGhast.class)) {
                ghast.getAttribute(Attribute.FLYING_SPEED).setBaseValue(0.05);
            }
        }
    }

    public StatsManger getStatsManger() {
        return statsManger;
    }

    public AddonManger getAddonManger() {
        return addonManger;
    }

    public SMPManger getSmpManger() {
        return smpManger;
    }

    public StaffManger getStaffManager() {
        return staffManger;
    }

    public InventoryManager getInventoryManager() {
        return inventoryManager;
    }
}
