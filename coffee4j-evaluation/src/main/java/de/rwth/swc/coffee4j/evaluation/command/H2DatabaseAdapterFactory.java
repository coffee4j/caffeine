package de.rwth.swc.coffee4j.evaluation.command;

import de.rwth.swc.coffee4j.evaluation.persistence.DatabaseAdapter;
import de.rwth.swc.coffee4j.evaluation.persistence.H2DatabaseAdapter;

import java.io.IOException;
import java.nio.file.Path;

/**
 * {@link DatabaseAdapterFactory} that initializes a {@link H2DatabaseAdapterFactory}.
 * <p>
 * Calling ith with a {@code null} path, returns an in-memory database, while calling it with a valid path a file-based
 * one.
 */
public class H2DatabaseAdapterFactory implements DatabaseAdapterFactory {
    @Override
    public DatabaseAdapter createDatabaseAdapter(Path path) throws IOException {
        if (path == null) {
            return new H2DatabaseAdapter();
        } else {
            return new H2DatabaseAdapter(path);
        }
    }
}
