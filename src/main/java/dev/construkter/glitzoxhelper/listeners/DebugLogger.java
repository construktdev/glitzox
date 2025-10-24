package dev.construkter.glitzoxhelper.listeners;

import dev.construkter.glitzoxhelper.Main;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DebugLogger extends ListenerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(DebugLogger.class);

    @Override
    public void onGenericEvent(GenericEvent event) {
        if (!Main.DEBUG_MODE) return;

        LOGGER.info("Received event {}", event.toString());
    }
}
