package de.rwth.swc.coffee4j.evaluation.analysis.scenario;

import de.rwth.swc.coffee4j.evaluation.analysis.PropertyKey;
import de.rwth.swc.coffee4j.evaluation.model.TestModel;
import de.rwth.swc.coffee4j.evaluation.model.TestScenario;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Analyzer that uses given {@link ScenarioAnalysis} to compute the {@link ScenarioProperties} for a {@link
 * TestScenario}.
 * <p>
 * It delegates any call to {@link #analyze(TestModel, TestScenario)} to the internal {@link ScenarioAnalysis} instances
 * and aggregates their results. If one or more analyses have overlapping keys, then the result contains only one of
 * them arbitrarily. Therefore, care has to be taken to have non-overlapping keys.
 */
public class ScenarioAnalyzer {

    private final Collection<ScenarioAnalysis> analyses;

    /**
     * Constructor.
     *
     * @param analyses a collection of the underlying analyses. It must not be {@code null}, nor contain {@code null}.
     */
    ScenarioAnalyzer(Collection<ScenarioAnalysis> analyses) {
        this.analyses = Objects.requireNonNull(analyses);
    }

    /**
     * Analyze the given scenario.
     *
     * @param model        the model to analyze. It must not be {@code null}.
     * @param testScenario the scenario to analyze. It must not be {@code null}.
     * @return the computed metrics
     */
    public ScenarioProperties analyze(TestModel model, TestScenario testScenario) {
        Objects.requireNonNull(model);
        Objects.requireNonNull(testScenario);
        Map<PropertyKey, Number> properties = new HashMap<>();
        for (ScenarioAnalysis analysis : analyses) {
            properties.putAll(analysis.analyze(model, testScenario));
        }
        return new ScenarioProperties(testScenario.getIdentifier(), properties);
    }

}
