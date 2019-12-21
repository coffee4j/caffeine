package de.rwth.swc.coffee4j.evaluation.command;

import de.rwth.swc.coffee4j.evaluation.persistence.SimpleDatabaseAdapter;
import picocli.CommandLine;

import java.lang.reflect.Constructor;

/**
 * A command factory which initializes all {@link DatabaseCommand} instances with a {@link
 * SimpleDatabaseAdapterFactory}.
 */
public class SimpleCommandFactory implements CommandLine.IFactory {

    private final SimpleDatabaseAdapter db;

    /**
     * Constructor.
     *
     * @param db the database adapter
     */
    public SimpleCommandFactory(SimpleDatabaseAdapter db) {
        this.db = db;
    }

    @Override
    public <K> K create(Class<K> aClass) throws Exception {
        try {
            if (DatabaseCommand.class.isAssignableFrom(aClass)) {
                Constructor<K> constructor = aClass.getConstructor(DatabaseAdapterFactory.class);
                return constructor.newInstance(new SimpleDatabaseAdapterFactory(db));
            } else {
                return CommandLine.defaultFactory().create(aClass);
            }
        } catch (Exception e) {
            return CommandLine.defaultFactory().create(aClass);
        }
    }
}