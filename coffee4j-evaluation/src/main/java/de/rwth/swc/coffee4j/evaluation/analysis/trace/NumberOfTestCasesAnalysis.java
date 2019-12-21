package de.rwth.swc.coffee4j.evaluation.analysis.trace;

import de.rwth.swc.coffee4j.evaluation.analysis.PropertyKey;
import de.rwth.swc.coffee4j.evaluation.model.TestModel;
import de.rwth.swc.coffee4j.evaluation.model.TestScenario;
import de.rwth.swc.coffee4j.evaluation.trace.Trace;
import de.rwth.swc.coffee4j.evaluation.trace.TraceIteration;
import de.rwth.swc.coffee4j.evaluation.utils.IntArrayWrapper;

import java.util.Map;
import java.util.Objects;

/**
 * {@link TraceAnalysis} that measures the number of test cases in an iteration. Calculates the number of used test
 * cases and unique used test cases.
 */
public final class NumberOfTestCasesAnalysis implements TraceAnalysis {

    private final String numberOfTestCasesKey;
    private final String numberOfUniqueTestCasesKey;

    /**
     * Constructor.
     *
     * @param numberOfTestCasesKey       The key for the number of test cases metric value. It must not be {@code
     *                                   null}.
     * @param numberOfUniqueTestCasesKey The key for the number of unique test cases metric value. It must not be {@code
     *                                   null}.
     */
    NumberOfTestCasesAnalysis(String numberOfTestCasesKey, String numberOfUniqueTestCasesKey) {
        this.numberOfTestCasesKey = Objects.requireNonNull(numberOfTestCasesKey);
        this.numberOfUniqueTestCasesKey = Objects.requireNonNull(numberOfUniqueTestCasesKey);
    }

    @Override
    public Map<PropertyKey, Number> analyze(TestModel model, TestScenario testScenario, Trace trace, TraceIteration iteration) {
        Objects.requireNonNull(model);
        Objects.requireNonNull(testScenario);
        Objects.requireNonNull(trace);
        Objects.requireNonNull(iteration);

        return Map.of(
                PropertyKey.positiveInteger(numberOfTestCasesKey), iteration.getTestCases().size(),
                PropertyKey.positiveInteger(numberOfUniqueTestCasesKey), iteration.getTestCases()
                        .stream().map(IntArrayWrapper::wrap).distinct().count());
    }

}
