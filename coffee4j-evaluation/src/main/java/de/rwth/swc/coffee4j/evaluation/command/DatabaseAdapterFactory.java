package de.rwth.swc.coffee4j.evaluation.command;

import de.rwth.swc.coffee4j.evaluation.persistence.DatabaseAdapter;

import java.io.IOException;
import java.nio.file.Path;

/**
 * A factory which creates a {@link DatabaseAdapter} from a given path.
 * <p>
 * This path may be ignored or used to specify the database location. The exact handling depends on the implementing
 * classes.
 */
public interface DatabaseAdapterFactory {

    /**
     * Creates a new database adapter.
     *
     * @param path the path to the database, or {@code null} if no path is specified
     * @return the database adapter
     * @throws IOException if the connection fails
     */
    DatabaseAdapter createDatabaseAdapter(Path path) throws IOException;

}
