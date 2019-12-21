package de.rwth.swc.coffee4j.evaluation.analysis.trace;

import de.rwth.swc.coffee4j.evaluation.analysis.PropertyKey;
import de.rwth.swc.coffee4j.evaluation.model.TestModel;
import de.rwth.swc.coffee4j.evaluation.model.TestScenario;
import de.rwth.swc.coffee4j.evaluation.trace.Trace;
import de.rwth.swc.coffee4j.evaluation.trace.TraceIteration;
import de.rwth.swc.coffee4j.evaluation.utils.CombinationUtil;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

/**
 * {@link TraceAnalysis} that analyzes a trace iteration for the amount of test cases that are forbidden by
 * constraints.
 * <p>
 * It computes two different metrics. First the absolute number of test cases with with a contained forbidden
 * combinations. Additionally, this value is put into context by providing the percentage of all test cases which are
 * constrained. Generally, this metric is a measure of how well an algorithm can handle constraints. A high percentage
 * of invalid test cases indicates potential for improvement in this regard.
 */
public class NumberOfInvalidTestCasesAnalysis implements TraceAnalysis {

    private final String numberOfMatchingTestCasesKey;
    private final String percentageOfMatchingTestCasesKey;

    /**
     * Constructor.
     *
     * @param numberOfMatchingTestCasesKey     the analysis key for the absolute number. It must not be {@code null} .
     * @param percentageOfMatchingTestCasesKey the analysis key for the percentage. It must not be {@code null} .
     */
    public NumberOfInvalidTestCasesAnalysis(String numberOfMatchingTestCasesKey, String percentageOfMatchingTestCasesKey) {
        this.numberOfMatchingTestCasesKey = Objects.requireNonNull(numberOfMatchingTestCasesKey);
        this.percentageOfMatchingTestCasesKey = Objects.requireNonNull(percentageOfMatchingTestCasesKey);
    }

    @Override
    public Map<PropertyKey, Number> analyze(TestModel model, TestScenario testScenario, Trace trace, TraceIteration iteration) {
        Collection<int[]> constraints = model.getConstraintsForScenario(testScenario).values();
        int numberOfInvalidTestCases = 0;
        for (int[] testCase : iteration.getTestCases()) {
            if (constraints.stream().anyMatch(fault -> CombinationUtil.contains(testCase, fault))) {
                numberOfInvalidTestCases++;
            }
        }

        return Map.of(PropertyKey.positiveInteger(numberOfMatchingTestCasesKey),
                numberOfInvalidTestCases,
                PropertyKey.percentage(percentageOfMatchingTestCasesKey),
                (double) numberOfInvalidTestCases / iteration.getTestCases().size());

    }
}
