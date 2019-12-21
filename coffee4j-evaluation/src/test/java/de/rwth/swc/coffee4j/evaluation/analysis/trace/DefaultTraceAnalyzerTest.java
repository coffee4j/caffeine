package de.rwth.swc.coffee4j.evaluation.analysis.trace;

import de.rwth.swc.coffee4j.evaluation.TestData;
import de.rwth.swc.coffee4j.evaluation.trace.ExecutionState;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DefaultTraceAnalyzerTest {

    @Test
    void shouldCreateNonEmptyAnalyzer() {

        TraceAnalyzer analyzer = new DefaultTraceAnalyzer();
        TraceProperties properties = analyzer.analyze(
                TestData.MODEL_1,
                TestData.MODEL_1.getScenario("S0"),
                TestData.TRACE_1);
        assertEquals(TestData.TRACE_1.getIdentifier(), properties.getIdentifier());
        assertTrue(properties.getPropertiesForIteration().stream()
                .allMatch(s -> s.getState() != ExecutionState.COMPLETED || !s.getProperties().isEmpty()));

    }

}