package de.rwth.swc.coffee4j.evaluation.analysis.model;

import java.util.List;

/**
 * {@link ModelAnalyzer} using a default selection of {@link ModelAnalysis}.
 */
public final class DefaultModelAnalyzer extends ModelAnalyzer {

    private static final String NUMBER_OF_PARAMETERS_KEY = "NumberOfParameters";
    private static final String TOTAL_POSSIBLE_COMBINATIONS_KEY = "TotalPossibleCombinations";

    /**
     * Constructor.
     */
    public DefaultModelAnalyzer() {
        super(List.of(
                new ModelPropertiesAnalysis(NUMBER_OF_PARAMETERS_KEY),
                new TotalPossibleCombinationsAnalysis(TOTAL_POSSIBLE_COMBINATIONS_KEY)
        ));
    }
}
