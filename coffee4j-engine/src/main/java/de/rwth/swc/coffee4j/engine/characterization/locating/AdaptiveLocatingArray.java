package de.rwth.swc.coffee4j.engine.characterization.locating;

import de.rwth.swc.coffee4j.engine.TestModel;
import de.rwth.swc.coffee4j.engine.TestResult;
import de.rwth.swc.coffee4j.engine.characterization.FaultCharacterizationAlgorithm;
import de.rwth.swc.coffee4j.engine.characterization.FaultCharacterizationConfiguration;
import de.rwth.swc.coffee4j.engine.constraint.ConstraintChecker;
import de.rwth.swc.coffee4j.engine.util.CombinationUtil;
import de.rwth.swc.coffee4j.engine.util.Combinator;
import de.rwth.swc.coffee4j.engine.util.IntArrayWrapper;
import de.rwth.swc.coffee4j.engine.util.Preconditions;

import java.util.*;

/**
 * An implementation of the algorithm from "Locating a Faulty Interaction in Pair-Wise Testing".
 * <p>
 * It is an adaptive algorithm that constructs a (1,2) locating array. It classifies each possibly faulty pairwise
 * combination of parameters into equivalence classes according to the initial test set. Two combinations are
 * equivalent when they occur in exactly the same testcases. During characterization the algorithm iteratively generates
 * new test cases to split these equivalence classes until all combinations can be uniquely identified.
 * <p>
 * Important Information:
 * <ul>
 *     <li>Does only support systems with exactly one fault
 *     <li>Does only support faults with exactly two involved parameters
 *     <li>Has some support for constraints
 * </ul>
 */
public class AdaptiveLocatingArray implements FaultCharacterizationAlgorithm {

    private final TestModel model;
    private final ConstraintChecker checker;
    private final Map<int[], TestResult> allTestResults = new HashMap<>();
    private final List<Set<IntArrayWrapper>> equivalenceClasses = new ArrayList<>();

    /**
     * Constructor.
     *
     * @param configuration the fault characterization configuration. It may not be {@code null}.
     */
    public AdaptiveLocatingArray(FaultCharacterizationConfiguration configuration) {
        Preconditions.notNull(configuration);
        if (configuration.getTestModel().getStrength() > 2) {
            configuration.getReporter().reportAssumptionViolation("TestingStrength");
        }
        this.model = configuration.getTestModel();
        this.checker = configuration.getChecker();
    }

    @Override
    public List<int[]> computeNextTestInputs(Map<int[], TestResult> testResults) {
        if (equivalenceClasses.isEmpty()) {
            buildEquivalenceClasses(testResults);
        }

        for (int[] testCase : testResults.keySet()) {
            splitEquivalenceClasses(testCase);
        }

        allTestResults.putAll(testResults);
        if (equivalenceClasses.stream().allMatch(c -> c.size() <= 1)) {
            return Collections.emptyList();
        }

        return Collections.singletonList(findSplittingTestCase());

    }

    private void buildEquivalenceClasses(Map<int[], TestResult> testResults) {
        Set<IntArrayWrapper> eq = new HashSet<>();
        for (Map.Entry<int[], TestResult> entry : testResults.entrySet()) {
            if (entry.getValue().isUnsuccessful()) {
                eq.addAll(IntArrayWrapper.wrapToSet(Combinator.computeSubCombinations(entry.getKey(), 2)));
            }
        }
        equivalenceClasses.add(eq);
    }

    private int[] findSplittingTestCase() {
        int[] newTestCase = CombinationUtil.emptyCombination(model.getNumberOfParameters());
        Set<Set<IntArrayWrapper>> checked = new HashSet<>();
        while (checked.size() < equivalenceClasses.size()) {
            for (Set<IntArrayWrapper> equivalenceClass : equivalenceClasses) {
                findEquivalenceClassSplitter(newTestCase, equivalenceClass);
                checked.add(equivalenceClass);
            }
        }
        for (int i = 0; i < newTestCase.length; i++) {
            if (newTestCase[i] == CombinationUtil.NO_VALUE) {
                newTestCase[i] = 0;
            }
        }
        return newTestCase;
    }

    private void findEquivalenceClassSplitter(int[] newTestCase, Set<IntArrayWrapper> equivalenceClass) {
        for (IntArrayWrapper interaction1 : equivalenceClass) {
            for (IntArrayWrapper interaction2 : equivalenceClass) {
                if (interaction1 != interaction2) {
                    if (CombinationUtil.canBeAdded(newTestCase, interaction1.getArray(), checker) &&
                            CombinationUtil.canBeAdded(newTestCase, interaction2.getArray(), checker)) {
                        CombinationUtil.add(newTestCase, interaction1.getArray());
                        CombinationUtil.add(newTestCase, interaction2.getArray());
                        for (int i = 0; i < interaction1.getArray().length; i++) {
                            if (interaction1.getArray()[i] != CombinationUtil.NO_VALUE &&
                                    interaction1.getArray()[i] != interaction2.getArray()[i]) {
                                newTestCase[i] = (newTestCase[i] + 1) % model.getSizeOfParameter(i);
                            }
                        }
                        return;
                    }
                }
            }
        }
    }

    private void splitEquivalenceClasses(int[] testCase) {
        List<Set<IntArrayWrapper>> newEquivalenceClasses = new ArrayList<>();
        for (Set<IntArrayWrapper> equivalenceClass : equivalenceClasses) {
            if (equivalenceClass.size() > 1) {
                Set<IntArrayWrapper> newEquivalenceClass = new HashSet<>();
                for (IntArrayWrapper interaction : equivalenceClass) {
                    if (CombinationUtil.contains(testCase, interaction.getArray())) {
                        newEquivalenceClass.add(interaction);
                    }
                }
                if (newEquivalenceClass.size() > 0) {
                    equivalenceClass.removeAll(newEquivalenceClass);
                    newEquivalenceClasses.add(newEquivalenceClass);
                }
            }
        }
        equivalenceClasses.addAll(newEquivalenceClasses);
    }

    @Override
    public List<int[]> computeFailureInducingCombinations() {
        Set<int[]> suspiciousInteractions = new HashSet<>();
        for (Map.Entry<int[], TestResult> entry : allTestResults.entrySet()) {
            if (entry.getValue().isUnsuccessful()) {
                for (Set<IntArrayWrapper> equivalenceClass : equivalenceClasses) {
                    for (IntArrayWrapper interaction : equivalenceClass) {
                        if (CombinationUtil.contains(entry.getKey(), interaction.getArray())) {
                            suspiciousInteractions.add(interaction.getArray());
                        }
                    }
                }
            }
        }
        for (Map.Entry<int[], TestResult> entry : allTestResults.entrySet()) {
            Set<int[]> cleanInteractions = new HashSet<>();
            if (entry.getValue().isSuccessful()) {
                for (int[] interaction : suspiciousInteractions) {
                    if (CombinationUtil.contains(entry.getKey(), interaction)) {
                        cleanInteractions.add(interaction);
                    }
                }
                suspiciousInteractions.removeAll(cleanInteractions);
            }
        }
        return new ArrayList<>(suspiciousInteractions);
    }

}
