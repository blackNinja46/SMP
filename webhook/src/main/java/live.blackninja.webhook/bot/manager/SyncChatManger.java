package live.blackninja.webhook.bot.manager;

import live.blackninja.smp.builder.MessageBuilder;
import live.blackninja.webhook.Webhook;
import live.blackninja.webhook.bot.Bot;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.time.LocalDateTime;

public class SyncChatManger {

    private final Webhook webhook;

    public SyncChatManger(Webhook webhook) {
        this.webhook = webhook;
    }

    public void sendBotMessage(Player player, String message) {

        EmbedBuilder messageEmbed = new EmbedBuilder()
                .setAuthor(player.getName(), null, "https://mc-heads.net/head/" + player.getName())
                .setDescription(message)
                .setColor(0x00AEFF);

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

        textChannel.sendMessageEmbeds(messageEmbed.build()).queue();

    }

    public void sendPlayerMessage(Member member, Message message) {

        Component finalMessage;
        User author = member.getUser();

        if (member.hasPermission(Permission.ADMINISTRATOR)) {
            finalMessage = MessageBuilder.build("<color:#ff8045>" + author.getAsTag() + " <dark_gray>[<color:#7d97ff>Discord<dark_gray>]: <white>" + message.getContentDisplay());

            for (Player player : Bukkit.getOnlinePlayers()) {
                player.sendMessage(finalMessage);
            }
            return;
        }

        finalMessage = MessageBuilder.build("<white>" + author.getAsTag() + " <dark_gray>[<color:#7d97ff>Discord<dark_gray>]: <white>" + message.getContentDisplay());

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(finalMessage);
        }


    }

}
