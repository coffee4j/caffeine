package de.rwth.swc.coffee4j.evaluation.trace.processor;

import okio.BufferedSink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link LineProcessor} that logs any received communication.
 */
public class LoggingLineProcessor implements LineProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingLineProcessor.class);

    private final Level level;

    /**
     * Constructor.
     *
     * @param level the level the communication should be logged at
     */
    public LoggingLineProcessor(Level level) {
        this.level = level;
    }

    @Override
    public void processLine(String line, BufferedSink output) {
        switch (level) {
            case TRACE:
                LOGGER.trace(line);
                break;
            case DEBUG:
                LOGGER.debug(line);
                break;
            case INFO:
                LOGGER.info(line);
                break;
            case WARN:
                LOGGER.warn(line);
                break;
            case ERROR:
                LOGGER.error(line);
                break;
        }
    }

    /**
     * Log level enum mirroring the slf4j log levels.
     */
    public enum Level {
        /**
         * Trace log level.
         */
        TRACE,
        /**
         * Debug log level.
         */
        DEBUG,
        /**
         * Info log level.
         */
        INFO,
        /**
         * Warn log level.
         */
        WARN,
        /**
         * Error log level.
         */
        ERROR
    }
}
