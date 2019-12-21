package de.rwth.swc.coffee4j.evaluation.persistence.analysis;

import de.rwth.swc.coffee4j.evaluation.persistence.DatabaseAdapter;
import de.rwth.swc.coffee4j.evaluation.persistence.SimpleDatabaseAdapter;

public class SimpleAnalysisRepositoryTest implements AnalysisRepositoryTest {

    @Override
    public DatabaseAdapter provideDataSource() {
        return new SimpleDatabaseAdapter();
    }
}
