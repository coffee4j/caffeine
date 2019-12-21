package de.rwth.swc.coffee4j.engine.characterization.fic;

import de.rwth.swc.coffee4j.engine.characterization.FaultCharacterizationConfiguration;

/**
 * The implementation of the FIC_BS fault characterization algorithm as described in "Characterizing Failure-Causing
 * Parameter Interactions by Adaptive Testing".
 * It uses binary search instead of a simple linear search to find fixed parameters of faulty interactions.
 * Otherwise functions exactly the same as {@link Fic}.
 * <p>
 * Important Information:
 * <ul>
 *     <li>Linearly searches the failed test cases for fixed parameters
 *     <li>Relies on the framework for test case caching, i.e. may generate duplicate test cases.
 *     <li>Assumes that no new test cases are uncovered during the localization of fixed parameters
 *     <li>Assumes that faults are non-overlapping
 *     <li>Does not support for constraints
 * </ul>
 */
public class FicBS extends Fic {

    /**
     * Constructor.
     *
     * @param configuration the configuration. May not be {@code null}.
     */
    public FicBS(FaultCharacterizationConfiguration configuration) {
        super(configuration);
    }

    @Override
    protected FixedVariableFinderFactory provideFixedVariableFinder() {
        return BinarySearchFixedVariableFinder::new;
    }
}
