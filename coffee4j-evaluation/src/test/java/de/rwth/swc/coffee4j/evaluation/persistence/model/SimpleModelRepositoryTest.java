package de.rwth.swc.coffee4j.evaluation.persistence.model;

import de.rwth.swc.coffee4j.evaluation.persistence.DatabaseAdapter;
import de.rwth.swc.coffee4j.evaluation.persistence.SimpleDatabaseAdapter;

public class SimpleModelRepositoryTest implements ModelRepositoryTest {
    @Override
    public DatabaseAdapter provideDataSource() {
        return new SimpleDatabaseAdapter();
    }
}
