package de.rwth.swc.coffee4j.evaluation.analysis.scenario;

import de.rwth.swc.coffee4j.evaluation.TestData;
import de.rwth.swc.coffee4j.evaluation.TestUtils;
import de.rwth.swc.coffee4j.evaluation.analysis.PropertyKey;
import de.rwth.swc.coffee4j.evaluation.model.TestModel;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Map;
import java.util.stream.Stream;

class SafeValueAssumptionAnalysisTest {

    private static Stream<Arguments> testCaseProvider() {
        return Stream.of(
                Arguments.of(TestData.MODEL_1, "S0",
                        Map.of(PropertyKey.positiveInteger("Key"), 1)),
                Arguments.of(TestData.MODEL_EMPTY_SCENARIO, "S0",
                        Map.of(PropertyKey.positiveInteger("Key"), 1)),
                Arguments.of(new TestModel.Builder("Test")
                                .withParameters(2, 2, 2)
                                .withConstraint("C1", 0, 0, 0)
                                .withConstraint("C2", 1, 1, 1)
                                .scenario("S0")
                                .withFaults("C1", "C2")
                                .buildScenario()
                                .buildModel(), "S0",
                        Map.of(PropertyKey.positiveInteger("Key"), 0)),
                Arguments.of(new TestModel.Builder("Test")
                                .withParameters(2, 2, 2)
                                .withConstraint("C1", 0, 0, 0)
                                .withConstraint("C2", 1, 1, 0)
                                .scenario("S0")
                                .withFaults("C1", "C2")
                                .buildScenario()
                                .buildModel(), "S0",
                        Map.of(PropertyKey.positiveInteger("Key"), 0)),
                Arguments.of(new TestModel.Builder("Test")
                                .withParameters(3, 3, 3)
                                .withConstraint("C1", 0, 0, 0)
                                .withConstraint("C2", 1, 1, 0)
                                .scenario("S0")
                                .withFaults("C1", "C2")
                                .buildScenario()
                                .buildModel(), "S0",
                        Map.of(PropertyKey.positiveInteger("Key"), 1)),
                Arguments.of(new TestModel.Builder("Test")
                                .withParameters(2, 2, 2)
                                .withConstraint("C1", 0, 0, 0)
                                .withConstraint("C2", 1, 1, 0)
                                .scenario("S0")
                                .withFault("C1")
                                .withConstraint("C2")
                                .buildScenario()
                                .buildModel(), "S0",
                        Map.of(PropertyKey.positiveInteger("Key"), 0))
        );
    }

    @ParameterizedTest
    @MethodSource("testCaseProvider")
    void shouldCalculateExpectedResults(TestModel model, String scenarioName,
                                        Map<PropertyKey, Number> expectedResults) {

        ScenarioAnalysis analysis = new SafeValueAssumptionAnalysis("Key");
        Map<PropertyKey, Number> results = analysis.analyze(model, model.getScenario(scenarioName));

        TestUtils.assertPropertyEquals(expectedResults, results);
    }

}