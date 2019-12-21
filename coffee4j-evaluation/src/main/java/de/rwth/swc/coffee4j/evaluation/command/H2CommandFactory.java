package de.rwth.swc.coffee4j.evaluation.command;

import picocli.CommandLine;

import java.lang.reflect.Constructor;

/**
 * Command factory that initializes each {@link DatabaseCommand} with an {@link H2DatabaseAdapterFactory}.
 */
class H2CommandFactory implements CommandLine.IFactory {
    @Override
    public <K> K create(Class<K> aClass) throws Exception {
        try {
            if (DatabaseCommand.class.isAssignableFrom(aClass)) {
                Constructor<K> constructor = aClass.getConstructor(DatabaseAdapterFactory.class);
                return constructor.newInstance(new H2DatabaseAdapterFactory());
            } else {
                return CommandLine.defaultFactory().create(aClass);
            }
        } catch (Exception e) {
            return CommandLine.defaultFactory().create(aClass);
        }
    }
}
