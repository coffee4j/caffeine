package de.rwth.swc.coffee4j.evaluation.analysis.trace;

import java.util.List;

/**
 * Default implementation for a {@link TraceAnalyzer} which provides sensible keys for all available analyses.
 */
public final class DefaultTraceAnalyzer extends TraceAnalyzer {

    private static final String EXECUTION_TIME_KEY = "ExecutionTime";
    private static final String NUMBER_OF_RESULTS_KEY = "NumberOfResults";
    private static final String NUMBER_OF_TEST_CASES_KEY = "NumberOfTestCases";
    private static final String NUMBER_OF_UNIQUE_TEST_CASES_KEY = "NumberOfUniqueTestCases";
    private static final String SUPER_PRECISION_KEY = "SuperPrecision";
    private static final String PRECISION_KEY = "Precision";
    private static final String RECALL_KEY = "Recall";
    private static final String F_SCORE_KEY = "FScore";
    private static final String CLASS_PRECISION_KEY = "ClassPrecision";
    private static final String CLASS_RECALL_KEY = "ClassRecall";
    private static final String CLASS_F_SCORE_KEY = "ClassFScore";
    private static final String MULTIPLE_FAULTS_NUMBER_KEY = "NumMultipleFaults";
    private static final String MULTIPLE_FAULTS_PERCENTAGE_KEY = "PctMultipleFaults";
    private static final String INVALID_TC_NUMBER_KEY = "NumInvalidTests";
    private static final String INVALID_TC_PERCENTAGE_KEY = "PctInvalidTests";

    /**
     * Constructor.
     */
    public DefaultTraceAnalyzer() {
        super(List.of(
                new ExecutionTimeAnalysis(EXECUTION_TIME_KEY),
                new ClassificationResultQualityAnalysis(CLASS_PRECISION_KEY, CLASS_RECALL_KEY, CLASS_F_SCORE_KEY),
                new NumberOfTestCasesAnalysis(NUMBER_OF_TEST_CASES_KEY,
                        NUMBER_OF_UNIQUE_TEST_CASES_KEY),
                new NumberOfResultsAnalysis(NUMBER_OF_RESULTS_KEY),
                new ResultQualityAnalysis(SUPER_PRECISION_KEY, PRECISION_KEY, RECALL_KEY, F_SCORE_KEY),
                new MultipleFaultsInTestCaseAnalysis(MULTIPLE_FAULTS_NUMBER_KEY, MULTIPLE_FAULTS_PERCENTAGE_KEY),
                new NumberOfInvalidTestCasesAnalysis(INVALID_TC_NUMBER_KEY, INVALID_TC_PERCENTAGE_KEY),
                new AssumptionsAnalysis()));
    }
}
