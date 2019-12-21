package de.rwth.swc.coffee4j.evaluation.utils;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

/**
 * Common utilities used for integer array which represent combinations or test inputs. All combinations are represented
 * by an array containing the value indexes for each parameter. This means the parameters are 0 through array length
 * minus one. For example, if the first parameter is set to its first value, and the second one to its third value, the
 * resulting array would look like this. [0, 2]. If a parameter does not have a value in a combination, this is
 * represented via {@link CombinationUtil#NO_VALUE}.
 */
public final class CombinationUtil {

    /**
     * The value used to indicate that a parameter has not been assigned a value in a combination.
     */
    public static final int NO_VALUE = -1;

    private CombinationUtil() {

    }


    /**
     * Creates a new combinations which is empty. A combinations is empty if no parameter has a set value, so each entry
     * of the returned array is {@link CombinationUtil#NO_VALUE}. For example, for the size 5, [-1, -1, -1, -1, -1] is
     * returned.
     *
     * @param size the number of parameters for the combinations. Must be greater that or equal to zero
     * @return a combination with the given number of parameters all set to {@link CombinationUtil#NO_VALUE}
     */
    public static int[] emptyCombination(int size) {
        if (size < 0) {
            throw new IllegalArgumentException();
        }

        final int[] combination = new int[size];
        Arrays.fill(combination, NO_VALUE);

        return combination;
    }

    /**
     * Checks whether the first combinations contains the second one. The contains relation is defined as follows: A
     * combination contains another combination if it has the same values for all values which are set in the other
     * combination. Here is a list of two example combinations and a value for stating whether the first one contains
     * the second one: [0] [-1] true [-1] [0] false [0] [0] true [-1] [-1] true Both combinations need to be of the same
     * length.
     *
     * @param firstCombination  a combination. Must not be {@code null}
     * @param secondCombination a combination for which it is checked whether it is contained in the first one. Must not
     *                          be {@code null} and must be of the same size as the first combination
     * @return whether the second combination is contained in the first one as defined by the rules above
     */
    public static boolean contains(int[] firstCombination, int[] secondCombination) {
        checkNotNullAndSameLength(firstCombination, secondCombination);

        for (int i = 0; i < firstCombination.length; i++) {
            if (secondCombination[i] != NO_VALUE && firstCombination[i] != secondCombination[i]) {
                return false;
            }
        }

        return true;
    }

    private static void checkNotNullAndSameLength(int[] first, int[] second) {
        Objects.requireNonNull(first);
        Objects.requireNonNull(second);
        if (first.length != second.length) {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Checks if a combination is valid compared to a given parameter array.
     * <p>
     * This check includes verifying that the number of parameters match and that every parameter is in range.
     *
     * @param combination the combination to check
     * @param parameters  the parameter array
     * @return if the combination is valid
     */
    public static boolean isValid(int[] combination, int[] parameters) {
        Objects.requireNonNull(combination);
        Objects.requireNonNull(parameters);
        if (combination.length != parameters.length) {
            return false;
        }
        for (int i = 0; i < combination.length; i++) {
            if (combination[i] >= parameters[i] || combination[i] < NO_VALUE) {
                return false;
            }
        }
        return true;
    }


    /**
     * Calculates the number of parameters in the combinations not set to {@link CombinationUtil#NO_VALUE}.
     *
     * @param combination a combination. Most not be {@code null}
     * @return the number of parameters in the combination not set
     */
    public static int numberOfSetParameters(int[] combination) {
        Objects.requireNonNull(combination);

        int numberOfSetParameters = 0;
        for (int value : combination) {
            if (value != NO_VALUE) {
                numberOfSetParameters++;
            }
        }

        return numberOfSetParameters;
    }

    /**
     * Calculates an array with the same order and elements as the original, except the excluded ones. For example, if
     * elements is [0, 5, 3, 2, 5, 2], and excluded elements is [5, 6], then [0, 3, 2, 2] is the result.
     *
     * @param elements         all elements. Must not be {@code null}
     * @param excludedElements the ones which are be excluded (duplicates don't matter). Most not be {@code null}
     * @return all elements exception the ones which shall be excluded
     */
    public static int[] exclude(int[] elements, int[] excludedElements) {
        Objects.requireNonNull(elements);
        Objects.requireNonNull(excludedElements);

        final int[] elementsWithoutExcluded = new int[elements.length - numberOfOccurrences(elements, excludedElements)];
        int index = 0;

        for (int element : elements) {
            if (!contains(excludedElements, element)) {
                elementsWithoutExcluded[index++] = element;
            }
        }

        return elementsWithoutExcluded;
    }

    private static int numberOfOccurrences(int[] elements, int[] otherElements) {
        int occurrences = 0;

        for (int element : elements) {
            if (contains(otherElements, element)) {
                occurrences++;
            }
        }

        return occurrences;
    }

    /**
     * Checks whether the given element appears anywhere in the given array. If the given array is empty, the element
     * cannot appear in it and therefore {@code null} is returned.
     *
     * @param elements all elements which are searched. Must not be {@code null}
     * @param element  the element for which is searched
     * @return whether the given element appears in the given elements at any arbitrary index
     */
    public static boolean contains(int[] elements, int element) {
        Objects.requireNonNull(elements);

        for (int leftElement : elements) {
            if (leftElement == element) {
                return true;
            }
        }

        return false;
    }

    /**
     * Checks of two maps with integer array values are equal.
     * <p>
     * Two of such maps are equal when they both contain the same key-value pairs. This method calls {@link
     * Arrays#equals(int[], int[])} instead of the usual {@link Object#equals(Object)} for the values.
     *
     * @param combinations1 the first combination map
     * @param combinations2 the second combination map
     * @param <T>           the type for the keys
     * @return if the map are equals
     */
    public static <T> boolean equals(Map<T, int[]> combinations1, Map<T, int[]> combinations2) {
        if (combinations1.size() != combinations2.size()) {
            return false;
        }
        for (Map.Entry<T, int[]> entry : combinations1.entrySet()) {
            if (!Arrays.equals(entry.getValue(), combinations2.get(entry.getKey()))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks whether two combinations overlap.
     * <p>
     * This is the case iff both combinations share a fixed parameter value. Additionally, both combinations need to
     * have the same length. This is not technically necessary for overlap in general, but it makes sense to restrict it
     * for this purpose.
     *
     * @param combination1 the first combinations. It must not be {@code null}.
     * @param combination2 the second combinations. It must not be {@code null}.
     * @return whether they overlap
     */
    public static boolean overlap(int[] combination1, int[] combination2) {
        Objects.requireNonNull(combination1);
        Objects.requireNonNull(combination2);
        if (combination1.length != combination2.length) {
            return false;
        }

        for (int i = 0; i < combination1.length; i++) {
            if (combination1[i] != NO_VALUE && combination1[i] == combination2[i]) {
                return true;
            }
        }

        return false;
    }
}
