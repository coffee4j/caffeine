package de.rwth.swc.coffee4j.evaluation.command;

import de.rwth.swc.coffee4j.evaluation.command.options.DatabaseOptions;
import de.rwth.swc.coffee4j.evaluation.persistence.DatabaseAdapter;
import de.rwth.swc.coffee4j.evaluation.utils.ExitCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

import java.io.IOException;

/**
 * Abstract parent class for commands accessing the database.
 * <p>
 * It provides the necessary options, initializes the connection, and closes it after execution. Should an invalid path
 * be specified, then the command terminates with an error.
 * <p>
 * This class is intended to be subclassed by all commands that need to access the persistence component.
 */
public abstract class DatabaseCommand extends BaseCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseCommand.class);

    private final DatabaseAdapterFactory dbFactory;

    @CommandLine.Mixin
    private DatabaseOptions databaseOptions;

    /**
     * Constructor.
     *
     * @param dbFactory the factory providing access to a database adapter
     */
    public DatabaseCommand(DatabaseAdapterFactory dbFactory) {
        this.dbFactory = dbFactory;
    }

    @Override
    protected ExitCode execute() {
        try (DatabaseAdapter db = dbFactory.createDatabaseAdapter(databaseOptions.getPath())) {
            return execute(db);
        } catch (IOException e) {
            LOGGER.error("Error connecting to the database: {}", e.getMessage());
            return ExitCode.ERROR;
        }
    }

    /**
     * Execute the command.
     *
     * @param db the adapter providing access to the database. It must not be {@code null}.
     * @return the exit code of the command.
     */
    protected abstract ExitCode execute(DatabaseAdapter db);
}
