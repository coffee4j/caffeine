package de.rwth.swc.coffee4j.evaluation.command;

import de.rwth.swc.coffee4j.evaluation.persistence.DatabaseAdapter;
import de.rwth.swc.coffee4j.evaluation.persistence.SimpleDatabaseAdapter;

import java.nio.file.Path;

/**
 * {@link DatabaseAdapterFactory} that returns a given {@link SimpleDatabaseAdapter}.
 * <p>
 * This can be used for command testing on a given database.
 */
public class SimpleDatabaseAdapterFactory implements DatabaseAdapterFactory {

    private final SimpleDatabaseAdapter db;

    /**
     * Constructor.
     *
     * @param db the database adapter to copy from
     */
    SimpleDatabaseAdapterFactory(SimpleDatabaseAdapter db) {
        this.db = db;
    }


    @Override
    public DatabaseAdapter createDatabaseAdapter(Path path) {
        return db;
    }
}
