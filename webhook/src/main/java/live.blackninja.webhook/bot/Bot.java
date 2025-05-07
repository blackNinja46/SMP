package live.blackninja.webhook.bot;

import live.blackninja.webhook.Webhook;
import live.blackninja.webhook.bot.cmd.EmbedCmd;
import live.blackninja.webhook.bot.cmd.PlayersCmd;
import live.blackninja.webhook.bot.cmd.SyncChatCmd;
import live.blackninja.webhook.bot.listener.MessageReceivedListener;
import live.blackninja.webhook.bot.manager.SyncChatManger;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;

import javax.security.auth.login.LoginException;

public class Bot {

    private Webhook instance;

    private String token;
    private static JDA jda;
    private SyncChatManger syncChatManger;

    public Bot(Webhook instance) {
        this.instance = instance;
    }

    public void start() throws LoginException, InterruptedException {
        token = instance.getConfigManger().getToken();

        if (token == null) {
            instance.getServer().getConsoleSender().sendMessage("§8[§9SMPDiscordWebhook§8] §fToken not found! Check the config.yml");
            return;
        }

        jda = JDABuilder.createDefault(token)
                .setStatus(OnlineStatus.ONLINE)
                .setActivity(Activity.playing("SMP v5 Beta"))
                .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                .build()
                .awaitReady();

        if (jda == null) {
            instance.getServer().getConsoleSender().sendMessage("§8[§9SMPDiscordWebhook§8] §fBot not found! Check the config.yml");
            return;
        }

        syncChatManger = new SyncChatManger(instance);

        initCommands();
        initEvents();

        instance.getServer().getConsoleSender().sendMessage("§8[§9SMPDiscordWebhook§8] §fBot started!");
    }

    public void initCommands() {
        jda.updateCommands().addCommands(
                Commands.slash("syncchat", "Synchronisiere den Chat! (Admin Only)")
                        .addOption(OptionType.BOOLEAN, "syncchatenabled", "Aktiviert oder deaktiviert die Chat-Synchronisierung", true),
                Commands.slash("players", "Akutelle Spieler, die sich auf dem SMP Server befinden."),
                Commands.slash("embed-builder", "Sendet eine Embed Nachricht (Admin Only)")
                        .addOption(OptionType.INTEGER, "id", "Die ID der Nachricht", true)
        ).queue();
    }

    public void initEvents() {
        jda.addEventListener(new SyncChatCmd(instance));
        jda.addEventListener(new PlayersCmd(instance));
        jda.addEventListener(new EmbedCmd(instance));
        jda.addEventListener(new MessageReceivedListener(instance));
    }

    public void stop() {
        instance.getServer().getConsoleSender().sendMessage("§8[§9SMPDiscordWebhook§8] §fBot stopped!");
        jda.shutdown();
    }

    public static JDA getJda() {
        return jda;
    }

    public SyncChatManger getSyncChatManger() {
        return syncChatManger;
    }
}
