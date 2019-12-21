package de.rwth.swc.coffee4j.evaluation.analysis.model;

import de.rwth.swc.coffee4j.evaluation.TestData;
import de.rwth.swc.coffee4j.evaluation.TestUtils;
import de.rwth.swc.coffee4j.evaluation.analysis.PropertyKey;
import de.rwth.swc.coffee4j.evaluation.model.TestModel;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Map;
import java.util.stream.Stream;

class TotalPossibleCombinationsAnalysisTest {

    private static Stream<Arguments> testCaseProvider() {
        return Stream.of(
                Arguments.of(TestData.MODEL_1, Map.of(PropertyKey.positiveInteger("Key"), 24)),
                Arguments.of(TestData.MODEL_2, Map.of(PropertyKey.positiveInteger("Key"), 10)),
                Arguments.of(TestData.MODEL_EMPTY, Map.of(PropertyKey.positiveInteger("Key"), 8)),
                Arguments.of(new TestModel.Builder("Test")
                        .withParameters(100, 100, 100, 100)
                        .buildModel(), Map.of(PropertyKey.positiveInteger("Key"), 100000000))
        );
    }

    @ParameterizedTest
    @MethodSource("testCaseProvider")
    void shouldCalculateExpectedResults(TestModel model, Map<PropertyKey, Number> expectedResults) {

        ModelAnalysis analysis = new TotalPossibleCombinationsAnalysis("Key");
        Map<PropertyKey, Number> results = analysis.analyze(model);

        TestUtils.assertPropertyEquals(expectedResults, results);
    }

}