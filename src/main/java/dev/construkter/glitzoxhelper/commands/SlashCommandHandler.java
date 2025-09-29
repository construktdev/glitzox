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

import java.awt.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.time.Instant;

public class SlashCommandHandler extends ListenerAdapter {

    static TextChannel announcementChannel = Main.announcementChannel;

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        switch (event.getName()) {
            case "help" -> {
                EmbedBuilder helEmbed = new EmbedBuilder()
                        .setTitle("Hilfe")
                        .addField("</help:1421914614043644096>", "Zeigt diesen Embed", false)
                        .addField("</announce:1421934775266050213> <type> <message>", "Erlaubt Admins Nachrichten zu veröffentlichen", false)
                        .addField("</alive:1422252921814253610>", "Überprüft ob der Minecraft Server online ist", false)
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

                var type = Type.getTypeByString(typeIn.getAsString());

                if (type == null) {
                    event.reply("Wrong Type (dm or channel)").setEphemeral(true).queue();
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
                        event.reply("Dieser Befehl kann nur in Gilden ausgeführt werden").setEphemeral(true).queue();
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

                event.reply("Die Nachricht wurde erfolgreich versendet!").setEphemeral(true).queue();
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
                            .setTitle("Is the Server Online?")
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

            default -> event.reply("Invalid Command Interaction").setEphemeral(true).queue();
        }
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

enum Type {
    channel,
    dm;

    @Nullable
    public static Type getTypeByString(String type) {
        if (type.equalsIgnoreCase("channel")) {
            return Type.channel;
        } else if (type.equalsIgnoreCase("dm")) {
            return Type.dm;
        } else {
            return null;
        }
    }

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
