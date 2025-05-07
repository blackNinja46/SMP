package live.blackninja.webhook.bot.cmd;

import live.blackninja.webhook.Webhook;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class EmbedCmd extends ListenerAdapter {

    private final Webhook webhook;

    public EmbedCmd(Webhook webhook) {
        this.webhook = webhook;
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if (event.getName().equals("embed-builder")) {
            if (!event.getMember().hasPermission(Permission.ADMINISTRATOR)) {
                event.reply("\uD83D\uDEAB Du hast **kein** Zugriff auf den Befehl **" + event.getName() + "**").setEphemeral(true).queue();
                return;
            }

            int option = event.getOption("id").getAsInt();

            if (option == 1) {
                EmbedBuilder commands = new EmbedBuilder()
                        .setTitle("\uD83D\uDD17 | Befehle")
                        .setDescription("ㅤ\n" +
                                "**/spawn**\n" +
                                "> Teleportiert dich an den SMP-Spawn.\n" +
                                "**/sit**\n" +
                                "> Setzte dich hin.\n" +
                                "\n" +
                                "**/tpa <Spieler>**\n" +
                                "> Sendet einem Spieler eine Teleportationsanfrage.\n" +
                                "**/tpa accept**\n" +
                                "> Nimmt eine Teleportationsanfrage an.\n" +
                                "**/tpa deny**\n" +
                                "> Lehne eine Teleportationsanfrage ab.\n" +
                                "\n" +
                                "**/home <Name>**\n" +
                                "> Teleportiert dich zu deinem Home\n" +
                                "**/home set <Name>**\n" +
                                "> Setzt einen Home zu dem du dich immer wieder teleportieren kannst.\n" +
                                "**/home delete <Name>**\n" +
                                "> Löscht einen gespeicherten Teleportations-Punkt.\n" +
                                "**/home list**\n" +
                                "> Listet alle gespeicherten Teleportations-Punkte auf.\n" +
                                "\n" +
                                "**/status set <Status>**\n" +
                                "> Setze dir einen bestimmten Status, der in  deinem Namen angezeigt werden soll.\n" +
                                "**/status add <Name> <Anzeige Namen>**¹\n" +
                                "> Erstelle einen neuen Status.\n" +
                                "**/status remove <Name>**¹\n" +
                                "> Lösche einen Status.\n" +
                                "**/status list**\n" +
                                "> Listet alle Status auf.\n" +
                                "\n" +
                                "**/vote [yes/no]**\n" +
                                "> Nehme an einer Globalen Abstimmung teil für einen Temporären Timeout eines ausgewählten Spieler. Stimme mit Ja oder Nein ab.\n" +
                                "**/vote create <Spieler> <Dauer der Abstimmung in Minuten> <Grund> <Sekunden | Minuten | Stunden | Tage>**¹\n" +
                                "> Erstelle eine Abstimmung.\n" +
                                "**/vote cancel**¹\n" +
                                "> Breche die aktuelle Abstimmung ab.\n" +
                                "\n" +
                                "**/ah sell <Kaufpreis> <Startpreis> <Dauer in Minuten>**\n" +
                                "> Verkaufe ein Item deiner Wahl und setze es für eine Bestimmte Zeit ins Auktionshaus.\n" +
                                "**/ah withdraw **\n" +
                                "> Nehme alle deine Items aus der Auktion\n" +
                                "**/ah paydebt**\n" +
                                "> Zahle den Preis um dein Item zu bekommen, falls du bei einer Auktion nicht Online warst.\n" +
                                "\n" +
                                "ㅤ")
                        .setColor(0x0085FF)
                        .setFooter("¹ Folgende Befehle erfordern einen bestimmten Rang, da sie vertrauenswürdiger sind als andere. Sie beeinträchtigen jedoch nicht das eigentliche Spielgeschehen und helfen dabei, Probleme auf dem Server zu vermeiden.");

                event.getChannel().sendMessageEmbeds(commands.build()).queue();
                return;
            }

            if (option == 2) {
                EmbedBuilder rules = new EmbedBuilder()
                        .setTitle("\uD83D\uDCDD | Regelwerk")
                        .setDescription("*Die Offiziellen Regeln des SMP Servers*\n" +
                                "\n" +
                                "• **Griefen** ist in jeglicher form (klauen, zerstören, Creeper etc.) verboten und wird mit einem **1 Tages Ban** bestraft!\n" +
                                "\n" +
                                "• **Cheaten** (Xray, hacken etc. ) ist in allen formen ausdrücklich untersagt und wird **individuell bestraft**! (Chat Abstimmung oder durch einen Moderator)\n" +
                                "\n" +
                                "• **Dupen** in form von Ressourcen und Ausrüstung wird strengstens untersagt, materielle Duplikationen wie Sand oder Teppiche werden geduldet solange sie keine Lags produzieren!\n" +
                                "\n" +
                                "• Ab stößige und **Ordnungswidrige Skins** werden nicht toleriert! \n" +
                                "\n" +
                                "> ❓| Solltest du noch weitere Fragen haben, melde dich bei einem Teammitglied. Wenn du einen Usermelden möchtest, schreibe einem <@&903699395382505572>")
                        .setColor(0xFF0000);

                event.getChannel().sendMessageEmbeds(rules.build()).queue();
                return;
            }

            event.reply("❌ | **Fehler**: Du hast eine **falsche ID** angegeben.").setEphemeral(true).queue();

            return;
        }
    }
}
