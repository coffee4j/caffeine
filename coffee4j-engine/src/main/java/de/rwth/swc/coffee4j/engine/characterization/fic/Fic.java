package de.rwth.swc.coffee4j.engine.characterization.fic;

import de.rwth.swc.coffee4j.engine.TestModel;
import de.rwth.swc.coffee4j.engine.TestResult;
import de.rwth.swc.coffee4j.engine.characterization.FaultCharacterizationAlgorithm;
import de.rwth.swc.coffee4j.engine.characterization.FaultCharacterizationAlgorithmFactory;
import de.rwth.swc.coffee4j.engine.characterization.FaultCharacterizationConfiguration;
import de.rwth.swc.coffee4j.engine.util.IntArrayWrapper;
import de.rwth.swc.coffee4j.engine.util.Preconditions;

import java.util.*;
import java.util.stream.Collectors;

/**
 * The implementation of the FIC fault characterization algorithm as described in "Characterizing Failure-Causing
 * Parameter Interactions by Adaptive Testing".
 * <p>
 * The algorithm inspects each failed test case in isolation. The first step of fault characterization is the
 * localization of one or more fixed parameters. This is done by linearly searching over the failed test case, always
 * mutating one parameter value. If the mutated test is still failing, then the parameter is most likely not responsible
 * for the failure. All paramters that lead to successful test cases, however, form a candidate failure inducing
 * combination. The algorithm then checks if the original test case contains another fault by removing the previously
 * found one. This is repeated for each failed test case.
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
public class Fic implements FaultCharacterizationAlgorithm {

    private final TestModel model;
    private final Set<Map.Entry<int[], TestResult>> remainingTestCases = new HashSet<>();
    private final Set<IntArrayWrapper> failureInducingCombinations = new HashSet<>();
    private Mode mode = Mode.INIT;
    private NonOverlappingCombinationFinder currentFinder;

    /**
     * Constructor.
     *
     * @param configuration the configuration. May not be {@code null}.
     */
    public Fic(FaultCharacterizationConfiguration configuration) {
        this.model = Preconditions.notNull(configuration).getTestModel();
    }

    @Override
    public List<int[]> computeNextTestInputs(Map<int[], TestResult> testResults) {
        if (mode == Mode.INIT) {
            for (Map.Entry<int[], TestResult> entry : testResults.entrySet()) {
                if (entry.getValue().isUnsuccessful()) {
                    remainingTestCases.add(entry);
                }
            }
            mode = Mode.RUN;
        }

        Map.Entry<int[], TestResult> oldTestResult = testResults.entrySet().stream().findFirst().orElseThrow();
        Optional<int[]> newTestCase = currentFinder != null ? currentFinder.runIteration(oldTestResult.getValue())
                : Optional.empty();
        while (newTestCase.isEmpty()) {
            if (currentFinder != null) {
                failureInducingCombinations.addAll(currentFinder.getInteractions().stream()
                        .map(IntArrayWrapper::wrap).collect(Collectors.toList()));
            }
            if (remainingTestCases.isEmpty()) {
                break;
            }
            Map.Entry<int[], TestResult> seedTestCase = remainingTestCases.stream().findAny().get();
            remainingTestCases.remove(seedTestCase);
            currentFinder = new NonOverlappingCombinationFinder(seedTestCase.getKey(),
                    model, provideFixedVariableFinder());
            newTestCase = currentFinder.runIteration(seedTestCase.getValue());
        }

        return newTestCase.map(Collections::singletonList).orElse(Collections.emptyList());

    }

    public FaultCharacterizationAlgorithmFactory fic() {
        return Fic::new;
    }

    @Override
    public List<int[]> computeFailureInducingCombinations() {
        return failureInducingCombinations.stream().map(IntArrayWrapper::getArray).collect(Collectors.toList());
    }

    protected FixedVariableFinderFactory provideFixedVariableFinder() {
        return SimpleFixedVariableFinder::new;
    }

    private enum Mode {
        INIT,
        RUN
    }

}
