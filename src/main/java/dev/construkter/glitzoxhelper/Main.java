package dev.construkter.glitzoxhelper;

import dev.construkter.glitzoxhelper.commands.MessageCommandHandler;
import dev.construkter.glitzoxhelper.commands.SlashCommandHandler;
import dev.construkter.glitzoxhelper.listeners.DebugLogger;
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
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.time.Instant;

public class Main extends ListenerAdapter {

    public static TextChannel logChannel;
    public static TextChannel announcementChannel;
    public static final String VERSION = "1.0.4";
    public static Logger logger = GlitzoXLogger.getUniversalLogger();
    public static boolean DEBUG_MODE = false;

    public static void main(String[] args) {
        GlitzoXLogger.init();
        if (args.length >= 1) {
            for (String arg : args) {
                if (arg.equalsIgnoreCase("debug")) {
                    DEBUG_MODE = true;
                    logger.debug("DEBUG MODE - DON'T USE THIS IN PROD");
                }
            }
        }
        JDA jda = JDABuilder.createDefault(Token.get(), GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MEMBERS)
                .setActivity(Activity.playing("GlitzoriumX"))
                .disableCache(CacheFlag.ACTIVITY, CacheFlag.VOICE_STATE, CacheFlag.EMOJI, CacheFlag.STICKER, CacheFlag.CLIENT_STATUS, CacheFlag.ONLINE_STATUS, CacheFlag.SCHEDULED_EVENTS)
                .addEventListeners(new SlashCommandHandler(), new Main(), new JoinHandler(), new MessageCommandHandler(), new DebugLogger())
                .build();
    }

    @Override
    public void onReady(ReadyEvent event) {
        JDA api = event.getJDA();

        logChannel = api.getTextChannelById(1421916551619149824L);
        announcementChannel = api.getTextChannelById(1420468205360517162L);

        for (Guild guild : api.getGuilds()) {
            guild.updateCommands().addCommands(
                    Commands.slash("help", "Zeigt ein Hilfe Menü an"),
                    Commands.slash("announce", "Sendet eine Message an alle User")
                            .addOption(OptionType.STRING, "type", "Der Typ des Announcements (dm/channel)", true)
                            .addOption(OptionType.STRING, "message", "Die Nachricht die gesendet werden soll", true),
                    Commands.slash("alive", "Überprüfe ob der Server online ist"),
                    Commands.slash("version", "Zeigt die aktuelle Version der Instanz an"),
                    Commands.slash("clear", "Löscht eine gewissen Anzahl an Nachrichten (x!c)")
                            .addOption(OptionType.INTEGER, "amount", "Wie viele Nachrichten gelöscht werden", true)
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