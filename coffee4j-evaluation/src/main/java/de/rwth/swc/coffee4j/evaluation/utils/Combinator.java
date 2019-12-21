package de.rwth.swc.coffee4j.evaluation.utils;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static de.rwth.swc.coffee4j.evaluation.utils.CombinationUtil.NO_VALUE;
import static de.rwth.swc.coffee4j.evaluation.utils.CombinationUtil.emptyCombination;

/**
 * Utility methods used for combinatorial tasks in the context of combinatorial test generation.
 * <p>
 * Uses the indexing schema introduced in {@link CombinationUtil}.
 */
public final class Combinator {

    private static final String PARAMETERS_NOT_NULL = "Parameters cannot be null";
    private static final String AT_LEAST_ONE_PARAMETER = "At least one parameter has to be given";
    private static final String TOO_MANY_PARAMETERS = "The combination size cannot be smaller than the number" + "of parameters";
    private static final String TOO_HIGH_PARAMETERS = "The combination size cannot be smaller than the highest" + "parameter number";

    private Combinator() {
    } //private constructor for utility class

    /**
     * Computes the full cartesian product of the given parameters. Each entry form the cartesian product is stored in
     * an array of size combinationSize so that the combinations can be used for larger test inputs in later iterations
     * of the IPOG algorithm.
     *
     * @param parameters      the parameters for whose values the cartesian product shall be computed. Must no be {@code
     *                        null} or empty
     * @param combinationSize the size of the combinations returned. Empty places are filled with {@link
     *                        CombinationUtil#NO_VALUE}. Must not be smaller than the number of parameters
     * @return all tuples of the cartesian product
     * @throws NullPointerException     if parameters is {@code null}
     * @throws IllegalArgumentException if there are no parameters or if the combinationsSize is too small
     */
    private static List<int[]> computeCartesianProduct(Map<Integer, Integer> parameters, int combinationSize) {
        Objects.requireNonNull(parameters, PARAMETERS_NOT_NULL);
        if (parameters.isEmpty()) {
            throw new IllegalArgumentException(AT_LEAST_ONE_PARAMETER);
        }
        if (combinationSize < parameters.size()) {
            throw new IllegalArgumentException(TOO_MANY_PARAMETERS);
        }
        if (combinationSize <= parameters.keySet().stream().mapToInt(parameter -> parameter).max().orElse(0)) {
            throw new IllegalArgumentException(TOO_HIGH_PARAMETERS);
        }

        List<int[]> combinations = new ArrayList<>();
        int[] currentIndex = new int[parameters.size()];

        int[] keys = parameters.keySet().stream().mapToInt(Integer::intValue).toArray();
        Arrays.sort(keys);

        do {
            int[] currentCombination = new int[combinationSize];
            Arrays.fill(currentCombination, NO_VALUE);

            for (int i = 0; i < keys.length; i++) {
                int index = keys[i];
                int value = currentIndex[i];

                currentCombination[index] = value;
            }

            combinations.add(currentCombination);
        } while (tryIncreaseByOne(currentIndex, keys, parameters));

        return combinations;
    }

    private static boolean tryIncreaseByOne(int[] currentIndex, int[] keys, Map<Integer, Integer> parameters) {
        for (int i = 0; i < currentIndex.length; i++) {
            currentIndex[i]++;
            if (currentIndex[i] < parameters.get(keys[i])) {
                return true;
            } else {
                currentIndex[i] = 0;
            }
        }

        return false;
    }

    private static List<Set<Integer>> computeParameterCombinationsRecursively(int[] parameters, int k) {
        if (k == 0 || parameters.length == 0 || parameters.length < k) {
            return Collections.emptyList();
        } else if (k == 1) {
            List<Set<Integer>> combinations = new ArrayList<>(parameters.length);

            for (int parameter : parameters) {
                Set<Integer> set = new HashSet<>(1);
                set.add(parameter);

                combinations.add(set);
            }

            return combinations;
        } else if (parameters.length == k) {
            List<Set<Integer>> combinations = new ArrayList<>(1);
            combinations.add(Arrays.stream(parameters).boxed().collect(Collectors.toSet()));

            return combinations;
        } else {
            int[] tail = Arrays.copyOfRange(parameters, 1, parameters.length);
            List<Set<Integer>> tailSubsets = computeParameterCombinationsRecursively(tail, k - 1);

            for (Set<Integer> set : tailSubsets) {
                set.add(parameters[0]);
            }

            List<Set<Integer>> subsets = computeParameterCombinationsRecursively(tail, k);

            List<Set<Integer>> combinations = new ArrayList<>(tailSubsets.size() + subsets.size());
            combinations.addAll(tailSubsets);
            combinations.addAll(subsets);

            return combinations;
        }
    }


