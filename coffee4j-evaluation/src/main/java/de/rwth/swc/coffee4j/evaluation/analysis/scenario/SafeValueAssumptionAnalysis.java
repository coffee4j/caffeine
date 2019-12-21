package de.rwth.swc.coffee4j.evaluation.analysis.scenario;

import de.rwth.swc.coffee4j.evaluation.analysis.PropertyKey;
import de.rwth.swc.coffee4j.evaluation.model.TestModel;
import de.rwth.swc.coffee4j.evaluation.model.TestScenario;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


/**
 * {@link ScenarioAnalysis} that calculates whether a {@link TestScenario} satisfies the safe-value assumption.
 * <p>
 * The safe-value assumptions assumes that each parameter has a value that is not part of any failure inducing
 * combination.
 * <p>
 * Example:
 * <p>
 * Parameters: [2, 2, 2] Faults: [0, -1, -1] [-1, 0, 0] | Satisfies the assumption because [1, 1, 1] are safe values.
 * <p>
 * Parameters: [2, 2, 1] Faults: [0, -1, -1] [1, 0, 0] | Does not satisfy the assumption, because parameter 0 has no
 * safe value.
 */
public final class SafeValueAssumptionAnalysis implements ScenarioAnalysis {

    private final String key;

    /**
     * Constructor.
     *
     * @param key the analysis key. It must not be {@code null}.
     */
    public SafeValueAssumptionAnalysis(String key) {
        this.key = Objects.requireNonNull(key);
    }

    @Override
    public Map<PropertyKey, Number> analyze(TestModel model, TestScenario testScenario) {
        boolean hasSafeValues = true;
        List<Set<Integer>> safeValues = new ArrayList<>(model.getParameters().length);
        for (int parameter = 0; parameter < model.getParameters().length; parameter++) {

            safeValues.add(new HashSet<>());
            safeValues.get(parameter).addAll(IntStream.range(0, model.getParameters()[parameter])
                    .boxed().collect(Collectors.toList()));

        }
        for (int[] cause : model.getFaultsForScenario(testScenario).values()) {
            for (int parameter = 0; parameter < cause.length; parameter++) {
                safeValues.get(parameter).remove(cause[parameter]);
            }
        }

        for (int[] cause : model.getConstraintsForScenario(testScenario).values()) {
            for (int parameter = 0; parameter < cause.length; parameter++) {
                safeValues.get(parameter).remove(cause[parameter]);
            }
        }

        for (Set<Integer> safeValue : safeValues) {
            if (safeValue.isEmpty()) {
                hasSafeValues = false;
                break;
            }
        }

        return Map.of(PropertyKey.bool(key), hasSafeValues ? 1 : 0);
    }

}
