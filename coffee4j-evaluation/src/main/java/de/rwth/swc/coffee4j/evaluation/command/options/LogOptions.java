package de.rwth.swc.coffee4j.evaluation.command.options;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import picocli.CommandLine;

/**
 * {@link picocli.CommandLine.Mixin} for configuring the logging level.
 * <p>
 * The internal logback level is set to the appropriate value when calling {@link #setLogLevel()}. This replaces the
 * need for any quiet or verbose options.
 */
public class LogOptions {

    @CommandLine.Option(
            names = {"--log-level"},
            defaultValue = "INFO",
            description = "Log levels: ${COMPLETION-CANDIDATES}%nDefault: ${DEFAULT-VALUE}",
            paramLabel = "LEVEL")
    private LogLevel level;

    /**
     * Sets the logging level of the root logger to the command line argument. This only works when this class has been
     * used as a mixin for a command line command.
     */
    public void setLogLevel() {
        Logger root = (Logger) org.slf4j.LoggerFactory.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
        root.setLevel(level.getLevel());
    }

    private enum LogLevel {
        TRACE(Level.TRACE),
        DEBUG(Level.DEBUG),
        INFO(Level.INFO),
        WARN(Level.WARN),
        ERROR(Level.ERROR),
        OFF(Level.OFF);

        private final Level level;

        LogLevel(Level level) {
            this.level = level;
        }

        public Level getLevel() {
            return level;
        }
    }

}
