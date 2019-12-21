package de.rwth.swc.coffee4j.evaluation.persistence.trace;

import de.rwth.swc.coffee4j.evaluation.persistence.DatabaseAdapter;
import de.rwth.swc.coffee4j.evaluation.persistence.SimpleDatabaseAdapter;

public class SimpleTraceRepositoryTest implements TraceRepositoryTest {
    @Override
    public DatabaseAdapter provideDataSource() {
        return new SimpleDatabaseAdapter();
    }
}
