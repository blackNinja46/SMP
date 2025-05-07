package live.blackninja.webhook.manger;

import live.blackninja.webhook.Webhook;

public class ConfigManger {

    private String token;
    private String guildId;
    private String channelId;
    private boolean enabled;

    private Webhook instance;



    public ConfigManger(Webhook instance) {
        this.instance = instance;

        load();
    }

    public void load() {
        token = instance.getConfig().getString("Bot.Token");
        guildId = instance.getConfig().getString("Bot.GuildID");
        channelId = instance.getConfig().getString("Bot.SyncChat.ChannelID");
        enabled = instance.getConfig().getBoolean("Bot.SyncChat.Enabled");
    }

    public void save() {
        instance.getConfig().set("Bot.Token", token);
        instance.getConfig().set("Bot.GuildID", guildId);
        instance.getConfig().set("Bot.SyncChat.ChannelID", channelId);
        instance.getConfig().set("Bot.SyncChat.Enabled", enabled);
        instance.saveConfig();
    }


    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getToken() {
        return token;
    }

    public String getChannelId() {
        return channelId;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String getGuildId() {
        return guildId;
    }
}
