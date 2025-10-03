package dev.construkter.glitzoxhelper;

import dev.construkter.glitzoxhelper.commands.SlashCommandHandler;
import dev.construkter.glitzoxhelper.listeners.JoinHandler;
import dev.construkter.glitzoxhelper.logging.GlitzoXLogger;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.time.Instant;

public class Main extends ListenerAdapter {

    public static TextChannel logChannel;
    public static TextChannel announcementChannel;
    public static final String VERSION = "1.0.2-l";
    public static Logger logger;

    public static void main(String[] args) {
        JDA jda = JDABuilder.createDefault(Token.get())
                .setActivity(Activity.playing("GlitzoriumX"))
                .addEventListeners(new SlashCommandHandler(), new Main(), new JoinHandler())
                .build();
        try {
            jda.awaitReady();
        } catch (InterruptedException e) {
            logger.error("Interrupted while waiting for JDA to start");
        }
    }

    @Override
    public void onReady(ReadyEvent event) {
        JDA api = event.getJDA();

        logChannel = api.getTextChannelById(1421916551619149824L);
        announcementChannel = api.getTextChannelById(1420468205360517162L);
        logger = LoggerFactory.getLogger(GlitzoXLogger.class);

        for (Guild guild : api.getGuilds()) {
            guild.updateCommands().addCommands(
                    Commands.slash("help", "Zeigt ein Hilfe Menü an"),
                    Commands.slash("announce", "Sendet eine Message an alle User")
                            .addOption(OptionType.STRING, "type", "Der Typ des Announcements (dm/channel)", true)
                            .addOption(OptionType.STRING, "message", "Die Nachricht die gesendet werden soll", true),
                    Commands.slash("alive", "Überprüfe ob der Server online ist")
            ).queue();
            logger.info("Registering commands for Guild {}", guild.getName());
        }

        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setTitle("GlitzoX Ready")
                .setDescription("GlitzoX has initialized!")
                .addField("Version", VERSION, true)
                .setColor(Color.GREEN)
                .setFooter("GlitzoX")
                .setTimestamp(Instant.now());

        logChannel.sendMessageEmbeds(embedBuilder.build()).queue();
    }
}