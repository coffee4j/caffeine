package de.rwth.swc.coffee4j.evaluation.analysis.trace;

import de.rwth.swc.coffee4j.evaluation.analysis.PropertyKey;
import de.rwth.swc.coffee4j.evaluation.model.TestModel;
import de.rwth.swc.coffee4j.evaluation.model.TestScenario;
import de.rwth.swc.coffee4j.evaluation.trace.Trace;
import de.rwth.swc.coffee4j.evaluation.trace.TraceIteration;
import de.rwth.swc.coffee4j.evaluation.utils.CombinationUtil;
import de.rwth.swc.coffee4j.evaluation.utils.IntArrayWrapper;

import java.util.Map;
import java.util.Objects;
import java.util.Set;


/**
 * {@link TraceAnalysis} that measures the quality of the results concerning matches to the original faults. It consists
 * of the precision, recall and f-score for an iteration.
 * <br>
 * <br>
 * Precision: What percentage of the found faults occur in the original faults?
 * <br>
 * Recall: What percentage of the original faults are found?
 * <br>
 * FScore: Harmonic mean between precision and recall
 * <br>
 * Additionally, another precision value called the "SuperPrecision" is given, where a found combination is scored as
 * correct, if it is contained in the original faults or in a super-combination of any original fault.
 * <br>
 * Example: Given are the original faults [0, -1, -1], [-1, 0, -1] and [-1, -1, 0] and the found faults [0, -1, -1] and
 * [-1, 0, 0]. SuperPrecision: 1/2
 * <br>
 * Precision: 1/2
 * <br>
 * Recall: 1/3
 * <br>
 * FScore: 4/10
 */
public final class ResultQualityAnalysis implements TraceAnalysis {

    private final String superPrecisionKey;
    private final String precisionKey;
    private final String recallKey;
    private final String fScoreKey;

    /**
     * Constructor.
     *
     * @param superPrecisionKey The metric key for precision including super-combinations. It must not be {@code null}.
     * @param precisionKey      The metric key for the precision value. It must not be {@code null}.
     * @param recallKey         The metric key for the recall value. It must not be {@code null}.
     * @param fScoreKey         The metric key for the f-score value. It must not be {@code null}.
     */
    ResultQualityAnalysis(String superPrecisionKey, String precisionKey, String recallKey, String fScoreKey) {
        this.superPrecisionKey = Objects.requireNonNull(superPrecisionKey);
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

        Set<IntArrayWrapper> real = IntArrayWrapper.wrapToSet(model.getFaultsForScenario(testScenario).values());
        Set<IntArrayWrapper> found = IntArrayWrapper.wrapToSet(iteration.getFailureInducingCombinations());

        long intersect = real.stream().filter(found::contains).count();

        int numberOfSuperMatches = 0;
        for (IntArrayWrapper foundFic : found) {
            for (IntArrayWrapper readFic : real) {
                if (CombinationUtil.contains(readFic.getArray(), foundFic.getArray())) {
                    numberOfSuperMatches++;
                    break;
                }
            }
        }

        double superPrecision = !found.isEmpty() ? (double) numberOfSuperMatches / found.size() : 1;
        double precision = !found.isEmpty() ? (double) intersect / found.size() : 1;
        double recall = !real.isEmpty() ? (double) intersect / real.size() : 1;
        double fScore = recall + precision != 0 ? 2 * (precision * recall) / (precision + recall) : 0;

        return Map.of(
                PropertyKey.percentage(superPrecisionKey), superPrecision,
                PropertyKey.percentage(precisionKey), precision,
                PropertyKey.percentage(recallKey), recall,
                PropertyKey.percentage(fScoreKey), fScore);
    }

}
