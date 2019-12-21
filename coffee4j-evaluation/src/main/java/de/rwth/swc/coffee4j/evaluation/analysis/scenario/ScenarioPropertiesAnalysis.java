package de.rwth.swc.coffee4j.evaluation.analysis.scenario;

import de.rwth.swc.coffee4j.evaluation.analysis.PropertyKey;
import de.rwth.swc.coffee4j.evaluation.model.TestModel;
import de.rwth.swc.coffee4j.evaluation.model.TestScenario;

import java.util.Map;

/**
 * {@link ScenarioAnalysis} that copies some basic properties from the scenario to the properties.
 * <p>
 * This analysis does not contain any complex logic, it just copies values from the scenario to the result. This allows
 * us to include these values when exporting the {@link de.rwth.swc.coffee4j.evaluation.analysis.AnalysisResult} or
 * otherwise work with the {@link ScenarioProperties}
 */
public class ScenarioPropertiesAnalysis implements ScenarioAnalysis {
    @Override
    public Map<PropertyKey, Number> analyze(TestModel model, TestScenario testScenario) {
        return Map.of(
                PropertyKey.positiveInteger("Strength"), testScenario.getStrength(),
                PropertyKey.positiveInteger("NumberOfFaults"), testScenario.getFaults().size()
        );
    }
}