    /**
     * Computes all size-value-combinations there are with the given parameters. For example, for the given parameters
     * [2, 2, 2] (three parameters with 2 values each) and size 2, the following combinations are returned: [0, 0, -1]
     * [0, 1, -1] [1, 0, -1] [1, 1, -1] [0, -1, 0] [0, -1, 1] [1, -1, 0] [1, -1, 1] [-1, 0, 0] [-1, 0, 1] [-1, 1, 0]
     * [-1, 1, 1]
     *
     * @param parameters all parameters. They are defined as their number of values. So [2, 3] means the first parameter
     *                   has two values, and the second one has three. Must not be {@code null}
     * @param size       the size of sub-combinations of values in the parameters that are calculated
     * @return all sub-combinations of the values with the given size as demonstrated above. The order of combinations
     * is not defined and may change in subsequent implementations. In any combinations the values for parameters are
     * ordered the same way as the parameters supplied to the method
     */
    public static Set<int[]> computeCombinations(int[] parameters, int size) {
        Objects.requireNonNull(parameters);
        if (size < 0) {
            throw new IllegalArgumentException();
        }

        final List<Set<Integer>> parameterCombinations = computeParameterCombinationsRecursively(IntStream.range(0, parameters.length).toArray(), size);
        final Set<int[]> combinations = new HashSet<>();
        for (Set<Integer> parameterCombination : parameterCombinations) {
            final Map<Integer, Integer> parameterSizes = new HashMap<>(parameterCombination.size());
            for (int parameter : parameterCombination) {
                parameterSizes.put(parameter, parameters[parameter]);
            }

            combinations.addAll(computeCartesianProduct(parameterSizes, parameters.length));
        }

        return combinations;
    }

    /**
     * Computes all sub-combinations with the given size that the combination has. The combinations is allowed to have
     * values not set. For example, [-1, 2, 3, 1, -1, 3] called with 2 would return [-1, 2, 3, -1, -1, -1] [-1, 2, -1,
     * 1, -1, -1] [-1, 2, -1, -1, -1, 3] [-1, -1, 3, 1, -1, -1] [-1, -1, 3, -1, -1, 3] [-1, -1, -1, 1, -1, 3]
     *
     * @param combination a combination. Must not be {@code null}
     * @param size        the size of sub-combinations. Must be positive
     * @return all sub-combinations with the given size of the combinations. No order is guaranteed. The parameters are
     * in the same order as with the given combination
     */
    public static List<int[]> computeSubCombinations(int[] combination, int size) {
        Objects.requireNonNull(combination);
        if (size < 0) {
            throw new IllegalArgumentException();
        }

        return computeSubCombinationsRecursively(combination, emptyCombination(combination.length), 0, size);
    }

    private static List<int[]> computeSubCombinationsRecursively(int[] combination, int[] currentSubCombination, int index, int size) {
        final int currentSize = CombinationUtil.numberOfSetParameters(currentSubCombination);
        if (currentSize == size) {
            return Collections.singletonList(Arrays.copyOf(currentSubCombination, currentSubCombination.length));
        }
        if (index == combination.length || size > combination.length - index + 1 + currentSize) {
            return Collections.emptyList();
        }

        final List<int[]> result = new ArrayList<>(computeSubCombinationsRecursively(combination, currentSubCombination, index + 1, size));

        if (combination[index] != NO_VALUE) {
            currentSubCombination[index] = combination[index];
            result.addAll(computeSubCombinationsRecursively(combination, currentSubCombination, index + 1, size));
            currentSubCombination[index] = NO_VALUE;
        }

        return result;
    }

}
