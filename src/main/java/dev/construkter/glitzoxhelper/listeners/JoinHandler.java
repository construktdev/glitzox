package dev.construkter.glitzoxhelper.listeners;

import dev.construkter.glitzoxhelper.Main;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.time.Instant;

public class JoinHandler extends ListenerAdapter {

    private static final TextChannel LOG = Main.logChannel;

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setTitle("New Member joined")
                .setColor(Color.ORANGE)
                .setDescription(event.getMember().getAsMention() + " joined the server.")
                .setFooter(event.getMember().getUser().getName(), event.getMember().getUser().getAvatarUrl())
                .setTimestamp(Instant.now());

        LOG.sendMessageEmbeds(embedBuilder.build()).queue();
    }
}
