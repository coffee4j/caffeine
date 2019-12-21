package de.rwth.swc.coffee4j.engine.util;

/**
 * Common operations which need to be performed on arrays in combinatorial test input generation.
 */
public final class ArrayUtil {
    
    private ArrayUtil() {
    }
    
    /**
     * Calculates an array with the same order and elements as the original, except the excluded ones.
     * For example, if elements is [0, 5, 3, 2, 5, 2], and excluded elements is [5, 6], then [0, 3, 2, 2] is the result.
     *
     * @param elements         all elements. Must not be {@code null}
     * @param excludedElements the ones which are be excluded (duplicates don't matter). Most not be {@code null}
     * @return all elements exception the ones which shall be excluded
     */
    public static int[] exclude(int[] elements, int[] excludedElements) {
        Preconditions.notNull(elements);
        Preconditions.notNull(excludedElements);
        
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
        Preconditions.notNull(elements);
        
        for (int leftElement : elements) {
            if (leftElement == element) {
                return true;
            }
        }
        
        return false;
    }
    
}
