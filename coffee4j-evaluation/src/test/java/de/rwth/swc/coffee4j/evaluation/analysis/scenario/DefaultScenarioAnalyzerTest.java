package de.rwth.swc.coffee4j.evaluation.analysis.scenario;

import de.rwth.swc.coffee4j.evaluation.TestData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class DefaultScenarioAnalyzerTest {

    @Test
    void shouldCreateNonEmptyAnalyzer() {

        ScenarioAnalyzer analyzer = new DefaultScenarioAnalyzer();
        ScenarioProperties properties = analyzer.analyze(TestData.MODEL_1, TestData.MODEL_1.getScenario("S0"));
        assertEquals(TestData.MODEL_1.getScenario("S0").getIdentifier(), properties.getIdentifier());
        assertFalse(properties.getProperties().isEmpty());

    }

}