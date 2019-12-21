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
 * {@link TraceAnalysis} that analyzes a trace iteration for the amount of test cases that contain multiple faults.
 * <p>
 * It computes two different metrics. First the absolute number of test cases with at least two faults are given.
 * Additionally, this value is put into context by providing the percentage of all test cases which contain more than
 * one fault. Generally, these test cases are one of the difficult challenges of fault characterization because only one
 * fault is apparent in the result while the other is hidden.
 */
public class MultipleFaultsInTestCaseAnalysis implements TraceAnalysis {

    private final String numberOfMatchingTestCasesKey;
    private final String percentageOfMatchingTestCasesKey;

    /**
     * Constructor.
     *
     * @param numberOfMatchingTestCasesKey     the analysis key for the absolute number. It must not be {@code null} .
     * @param percentageOfMatchingTestCasesKey the analysis key for the percentage. It must not be {@code null} .
     */
    public MultipleFaultsInTestCaseAnalysis(String numberOfMatchingTestCasesKey, String percentageOfMatchingTestCasesKey) {
        this.numberOfMatchingTestCasesKey = Objects.requireNonNull(numberOfMatchingTestCasesKey);
        this.percentageOfMatchingTestCasesKey = Objects.requireNonNull(percentageOfMatchingTestCasesKey);
    }

    @Override
    public Map<PropertyKey, Number> analyze(TestModel model, TestScenario testScenario, Trace trace, TraceIteration iteration) {
        Collection<int[]> faults = model.getFaultsForScenario(testScenario).values();
        int numberOfTestCasesWithMoreThanOneFault = 0;
        for (int[] testCase : iteration.getTestCases()) {
            if (faults.stream().filter(fault -> CombinationUtil.contains(testCase, fault)).count() > 1) {
                numberOfTestCasesWithMoreThanOneFault++;
            }
        }

        return Map.of(PropertyKey.positiveInteger(numberOfMatchingTestCasesKey),
                numberOfTestCasesWithMoreThanOneFault,
                PropertyKey.percentage(percentageOfMatchingTestCasesKey),
                (double) numberOfTestCasesWithMoreThanOneFault / iteration.getTestCases().size());
    }
}
