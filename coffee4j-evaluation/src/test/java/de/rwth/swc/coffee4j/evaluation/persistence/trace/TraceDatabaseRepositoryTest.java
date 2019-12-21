package de.rwth.swc.coffee4j.evaluation.persistence.trace;

import de.rwth.swc.coffee4j.evaluation.persistence.DatabaseAdapter;
import de.rwth.swc.coffee4j.evaluation.persistence.H2DatabaseAdapter;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.io.IOException;

@Execution(ExecutionMode.SAME_THREAD)
@Tag("Database")
class TraceDatabaseRepositoryTest implements TraceRepositoryTest {


    @Override
    public DatabaseAdapter provideDataSource() throws IOException {
        return new H2DatabaseAdapter();
    }
}