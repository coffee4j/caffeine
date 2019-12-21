package de.rwth.swc.coffee4j.engine.characterization.ict;

import de.rwth.swc.coffee4j.engine.TestModel;
import de.rwth.swc.coffee4j.engine.TestResult;
import de.rwth.swc.coffee4j.engine.characterization.FaultCharacterizationAlgorithm;
import de.rwth.swc.coffee4j.engine.characterization.FaultCharacterizationConfiguration;
import de.rwth.swc.coffee4j.engine.generator.aetg.AetgSatAlgorithm;
import de.rwth.swc.coffee4j.engine.generator.aetg.AetgSatConfiguration;
import de.rwth.swc.coffee4j.engine.util.CombinationUtil;
import de.rwth.swc.coffee4j.engine.util.Preconditions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * An implementation of the framework described in "An interleaving approach to combinatorial testing and
 * failure-inducing interaction identification".
 * <p>
 * It is an interleaved implementation, that combines the combinatorial test generation with the fault characterization.
 * The generation of test cases is handled by {@link de.rwth.swc.coffee4j.engine.generator.aetg.AetgSat}. As soon as a
 * test case is found, Ict begins the localization phase. It starts by generating a number of test cases to extract on
 * or more fixed paramters in the failed test case. This is similar to many other algorithms. But each of these test
 * cases is chosen so that it maximizes the gained coverage. Once a potentiall FIC has been found, the algorithm enters
 * a feedback checking phase. Here the fault is embedded in a user-set number of dissimilar test cases. Should all of
 * these fail, then the fault is accepted, other wise localization starts again with different additional test case.
 * Once a fault is accepted the combination is added as a forbidden constraint to {@link
 * de.rwth.swc.coffee4j.engine.generator.aetg.AetgSat} and normal test case generation resumes.
 * <p>
 * To fit into the given framework this interleaved approach ignores any given test cases at the start. That way it can
 * be called without using a combinatorial test generation algorithm beforehand.
 * <ul>
 *     <li>Because it uses AETGSat the fault characterization is non-deterministic.
 *     <li>Supports constraints.
 *     <li>Assumes that the first step of fixed parameter localization does not uncover any new faults.
 *     <li>Feedback checking as safety net against assumption violations.
 * </ul>
 */
public class Ict implements FaultCharacterizationAlgorithm {

    private static final int DEFAULT_NUMBER_OF_FEEDBACK_CHECKS = 5;

    private final AetgSatAlgorithm aetg;
    private final List<int[]> foundFailureInducingCombinations = new ArrayList<>();
    private final int numberOfFeedbackChecks;
    private final List<int[]> lastFeedback = new ArrayList<>();
    private final List<int[]> lastMutations = new ArrayList<>();

    private State state = State.GENERATION;
    private int[] currentFailure;
    private int[] currentFailureInducingCombination;
    private int feedbackCheckingRound;

    /**
     * Constructor.
     * <p>
     * The given {@link de.rwth.swc.coffee4j.engine.constraint.ConstraintChecker} is ignored while a fresh hard
     * constraint checker is created from the model.
     *
     * @param configuration the configuration. It may not be {@code null}.
     */
    public Ict(FaultCharacterizationConfiguration configuration) {
        this(configuration, DEFAULT_NUMBER_OF_FEEDBACK_CHECKS);
    }

    /**
     * Constructor with a given number of feedback iterations.
     *
     * @param configuration          the configuration. It may not be {@code null}.
     * @param numberOfFeedbackChecks a number describing how many feedback checks should be done when a fault has been
     *                               found. Must be positive.
     */
    public Ict(FaultCharacterizationConfiguration configuration, int numberOfFeedbackChecks) {
        Preconditions.notNull(configuration);
        Preconditions.check(numberOfFeedbackChecks >= 0);
        TestModel model = configuration.getTestModel();
        this.aetg = new AetgSatAlgorithm(AetgSatConfiguration.aetgSatConfiguration().model(model).build());
        this.numberOfFeedbackChecks = numberOfFeedbackChecks;
    }

    @Override
    public List<int[]> computeNextTestInputs(Map<int[], TestResult> testResults) {
        if (testResults.isEmpty()) {
            return generateTestCase();
        }

        if (state == State.GENERATION) {
            Map.Entry<int[], TestResult> testResult = testResults.entrySet().stream().findFirst().get();
            if (testResult.getValue().isSuccessful()) {
                aetg.updateCoverage(testResult.getKey());
                return generateTestCase();
            } else {
                state = State.CHARACTERIZATION;
                currentFailure = testResult.getKey();
                return mutateTestCase(currentFailure);
            }

        } else if (state == State.CHARACTERIZATION) {
            currentFailureInducingCombination = identifyFailureInducingCombination(currentFailure, testResults);
            state = State.FEEDBACK_CHECKING;
            feedbackCheckingRound = 1;
            lastFeedback.clear();
            return getFeedbackTestCase(currentFailure, currentFailureInducingCombination);

        } else {
            Map.Entry<int[], TestResult> testResult = testResults.entrySet().stream().findFirst().get();
            if (testResult.getValue().isUnsuccessful()) {

                if (feedbackCheckingRound > numberOfFeedbackChecks) {
                    addFailureInducingCombination(currentFailureInducingCombination);
                    state = State.GENERATION;
                    return generateTestCase();
                } else {
                    feedbackCheckingRound++;
                    return getFeedbackTestCase(currentFailure, currentFailureInducingCombination);
                }

            } else {
                aetg.updateCoverage(testResult.getKey());
                state = State.CHARACTERIZATION;
                return mutateTestCase(currentFailure);
            }
        }
    }

    private void addFailureInducingCombination(int[] failureInducingCombination) {
        lastMutations.clear();
        foundFailureInducingCombinations.add(failureInducingCombination);
        aetg.addForbiddenCombination(failureInducingCombination);
    }

    private int[] identifyFailureInducingCombination(int[] failure, Map<int[], TestResult> testResults) {
        int[] result = CombinationUtil.emptyCombination(failure.length);
        for (Map.Entry<int[], TestResult> entry : testResults.entrySet()) {
            if (entry.getValue().isSuccessful()) {
                aetg.updateCoverage(entry.getKey());
                for (int i = 0; i < failure.length; i++) {
                    if (entry.getKey()[i] != failure[i]) {
                        result[i] = failure[i];
                    }
                }
            }

        }
        return result;
    }

    private List<int[]> getFeedbackTestCase(int[] failure, int[] failureInducingCombination) {
        int[] testCase = aetg.selectDissimilar(failureInducingCombination, failure, lastFeedback);
        lastFeedback.add(testCase);
        return Collections.singletonList(testCase);
    }

    private List<int[]> mutateTestCase(int[] testCase) {
        List<int[]> result = new ArrayList<>(testCase.length);
        for (int parameter = 0; parameter < testCase.length; parameter++) {
            aetg.getMutatedTestCase(parameter, testCase, lastMutations).ifPresent(result::add);
        }
        lastMutations.addAll(result);
        return result;
    }

    private List<int[]> generateTestCase() {
        return aetg.getNextTestCase().stream().collect(Collectors.toList());
    }

    @Override
    public List<int[]> computeFailureInducingCombinations() {
        return foundFailureInducingCombinations;
    }

    private enum State {
        GENERATION,
        CHARACTERIZATION,
        FEEDBACK_CHECKING
    }
}
