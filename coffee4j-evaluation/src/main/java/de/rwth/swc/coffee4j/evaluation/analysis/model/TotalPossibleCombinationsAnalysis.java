package de.rwth.swc.coffee4j.evaluation.analysis.model;

import de.rwth.swc.coffee4j.evaluation.analysis.PropertyKey;
import de.rwth.swc.coffee4j.evaluation.model.TestModel;

import java.util.Map;
import java.util.Objects;
import java.util.stream.IntStream;

/**
 * {@link ModelAnalysis} that computes the number of possible combinations of the input model.
 * <p>
 * It does not include any constraint handling. Each combination is assumed to be possible. Therefore, the result is
 * simply the product of all possible parameter values.
 */
public final class TotalPossibleCombinationsAnalysis implements ModelAnalysis {

    private final String key;

    /**
     * Constructor.
     *
     * @param key the storage key for this metric. It must not be {@code null}.
     */
    public TotalPossibleCombinationsAnalysis(String key) {
        this.key = Objects.requireNonNull(key);
    }

    @Override
    public Map<PropertyKey, Number> analyze(TestModel model) {
        Objects.requireNonNull(model);
        double totalCombinations = IntStream.of(model.getParameters())
                .mapToDouble(Double::valueOf)
                .reduce(1, (a, b) -> a * b);
        return Map.of(new PropertyKey(key, Integer.class, 0, Double.POSITIVE_INFINITY), totalCombinations);
    }

}
