package de.rwth.swc.coffee4j.evaluation.analysis.trace;

import de.rwth.swc.coffee4j.evaluation.TestData;
import de.rwth.swc.coffee4j.evaluation.TestUtils;
import de.rwth.swc.coffee4j.evaluation.analysis.PropertyKey;
import de.rwth.swc.coffee4j.evaluation.model.TestModel;
import de.rwth.swc.coffee4j.evaluation.trace.Trace;
import de.rwth.swc.coffee4j.evaluation.trace.TraceIteration;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

class AssumptionsAnalysisTest {

    private static Stream<Arguments> testCaseProvider() {
        return Stream.of(
                Arguments.of(TestData.MODEL_1, "S0", TestData.TRACE_1,
                        List.of(
                                Map.of(PropertyKey.bool("A1"), 1, PropertyKey.bool("A2"), 0),
                                Map.of(),
                                Map.of(),
                                Map.of()))
        );
    }

    @ParameterizedTest
    @MethodSource("testCaseProvider")
    void shouldComputeExpectedResults(TestModel model, String scenarioName, Trace trace,
                                      List<Map<PropertyKey, Number>> expectedResults) {

        TraceAnalysis analysis = new AssumptionsAnalysis();
        List<TraceIteration> traceIterations = trace.getTraceIterations();
        for (int i = 0; i < traceIterations.size(); i++) {
            TraceIteration traceIteration = traceIterations.get(i);
            Map<PropertyKey, Number> results = analysis.analyze(model,
                    model.getScenario(scenarioName), trace, traceIteration);
            TestUtils.assertPropertyEquals(expectedResults.get(i), results);
        }

    }
}