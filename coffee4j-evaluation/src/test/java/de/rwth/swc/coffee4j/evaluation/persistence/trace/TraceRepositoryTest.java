package de.rwth.swc.coffee4j.evaluation.persistence.trace;

import de.rwth.swc.coffee4j.evaluation.TestData;
import de.rwth.swc.coffee4j.evaluation.persistence.DatabaseAdapter;
import de.rwth.swc.coffee4j.evaluation.persistence.model.ModelRepository;
import de.rwth.swc.coffee4j.evaluation.trace.Trace;
import de.rwth.swc.coffee4j.evaluation.trace.TraceIdentifier;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

interface TraceRepositoryTest {

    DatabaseAdapter provideDataSource() throws IOException;

    @Test
    default void shouldReadWrittenTraces() throws IOException {
        try (DatabaseAdapter db = provideDataSource()) {
            ModelRepository modelRepository = db.getModelRepository();
            TraceRepository traceRepository = db.getTraceRepository();
            modelRepository.write(TestData.MODEL_1);
            modelRepository.write(TestData.MODEL_2);

            assertFalse(traceRepository.exists(TestData.TRACE_1.getIdentifier()));
            assertFalse(traceRepository.exists(TestData.TRACE_2.getIdentifier()));
            assertFalse(traceRepository.exists(TestData.TRACE_3.getIdentifier()));

            traceRepository.write(TestData.TRACE_1);
            traceRepository.write(TestData.TRACE_2);
            traceRepository.write(TestData.TRACE_3);

            assertTrue(traceRepository.exists(TestData.TRACE_1.getIdentifier()));
            assertTrue(traceRepository.exists(TestData.TRACE_2.getIdentifier()));
            assertTrue(traceRepository.exists(TestData.TRACE_3.getIdentifier()));
            assertFalse(traceRepository.exists(new TraceIdentifier("", "", "")));

            assertEquals(TestData.TRACE_1, traceRepository.get(TestData.TRACE_1.getIdentifier()));
            assertEquals(TestData.TRACE_2, traceRepository.get(TestData.TRACE_2.getIdentifier()));
            assertEquals(TestData.TRACE_3, traceRepository.get(TestData.TRACE_3.getIdentifier()));
        }
    }

    @Test
    default void shouldThrowIfTraceNotFound() throws IOException {
        try (DatabaseAdapter db = provideDataSource()) {
            TraceRepository traceRepository = db.getTraceRepository();
            assertThrows(NoSuchElementException.class,
                    () -> traceRepository.get(new TraceIdentifier("", "", "")));
        }
    }

    @Test
    default void shouldThrowIfWritingWithoutStoredModel() throws IOException {
        try (DatabaseAdapter db = provideDataSource()) {
            TraceRepository traceRepository = db.getTraceRepository();
            assertThrows(NoSuchElementException.class, () -> traceRepository.write(TestData.TRACE_1));
        }
    }

    @Test
    default void getAllShouldStreamAllTraces() throws IOException {
        try (DatabaseAdapter db = provideDataSource()) {
            ModelRepository modelRepository = db.getModelRepository();
            TraceRepository traceRepository = db.getTraceRepository();
            modelRepository.write(TestData.MODEL_1);
            modelRepository.write(TestData.MODEL_2);
            traceRepository.write(TestData.TRACE_1);
            traceRepository.write(TestData.TRACE_2);
            traceRepository.write(TestData.TRACE_3);
            try (Stream<Trace> stream = traceRepository.getAll()) {
                List<Trace> read = stream.collect(Collectors.toList());
                assertEquals(3, read.size());
                assertTrue(read.contains(TestData.TRACE_1));
                assertTrue(read.contains(TestData.TRACE_2));
                assertTrue(read.contains(TestData.TRACE_3));
            }
            assertEquals(3, traceRepository.info().getNumberOfTraces());
        }
    }

}
