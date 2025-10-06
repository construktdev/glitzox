package dev.construkter.glitzoxhelper.commands;

import dev.construkter.glitzoxhelper.Main;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class MessageCommandHandler extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {

        if (event.getAuthor().isBot()) return;
        if (event.getMessage().getContentRaw().startsWith("x!")) {
            if (event.getMessage().getContentRaw().split("!")[1].startsWith("c ")) {
                String[] args = event.getMessage().getContentRaw().split(" ");
                event.getMessage().delete().queue();

                if (event.getMember() == null) {
                    return;
                }

                if (!event.getMember().hasPermission(Permission.ADMINISTRATOR)) {
                    event.getMessage().reply("Du musst Admin sein um diesen Befehl auszuführen").queue();
                    return;
                }

                int messages = 0;

                if (args.length == 2) {
                    try {
                        messages = Integer.parseInt(args[1]);
                    } catch (NumberFormatException e) {
                        event.getMessage().reply("Bitte gebe eine Zahl von 1-100 an: " + args[1]).queue();
                        return;
                    }
                }

                TextChannel channel = event.getChannel().asTextChannel();

                channel.getHistory().retrievePast(messages).queue(history -> {
                    channel.deleteMessages(history).queue();
                });
            } else {
                event.getMessage().reply("Command nicht gefunden").queue();
            }
        }
    }
}
