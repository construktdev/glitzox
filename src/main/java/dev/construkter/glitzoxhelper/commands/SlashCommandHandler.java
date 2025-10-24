package dev.construkter.glitzoxhelper.commands;

import dev.construkter.glitzoxhelper.Main;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.time.Instant;

public class SlashCommandHandler extends ListenerAdapter {

    static TextChannel announcementChannel = Main.announcementChannel;
    private static final Logger LOGGER = LoggerFactory.getLogger(SlashCommandHandler.class);

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        switch (event.getName()) {
            case "help" -> {
                EmbedBuilder helEmbed = new EmbedBuilder()
                        .setTitle("\uD83D\uDCD6 Hilfe")
                        .addField("</help:1421914614043644096>", "Zeigt diesen Embed", false)
                        .addField("</announce:1421934775266050213> <type> <message>", "Erlaubt Admins Nachrichten zu veröffentlichen", false)
                        .addField("</alive:1422252921814253610>", "Überprüft ob der Minecraft Server online ist", false)
                        .addField("</version:1423758521287184484>", "Zeigt die aktuelle Version des Bots an", false)
                        .setFooter("GlitzoX Helper")
                        .setTimestamp(Instant.now());

                event.replyEmbeds(helEmbed.build()).queue();
            }
            case "announce" -> {

                if (event.getMember() != null && !event.getMember().hasPermission(Permission.ADMINISTRATOR)) {
                    event.reply("Du hast keine Berechtigung diesen Befehl zu nutzen!").setEphemeral(true).queue();
                    return;
                }

                if (event.getMember() == null)  {
                    return; // Other App, which is quite impossible, but JDA wants the check
                }

                var typeIn = event.getOption("type");
                var messageIn = event.getOption("message");

                if (typeIn == null || messageIn == null) {
                    event.reply("Usage: /announce <type> <message>").setEphemeral(true).queue();
                    return;
                }

                var type = AnnouncementType.getTypeByString(typeIn.getAsString());

                if (type == null) {
                    event.reply("Wrong AnnouncementType (dm or channel)").setEphemeral(true).queue();
                    return;
                }

                EmbedBuilder embedBuilder = new EmbedBuilder()
                        .setTitle("\uD83D\uDCE3 - Announcement")
                        .setDescription(messageIn.getAsString())
                        .setFooter("Von GlitzoriumX", "https://cdn.construkter.de/glitzorium.png")
                        .setColor(Color.GREEN)
                        .setTimestamp(Instant.now());

                if (type.toString().equalsIgnoreCase("channel")) {
                    if (announcementChannel == null) {
                        announcementChannel = Main.announcementChannel;
                    }

                    announcementChannel.sendMessageEmbeds(embedBuilder.build()).queue();
                } else if (type.toString().equalsIgnoreCase("dm")) {
                    Guild guild = event.getGuild();

                    if (guild == null) {
                        event.reply("❌ Dieser Befehl kann nur in Gilden ausgeführt werden").setEphemeral(true).queue();
                        return;
                    }

                    for (Member member : guild.getMembers()) {
                        if (member.getUser().isBot()) {
                            continue;
                        }
                        member.getUser().openPrivateChannel().queue(privateChannel -> {
                            privateChannel.sendMessageEmbeds(embedBuilder.build()).queue();
                        });
                    }
                }

                event.reply("✅ Die Nachricht wurde erfolgreich versendet!").setEphemeral(true).queue();
            }
            case "alive" -> {
                EmbedBuilder embedBuilder = new EmbedBuilder()
                        .setTitle("Checking...")
                        .setDescription("\uD83D\uDD03 Checking if the server is online")
                        .setFooter("GlitzoX Helper")
                        .setTimestamp(Instant.now())
                        .setColor(Color.ORANGE);

                event.replyEmbeds(embedBuilder.build()).queue( messsage -> {
                    try {
                        Thread.sleep(500L);
                    } catch (InterruptedException e) {
                        // pass
                    }

                    EmbedBuilder embedBuilder2 = new EmbedBuilder()
                            .setTitle("\uD83D\uDDA5\uFE0F Is the Server Online?")
                            .setFooter("GlitzoX Helper")
                            .setTimestamp(Instant.now());

                    if (isOnline()) {
                        embedBuilder2.setDescription("<:SUCCESS77:1422251435059318946> Der Server ist online!")
                                .setColor(Color.GREEN);
                    } else {
                        embedBuilder2.setDescription("❌ Der Server ist offline!")
                                .setColor(Color.RED);
                    }

                    messsage.editOriginalEmbeds(embedBuilder2.build()).queue();
                });
            }

            case "version" -> {
                EmbedBuilder embedBuilder = new EmbedBuilder()
                        .setTitle("♻\uFE0F GlitzoX Version")
                        .setDescription("Diese GlitzoXHelper Instanz nutzt die Version " + Main.VERSION)
                        .setFooter("GlitzoX Helper")
                        .setTimestamp(Instant.now())
                        .setColor(Color.GREEN);

                event.replyEmbeds(embedBuilder.build()).queue();
            }

            case "clear" -> {
                if (event.getGuild() == null || event.getMember() == null) {
                    return;
                }

                if (!event.getMember().hasPermission(Permission.ADMINISTRATOR)) {
                    event.reply("Du musst Admin sein um diesen Befehl auszuführen").setEphemeral(true).queue();
                    return;
                }
                var amount = event.getOption("amount");

                if (amount == null) {
                    event.reply("Bitte gebe an wie viel du löschen möchtest (2-100)").setEphemeral(true).queue();
                    return;
                }

                int messages = amount.getAsInt();

                if (messages < 2 || messages > 100) {
                    event.reply("Bitte gebe an wie viel du löschen möchtest (2-100)").setEphemeral(true).queue();
                    return;
                }

                TextChannel channel = event.getChannel().asTextChannel();

                channel.getHistory().retrievePast(messages).queue(history -> {
                    channel.deleteMessages(history).queue(
                            success -> event.reply("✅ Nachrichten erfolgreich gelöscht").setEphemeral(true).queue(),
                            error -> event.reply(error.getMessage()).setEphemeral(false).queue()
                    );
                });

            }

            default -> event.reply("Invalid Command Interaction").setEphemeral(true).queue();
        }

        LOGGER.info("{} triggered the command {} in {} ({})", event.getMember().getUser().getName(), event.getName(), event.getChannel().getName(), event.getGuild().getName());
    }

    private static boolean isOnline() {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress("xeon1.server.construkter.dev", 25565), 2000);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}

enum AnnouncementType {
    channel,
    dm;

    @Nullable
    public static AnnouncementType getTypeByString(String type) {
        if (type.equalsIgnoreCase("channel")) {
            return AnnouncementType.channel;
        } else if (type.equalsIgnoreCase("dm")) {
            return AnnouncementType.dm;
        } else {
            return null;
        }
    }

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
