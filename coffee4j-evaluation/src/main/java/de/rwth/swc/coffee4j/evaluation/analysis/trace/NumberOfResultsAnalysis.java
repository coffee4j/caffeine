package de.rwth.swc.coffee4j.evaluation.analysis.trace;

import de.rwth.swc.coffee4j.evaluation.analysis.PropertyKey;
import de.rwth.swc.coffee4j.evaluation.model.TestModel;
import de.rwth.swc.coffee4j.evaluation.model.TestScenario;
import de.rwth.swc.coffee4j.evaluation.trace.Trace;
import de.rwth.swc.coffee4j.evaluation.trace.TraceIteration;

import java.util.Map;
import java.util.Objects;

/**
 * {@link TraceAnalysis} that measures the number of returned failure inducing combinations.
 */
public final class NumberOfResultsAnalysis implements TraceAnalysis {

    private final String key;

    /**
     * Constructor.
     *
     * @param key The key for the number of results metric value. It must not be {@code null}.
     */
    NumberOfResultsAnalysis(String key) {
        this.key = Objects.requireNonNull(key);
    }


    @Override
    public Map<PropertyKey, Number> analyze(TestModel model, TestScenario testScenario, Trace trace, TraceIteration iteration) {
        Objects.requireNonNull(model);
        Objects.requireNonNull(testScenario);
        Objects.requireNonNull(trace);
        Objects.requireNonNull(iteration);
        return Map.of(PropertyKey.positiveInteger(key), iteration.getFailureInducingCombinations().size());
    }

}
