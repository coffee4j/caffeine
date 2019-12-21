package de.rwth.swc.coffee4j.engine.generator.aetg;

import de.rwth.swc.coffee4j.engine.TestModel;
import de.rwth.swc.coffee4j.engine.util.CombinationUtil;
import de.rwth.swc.coffee4j.engine.util.Combinator;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntSet;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AetgSatAlgorithmTest {

    private static void verifyAllCombinationsPresent(List<int[]> testSuite, int[] parameterSizes, int strength) {
        final List<IntSet> parameterCombinations = Combinator.computeParameterCombinations(IntStream.range(0, parameterSizes.length).toArray(), strength);

        for (IntSet parameterCombination : parameterCombinations) {
            final List<int[]> combinations = computeCartesianProduct(parameterCombination, parameterSizes);

            for (int[] combination : combinations) {
                assertTrue(containsCombination(testSuite, combination), () -> "" + Arrays.toString(combination) + " missing.");
            }
        }
    }

    private static List<int[]> computeCartesianProduct(IntSet parameterCombination, int[] parameterSizes) {
        final Int2IntMap parameterSizeMap = new Int2IntOpenHashMap(parameterSizes.length);

        for (int parameter : parameterCombination) {
            parameterSizeMap.put(parameter, parameterSizes[parameter]);
        }

        return Combinator.computeCartesianProduct(parameterSizeMap, parameterSizes.length);
    }

    private static boolean containsCombination(List<int[]> testSuite, int[] combination) {
        for (int[] testInput : testSuite) {
            if (CombinationUtil.contains(testInput, combination)) {
                return true;
            }
        }
        return false;
    }

    @Test
    void oneParameterTwoValueModel() {
        final TestModel model = new TestModel(1, new int[]{2}, Collections.emptyList(), Collections.emptyList());

        final List<int[]> testSuite = new AetgSatAlgorithm(AetgSatConfiguration.aetgSatConfiguration().model(model).build()).generate();

        assertEquals(2, testSuite.size());
        assertEquals(1, testSuite.get(0).length);
        assertEquals(1, testSuite.get(1).length);
    }

    @Test
    void itShouldCoverEachValueOnceForStrengthOneWithMultipleParameters() {
        final TestModel model = new TestModel(1, new int[]{4, 4, 4, 4}, Collections.emptyList(), Collections.emptyList());

        final List<int[]> testSuite = new AetgSatAlgorithm(AetgSatConfiguration.aetgSatConfiguration().model(model).build()).generate();

        verifyAllCombinationsPresent(testSuite, model.getParameterSizes(), 1);
    }

    @Test
    void itShouldGenerateAllNeededTestInputsIfSmallerStrength() {
        final TestModel model = new TestModel(2, new int[]{3, 3, 3, 3}, Collections.emptyList(), Collections.emptyList());

        final List<int[]> testSuite = new AetgSatAlgorithm(AetgSatConfiguration.aetgSatConfiguration().model(model).build()).generate();

        verifyAllCombinationsPresent(testSuite, model.getParameterSizes(), 2);
    }

    @Test
    void itShouldCoverAllCombinationsIfParametersHaveDifferentSizes() {
        final TestModel model = new TestModel(2, new int[]{2, 5, 3, 2, 4}, Collections.emptyList(), Collections.emptyList());

        final List<int[]> testSuite = new AetgSatAlgorithm(AetgSatConfiguration.aetgSatConfiguration().model(model).build()).generate();

        verifyAllCombinationsPresent(testSuite, model.getParameterSizes(), 2);
    }

}
