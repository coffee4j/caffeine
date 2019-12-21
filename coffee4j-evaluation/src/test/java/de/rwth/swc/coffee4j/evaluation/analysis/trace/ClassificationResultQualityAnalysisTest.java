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

class ClassificationResultQualityAnalysisTest {

    private static Stream<Arguments> testCaseProvider() {
        return Stream.of(
                Arguments.of(TestData.MODEL_1, "S0", TestData.TRACE_1,
                        List.of(Map.of(PropertyKey.percentage("precision"), 0,
                                        PropertyKey.percentage("recall"), 0,
                                        PropertyKey.percentage("fscore"), 0),
                                Map.of(PropertyKey.percentage("precision"), 1,
                                        PropertyKey.percentage("recall"), 0,
                                        PropertyKey.percentage("fscore"), 0),
                                Map.of(PropertyKey.percentage("precision"), 1,
                                        PropertyKey.percentage("recall"), 0,
                                        PropertyKey.percentage("fscore"), 0),
                                Map.of(PropertyKey.percentage("precision"), 1,
                                        PropertyKey.percentage("recall"), 0,
                                        PropertyKey.percentage("fscore"), 0))),
                Arguments.of(TestData.MODEL_LARGE, "S0", TestData.TRACE_LARGE,
                        List.of(Map.of(
                                PropertyKey.percentage("precision"), 0.40789473684210525,
                                PropertyKey.percentage("recall"), 1,
                                PropertyKey.percentage("fscore"), 0.5794392523364486))),
                Arguments.of(TestData.MODEL_EMPTY_SCENARIO, "S0", TestData.TRACE_INVALID_ITERATION,
                        List.of(Map.of(PropertyKey.percentage("precision"), 1,
                                PropertyKey.percentage("recall"), 1,
                                PropertyKey.percentage("fscore"), 1)))
        );
    }

    @ParameterizedTest
    @MethodSource("testCaseProvider")
    void shouldComputeExpectedResults(TestModel model, String scenarioName, Trace trace,
                                      List<Map<PropertyKey, Number>> expectedResults) {

        TraceAnalysis analysis = new ClassificationResultQualityAnalysis("precision", "recall", "fscore");
        List<TraceIteration> traceIterations = trace.getTraceIterations();
        for (int i = 0; i < traceIterations.size(); i++) {
            TraceIteration traceIteration = traceIterations.get(i);
            Map<PropertyKey, Number> results = analysis.analyze(model,
                    model.getScenario(scenarioName), trace, traceIteration);
            TestUtils.assertPropertyEquals(expectedResults.get(i), results);
        }

    }

}