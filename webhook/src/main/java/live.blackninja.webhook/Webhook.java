package live.blackninja.webhook;

import fr.minuskube.inv.InventoryManager;
import live.blackninja.smp.Core;
import live.blackninja.smp.manger.addon.Addon;
import live.blackninja.smp.manger.addon.Addons;
import live.blackninja.smp.util.CommandUtils;
import live.blackninja.webhook.bot.Bot;
import live.blackninja.webhook.listener.ChatDataManger;
import live.blackninja.webhook.listener.PlayerChatListener;
import live.blackninja.webhook.manger.ConfigManger;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import javax.security.auth.login.LoginException;

public final class Webhook extends JavaPlugin implements Addon {

    private Core core;
    private Bot bot;
    private ConfigManger configManger;
    private ChatDataManger chatDataManger;

    public static final String PREFIX = "<dark_gray>| <gradient:#4498DB:#06F5FF>SMPDC <dark_gray>%> ";

    @Override
    public void onEnable() {
        core = (Core) getServer().getPluginManager().getPlugin("SMP");

        if (core == null) {
            getLogger().severe("Core-Plugin nicht gefunden! Deaktiviere SMP-Webook System...");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        core.getAddonManger().registerAddon(Addons.WEBHOOK, this);

        bot = new Bot(this);
        configManger = new ConfigManger(this);
        chatDataManger = new ChatDataManger();

        saveDefaultConfig();

        configManger.setChannelId("1324797050709344330");
        configManger.save();

        try {
            bot.start();
        } catch (LoginException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        registerCommands();
        registerListener(Bukkit.getPluginManager());
    }

    private void registerListener(PluginManager pluginManager) {
        pluginManager.registerEvents(new PlayerChatListener(this), this);
    }

    private void registerCommands() {
    }

    @Override
    public void onDisable() {
        super.onDisable();

        bot.stop();
        configManger.save();
    }

    @Override
    public void useComponent() {
        // Implementiere hier die Logik für die Verwendung des Addons
        // Beispiel: core.getAddonManger().useComponent(this);
    }

    public ChatDataManger getChatDataManger() {
        return chatDataManger;
    }

    public ConfigManger getConfigManger() {
        return configManger;
    }

    public Bot getBot() {
        return bot;
    }
}