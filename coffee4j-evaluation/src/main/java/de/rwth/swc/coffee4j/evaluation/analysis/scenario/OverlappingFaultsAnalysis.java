package de.rwth.swc.coffee4j.evaluation.analysis.scenario;

import de.rwth.swc.coffee4j.evaluation.analysis.PropertyKey;
import de.rwth.swc.coffee4j.evaluation.model.TestModel;
import de.rwth.swc.coffee4j.evaluation.model.TestScenario;
import de.rwth.swc.coffee4j.evaluation.utils.CombinationUtil;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

/**
 * {@link ScenarioAnalysis} that counts overlapping faults in a scenario.
 * <p>
 * Two faults are overlapping when they share a common parameter value. For example (1, 1, -) overlaps (1, 0, 0) while
 * (0, 0, 1) does not.
 * <p>
 * The computed number can only be used as an ordering and not an exact amount of overlapping faults. It may count pairs
 * twice but keep the general ordering.
 */
public class OverlappingFaultsAnalysis implements ScenarioAnalysis {

    private final String numberOfOverlappingFaultsKey;

    /**
     * Constructor.
     *
     * @param numberOfOverlappingFaultsKey the analysis key. It must not be {@code null}.
     */
    public OverlappingFaultsAnalysis(String numberOfOverlappingFaultsKey) {
        this.numberOfOverlappingFaultsKey = Objects.requireNonNull(numberOfOverlappingFaultsKey);
    }

    @Override
    public Map<PropertyKey, Number> analyze(TestModel model, TestScenario testScenario) {

        Collection<int[]> faults = model.getFaultsForScenario(testScenario).values();

        int numberOfOverlappingFaults = 0;
        for (int[] fault : faults) {
            for (int[] other : faults) {
                if (fault != other && CombinationUtil.overlap(fault, other)) {
                    numberOfOverlappingFaults++;
                }
            }
        }

        return Map.of(PropertyKey.positiveInteger(numberOfOverlappingFaultsKey), numberOfOverlappingFaults);
    }
}
