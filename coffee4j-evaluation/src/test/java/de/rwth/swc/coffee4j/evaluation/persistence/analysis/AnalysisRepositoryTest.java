package de.rwth.swc.coffee4j.evaluation.persistence.analysis;

import de.rwth.swc.coffee4j.evaluation.TestData;
import de.rwth.swc.coffee4j.evaluation.TestUtils;
import de.rwth.swc.coffee4j.evaluation.analysis.AnalysisResult;
import de.rwth.swc.coffee4j.evaluation.analysis.KeyInfo;
import de.rwth.swc.coffee4j.evaluation.persistence.DatabaseAdapter;
import de.rwth.swc.coffee4j.evaluation.persistence.model.ModelRepository;
import de.rwth.swc.coffee4j.evaluation.persistence.trace.TraceRepository;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public interface AnalysisRepositoryTest {

    DatabaseAdapter provideDataSource() throws IOException;

    @Test
    default void shouldReadWrittenAnalyses() throws IOException {
        try (DatabaseAdapter db = provideDataSource()) {
            ModelRepository modelRepository = db.getModelRepository();
            TraceRepository traceRepository = db.getTraceRepository();
            AnalysisRepository analysisRepository = db.getAnalysisRepository();
            modelRepository.write(TestData.MODEL_1);
            traceRepository.write(TestData.TRACE_1);
            traceRepository.write(TestData.TRACE_INVALID_ITERATION);
            analysisRepository.write(TestData.PROP_MODEL_1);
            analysisRepository.write(TestData.PROP_MODEL_1_S0);
            analysisRepository.write(TestData.PROP_TRACE_1);
            analysisRepository.write(TestData.PROP_TRACE_INVALID_ITERATION);

            assertTrue(analysisRepository.exists(TestData.MODEL_1.getIdentifier()));
            assertTrue(analysisRepository.exists(TestData.MODEL_1.getScenario("S0").getIdentifier()));
            assertTrue(analysisRepository.exists(TestData.TRACE_1.getIdentifier()));
            assertFalse(analysisRepository.exists(TestData.TRACE_3.getIdentifier()));

            AnalysisResult trace1 = analysisRepository.get(TestData.TRACE_1.getIdentifier());
            AnalysisResult invalidTrace = analysisRepository.get(TestData.TRACE_INVALID_ITERATION.getIdentifier());

            TestUtils.assertPropertyEquals(TestData.PROP_MODEL_1.getProperties(),
                    trace1.getModelProperties().getProperties());
            TestUtils.assertPropertyEquals(TestData.PROP_MODEL_1.getProperties(),
                    trace1.getModelProperties().getProperties());
            TestUtils.assertPropertyEquals(TestData.PROP_MODEL_1_S0.getProperties(),
                    trace1.getScenarioProperties().getProperties());
            TestUtils.assertPropertyEquals(TestData.PROP_MODEL_1_S0.getProperties(),
                    invalidTrace.getScenarioProperties().getProperties());
            TestUtils.assertPropertyEquals(TestData.PROP_TRACE_1.getPropertiesForIteration(),
                    trace1.getTraceProperties().getPropertiesForIteration());

        }
    }

    @Test
    default void shouldStreamAnalysisResults() throws IOException {
        try (DatabaseAdapter db = provideDataSource()) {
            ModelRepository modelRepository = db.getModelRepository();
            TraceRepository traceRepository = db.getTraceRepository();
            AnalysisRepository analysisRepository = db.getAnalysisRepository();
            modelRepository.write(TestData.MODEL_1);
            traceRepository.write(TestData.TRACE_1);
            traceRepository.write(TestData.TRACE_INVALID_ITERATION);
            analysisRepository.write(TestData.PROP_MODEL_1);
            analysisRepository.write(TestData.PROP_MODEL_1_S0);
            analysisRepository.write(TestData.PROP_TRACE_1);
            analysisRepository.write(TestData.PROP_TRACE_INVALID_ITERATION);

            List<AnalysisResult> analysisResults = analysisRepository.getAll().collect(Collectors.toList());
            assertEquals(2, analysisResults.size());
        }
    }

    @Test
    default void shouldReturnKeyInfo() throws IOException {
        try (DatabaseAdapter db = provideDataSource()) {
            ModelRepository modelRepository = db.getModelRepository();
            TraceRepository traceRepository = db.getTraceRepository();
            AnalysisRepository analysisRepository = db.getAnalysisRepository();
            modelRepository.write(TestData.MODEL_1);
            traceRepository.write(TestData.TRACE_1);
            traceRepository.write(TestData.TRACE_INVALID_ITERATION);
            analysisRepository.write(TestData.PROP_MODEL_1);
            analysisRepository.write(TestData.PROP_MODEL_1_S0);
            analysisRepository.write(TestData.PROP_TRACE_1);
            analysisRepository.write(TestData.PROP_TRACE_INVALID_ITERATION);

            KeyInfo keyInfo = analysisRepository.getKeyInfo();

            assertEquals(4, keyInfo.getModelKeys().size());
            assertTrue(keyInfo.getModelKeys().containsAll(List.of(
                    TestData.M_BOUNDED_DOUBLE_KEY,
                    TestData.M_BOUNDED_INT_KEY,
                    TestData.M_HALF_OPEN_LONG,
                    TestData.M_UNBOUNDED_INT_KEY)));

            assertEquals(1, keyInfo.getScenarioKeys().size());
            assertTrue(keyInfo.getScenarioKeys().contains(TestData.S_BOOLEAN_KEY));

            assertEquals(3, keyInfo.getTraceKeys().size());
            assertTrue(keyInfo.getTraceKeys().containsAll(List.of(
                    TestData.T_A_KEY,
                    TestData.T_B_KEY,
                    TestData.T_C_KEY)));

        }
    }

    @Test
    default void shouldThrowForWriteIfModelNotPresent() throws IOException {
        try (DatabaseAdapter db = provideDataSource()) {
            AnalysisRepository analysisRepository = db.getAnalysisRepository();
            assertThrows(NoSuchElementException.class, () -> analysisRepository.write(TestData.PROP_TRACE_1));
        }
    }

    @Test
    default void shouldThrowForReadIfModelPropertiesNotPresent() throws IOException {
        try (DatabaseAdapter db = provideDataSource()) {
            ModelRepository modelRepository = db.getModelRepository();
            TraceRepository traceRepository = db.getTraceRepository();
            AnalysisRepository analysisRepository = db.getAnalysisRepository();
            modelRepository.write(TestData.MODEL_1);
            traceRepository.write(TestData.TRACE_1);
            traceRepository.write(TestData.TRACE_INVALID_ITERATION);

            analysisRepository.write(TestData.PROP_TRACE_1);
            assertThrows(NoSuchElementException.class, () -> analysisRepository.get(TestData.PROP_TRACE_1.getIdentifier()));
        }
    }

}
