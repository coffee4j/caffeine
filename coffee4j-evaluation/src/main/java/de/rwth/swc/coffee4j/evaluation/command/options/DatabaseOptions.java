package de.rwth.swc.coffee4j.evaluation.command.options;

import de.rwth.swc.coffee4j.evaluation.persistence.DatabaseAdapter;
import de.rwth.swc.coffee4j.evaluation.persistence.H2DatabaseAdapter;
import picocli.CommandLine;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Command line options for the {@link de.rwth.swc.coffee4j.evaluation.command.DatabaseCommand}.
 * <p>
 * It provides a path to the database, as well as a adapter to a H2 database with the given path. Should no path be
 * specified then an in-memory database is created. This is primarily used for testing. Therefore, users need to take
 * care to provide a database path if they want to persist the data longer than the command execution time. Should the
 * user specify a file locations that does not already contain a database, then a new file-based H2 database is created.
 * For this flyway executes the migration files in the resource folder.
 */
public class DatabaseOptions {

    @CommandLine.Option(
            names = "--db",
            description = "The path to the H2 database.",
            paramLabel = "PATH")
    private Path pathToDatabase;

    /**
     * Gets {@link DatabaseAdapter} to an H2 database for the given path.
     * <p>
     * If the path is {@code null} an in-memory database is created.
     *
     * @return a database adapter
     * @throws IOException if the database creation failed.
     */
    public DatabaseAdapter getAdapter() throws IOException {
        if (pathToDatabase == null) {
            return new H2DatabaseAdapter();
        } else {
            return new H2DatabaseAdapter(pathToDatabase);
        }
    }

    /**
     * Gets the path to the database.
     * <p>
     * Depending if this method is called before or after {@link #getAdapter()}, this path may not contain a database.
     *
     * @return the database path
     */
    public Path getPath() {
        return pathToDatabase;
    }
}
