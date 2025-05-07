package live.blackninja.webhook.bot.listener;

import live.blackninja.webhook.Webhook;
import live.blackninja.webhook.bot.Bot;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class MessageReceivedListener extends ListenerAdapter {

    private final Webhook webhook;

    public MessageReceivedListener(Webhook webhook) {
        this.webhook = webhook;
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        Guild guild = Bot.getJda().getGuildById(webhook.getConfigManger().getGuildId());

        if (guild == null) {
            System.out.println("[SMPDiscordWebhook] Guild not found! Check the config.yml");
            return;
        }

        TextChannel textChannel = guild.getTextChannelById(webhook.getConfigManger().getChannelId());

        if (textChannel == null) {
            System.out.println("[SMPDiscordWebhook] Channel not found! Check the config.yml");
            return;
        }

        if (event.getChannel() != textChannel) {
            return;
        }

        if (event.getAuthor().isBot()) {
            return;
        }

        if (event.getMember() == null) {
            return;
        }

        event.getMessage().delete().queue();

        EmbedBuilder messageEmbed = new EmbedBuilder()
                .setAuthor(event.getAuthor().getAsTag(), null, event.getAuthor().getAvatarUrl())
                .setDescription(event.getMessage().getContentDisplay())
                .setColor(webhook.getConfigManger().isEnabled() ? 0x00AEFF : 0xFF2D60);

        event.getChannel().sendMessageEmbeds(messageEmbed.build()).queue();

        if (!webhook.getConfigManger().isEnabled()) {
            return;
        }

        webhook.getBot().getSyncChatManger().sendPlayerMessage(event.getMember(), event.getMessage());

    }
}
