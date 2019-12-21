package de.rwth.swc.coffee4j.evaluation.analysis.trace;

import de.rwth.swc.coffee4j.evaluation.analysis.PropertyKey;
import de.rwth.swc.coffee4j.evaluation.model.TestModel;
import de.rwth.swc.coffee4j.evaluation.model.TestScenario;
import de.rwth.swc.coffee4j.evaluation.trace.Trace;
import de.rwth.swc.coffee4j.evaluation.trace.TraceIteration;
import de.rwth.swc.coffee4j.evaluation.utils.CombinationUtil;
import de.rwth.swc.coffee4j.evaluation.utils.Combinator;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;


/**
 * {@link TraceAnalysis} that measures the quality of the results concerning the identification of failed test cases. It
 * consists of the average precision, recall and f-score over all iterations.
 * <br>
 * <br>
 * Precision: What percentage of failed test cases in an exhaustive search with the found failure inducing combinations
 * would also be identified as failed with the original failure inducing combinations?
 * <br>
 * Recall: What percentage of failed test cases in an exhaustive search with the original failure inducing combinations
 * would also be identified as failed with the found failure inducing combinations?
 * <br>
 * FScore: Harmonic mean between precision and recall
 * <br>
 * <br>
 * Example:
 * <br>
 * Given are the original faults [0, -1, -1], [-1, 0, -1] and [-1, -1, 0], the found faults [0, -1, -1] and [-1, 0, 0]
 * and the input parameters [2, 2, 2]. An exhaustive search on the parameter space returns the following results for
 * both versions:
 * <br>
 * <table>
 * <caption>Exhaustive search</caption>
 * <tr>
 * <th>Test Case</th>
 * <th>Original</th>
 * <th>Found</th>
 * </tr>
 * <tr>
 * <td>[0, 0, 0]</td>
 * <td>Fail</td>
 * <td>Fail</td>
 * </tr>
 * <tr>
 * <td>[0, 0, 1]</td>
 * <td>Fail</td>
 * <td>Fail</td>
 * </tr>
 * <tr>
 * <td>[0, 1, 0]</td>
 * <td>Fail</td>
 * <td>Fail</td>
 * </tr>
 * <tr>
 * <td>[0, 1, 1]</td>
 * <td>Fail</td>
 * <td>Fail</td>
 * </tr>
 * <tr>
 * <td>[1, 0, 0]</td>
 * <td>Fail</td>
 * <td>Fail</td>
 * </tr>
 * <tr>
 * <td>[1, 0, 1]</td>
 * <td>Fail</td>
 * <td>Success</td>
 * </tr>
 * <tr>
 * <td>[1, 1, 0]</td>
 * <td>Fail</td>
 * <td>Success</td>
 * </tr>
 * <tr>
 * <td>[1, 1, 1]</td>
 * <td>Success</td>
 * <td>Success</td>
 * </tr>
 * </table>
 * Precision: 1
 * <br>
 * Recall: 6/8
 * <br>
 * FScore: 6/7
 */
public final class ClassificationResultQualityAnalysis implements TraceAnalysis {

    private final String precisionKey;
    private final String recallKey;
    private final String fScoreKey;

    /**
     * Constructor.
     *
     * @param precisionKey The metric key for the precision value. It must not be {@code null}.
     * @param recallKey    The metric key for the recall value. It must not be {@code null}.
     * @param fScoreKey    The metric key for the f-score value. It must not be {@code null}.
     */
    ClassificationResultQualityAnalysis(String precisionKey, String recallKey, String fScoreKey) {
        this.precisionKey = Objects.requireNonNull(precisionKey);
        this.recallKey = Objects.requireNonNull(recallKey);
        this.fScoreKey = Objects.requireNonNull(fScoreKey);
    }


    @Override
    public Map<PropertyKey, Number> analyze(TestModel model, TestScenario testScenario, Trace trace, TraceIteration iteration) {
        Objects.requireNonNull(model);
        Objects.requireNonNull(testScenario);
        Objects.requireNonNull(trace);
        Objects.requireNonNull(iteration);

        Collection<int[]> foundCauses = iteration.getFailureInducingCombinations();
        Collection<int[]> originalCauses = model.getFaultsForScenario(testScenario).values();

        int truePositive = 0;
        int falsePositive = 0;
        int falseNegative = 0;

        Set<Integer> usedIndices = getUsedIndices(foundCauses, originalCauses);

        if (usedIndices.size() > 12) {
            return Collections.emptyMap();
        }

        int[] reduced = CombinationUtil.emptyCombination(usedIndices.size());
        int index = 0;
        long combinationFactor = 1;
        for (int i = 0; i < model.getParameters().length; i++) {

            if (usedIndices.contains(i)) {
                reduced[index] = model.getParameters()[i];
                index++;
            } else {
                combinationFactor *= model.getParameters()[i];
            }
        }

        List<int[]> reducedFoundCauses = foundCauses.stream().map(c -> reduceCause(c, usedIndices)).collect(Collectors.toList());
        List<int[]> reducedOriginalCauses = originalCauses.stream().map(c -> reduceCause(c, usedIndices)).collect(Collectors.toList());

        for (int[] testCase : Combinator.computeCombinations(reduced, reduced.length)) {
            boolean original = reducedOriginalCauses.stream().anyMatch(c -> CombinationUtil.contains(testCase, c));
            boolean found = reducedFoundCauses.stream().anyMatch(c -> CombinationUtil.contains(testCase, c));

            if (original && found) {
                truePositive += combinationFactor;
            } else if (original) {
                falseNegative += combinationFactor;
            } else if (found) {
                falsePositive += combinationFactor;
            }
        }

        return computeResults(originalCauses, truePositive, falsePositive, falseNegative);
    }

    @NotNull
    private Map<PropertyKey, Number> computeResults(Collection<int[]> originalCauses, int truePositive, int falsePositive, int falseNegative) {
        double precision = (truePositive + falsePositive) != 0 ? (double) truePositive / (truePositive + falsePositive) : 1;
        double recall = (truePositive + falseNegative) != 0 ? (double) truePositive / (truePositive + falseNegative) : 0;
        if (originalCauses.isEmpty()) {
            recall = 1;
        }

        double fScore = (recall + precision) != 0 ? 2 * (precision * recall) / (precision + recall) : 0;

        return Map.of(
                PropertyKey.percentage(precisionKey), precision,
                PropertyKey.percentage(recallKey), recall,
                PropertyKey.percentage(fScoreKey), fScore);
    }

    @NotNull
    private Set<Integer> getUsedIndices(Collection<int[]> foundCauses, Collection<int[]> originalCauses) {
        Set<Integer> usedIndices = new HashSet<>();
        for (int[] cause : foundCauses) {
            for (int i = 0; i < cause.length; i++) {
                if (cause[i] != CombinationUtil.NO_VALUE) {
                    usedIndices.add(i);
                }
            }
        }
        for (int[] cause : originalCauses) {
            for (int i = 0; i < cause.length; i++) {
                if (cause[i] != CombinationUtil.NO_VALUE) {
                    usedIndices.add(i);
                }
            }
        }
        return usedIndices;
    }

    private int[] reduceCause(int[] cause, Set<Integer> usedIndices) {
        int[] reducedCause = new int[usedIndices.size()];
        int index = 0;
        for (int i = 0; i < cause.length; i++) {
            if (usedIndices.contains(i)) {
                reducedCause[index] = cause[i];
                index++;
            }
        }
        return reducedCause;
    }

}
