package de.rwth.swc.coffee4j.evaluation.analysis.scenario;

import java.util.List;

/**
 * {@link ScenarioAnalyzer} using a default selection of {@link ScenarioAnalysis}.
 */
public final class DefaultScenarioAnalyzer extends ScenarioAnalyzer {

    private static final String SAFE_VALUE_ASSUMPTION_KEY = "SafeValue";
    private static final String OVERLAPPING_FAULTS_KEY = "Overlap";

    /**
     * Constructor.
     */
    public DefaultScenarioAnalyzer() {
        super(List.of(
                new ScenarioPropertiesAnalysis(),
                new OverlappingFaultsAnalysis(OVERLAPPING_FAULTS_KEY),
                new SafeValueAssumptionAnalysis(SAFE_VALUE_ASSUMPTION_KEY)
        ));
    }
}
