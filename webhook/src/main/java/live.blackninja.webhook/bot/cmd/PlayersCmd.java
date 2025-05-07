package live.blackninja.webhook.bot.cmd;

import live.blackninja.webhook.Webhook;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PlayersCmd extends ListenerAdapter {

    private final Webhook webhook;

    public PlayersCmd(Webhook webhook) {
        this.webhook = webhook;
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if (event.getName().equals("players")) {

            String playerList = String.join(", ", Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .toList());

            EmbedBuilder onlinePlayers = new EmbedBuilder()
                    .setTitle("\uD83C\uDF10 | Spieler Online")
                    .setDescription("Akutelle Spieler, die sich auf dem SMP Server befinden.\n" +
                            "\n" +
                            "> *Es befinden sich aktuell **" + Bukkit.getOnlinePlayers().size() + "** Spieler auf dem Server*\n" +
                            "> **" + (Bukkit.getOnlinePlayers().isEmpty() ? "Keiner" : playerList) + "**\n")
                    .setColor(0x0085FF)
                    .setFooter("BlackNinja SMP Interactions");

            event.replyEmbeds(onlinePlayers.build()).setEphemeral(true).queue();
            return;
        }
    }
}
