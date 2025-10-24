package dev.construkter.glitzoxhelper.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GlitzoXLogger {
    private static Logger LOGGER;
    public static void init() {
        LOGGER = LoggerFactory.getLogger(GlitzoXLogger.class);
    }

    public static Logger getUniversalLogger() {
        if (LOGGER == null) {
            LOGGER = LoggerFactory.getLogger(GlitzoXLogger.class);
        }

        return LOGGER;
    }
}
