package de.rwth.swc.coffee4j.evaluation.analysis.scenario;

import de.rwth.swc.coffee4j.evaluation.analysis.PropertyKey;
import de.rwth.swc.coffee4j.evaluation.model.TestModel;
import de.rwth.swc.coffee4j.evaluation.model.TestScenario;

import java.util.Map;

/**
 * Analysis that computes a number of metrics for a {@link TestScenario}.
 */
public interface ScenarioAnalysis {

    /**
     * Compute the metrics belonging to this analysis.
     *
     * @param model        the model to be analyzed. It must not be {@code null}.
     * @param testScenario the scenario to be analyzed. It must not be {@code null}.
     * @return the computed metrics
     */
    Map<PropertyKey, Number> analyze(TestModel model, TestScenario testScenario);

}
