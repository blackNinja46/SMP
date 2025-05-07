package live.blackninja.webhook.bot.cmd;

import live.blackninja.webhook.Webhook;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class SyncChatCmd extends ListenerAdapter {

    private final Webhook webhook;

    public SyncChatCmd(Webhook webhook) {
        this.webhook = webhook;
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if (event.getName().equals("syncchat")) {
            if (!event.getMember().hasPermission(Permission.ADMINISTRATOR)) {
                event.reply("\uD83D\uDEAB Du hast **kein** Zugriff auf den Befehl **" + event.getName() + "**").setEphemeral(true).queue();
                return;
            }

            boolean syncChat = event.getOption("syncchatenabled").getAsBoolean();

            if (!syncChat) {
                webhook.getConfigManger().setEnabled(false);

                EmbedBuilder syncChatOn = new EmbedBuilder()
                        .setTitle("❌ | SyncChat")
                        .setDescription("> Der **Chat** wird nun **nicht** mehr mit dem **Minecraft SMP** Chat synchronisiert.")
                        .setColor(0xFF0000)
                        .setFooter("BlackNinja SMP Interactions");

                event.replyEmbeds(syncChatOn.build()).setEphemeral(true).queue();
                return;
            }
            webhook.getConfigManger().setEnabled(true);

            EmbedBuilder syncChatOn = new EmbedBuilder()
                    .setTitle("✅ | SyncChat")
                    .setDescription("> Der **Chat** wird nun mit dem **Minecraft SMP** **Chat synchronisiert**.")
                    .setColor(0x00FF00)
                    .setFooter("BlackNinja SMP Interactions");

            event.replyEmbeds(syncChatOn.build()).setEphemeral(true).queue();
        }
    }
}
