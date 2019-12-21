package de.rwth.swc.coffee4j.evaluation.analysis;

import java.util.Objects;

/**
 * Class for identifying the value of a metric in one of the various property classes.
 * <p>
 * It contains a unique key and some information about the domain of the metric.
 */
public class PropertyKey {

    private final String key;
    private final Class<?> domainClass;
    private final double minValue;
    private final double maxValue;

    /**
     * Constructor.
     *
     * @param key         the key. It must not be {@code null}.
     * @param domainClass the domain class. It must not be {@code null}.
     * @param minValue    the min value
     * @param maxValue    the max value
     * @throws IllegalArgumentException if the min value is greater than the max value
     */
    public PropertyKey(String key, Class<?> domainClass, double minValue, double maxValue) {
        if (minValue > maxValue) {
            throw new IllegalArgumentException("Minimum value must be smaller or equal compared to the maximum value.");
        }
        this.key = Objects.requireNonNull(key);
        this.domainClass = Objects.requireNonNull(domainClass);
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    /**
     * Static factory method for a property key with a positive integer domain.
     *
     * @param key the key. It must not be {@code null}.
     * @return a property key
     */
    public static PropertyKey positiveInteger(String key) {
        return new PropertyKey(key, Integer.class, 0, Double.POSITIVE_INFINITY);
    }

    /**
     * Static factory method for a property key with a boolean domain.
     *
     * @param key the key. It must not be {@code null}.
     * @return a property key
     */
    public static PropertyKey bool(String key) {
        return new PropertyKey(key, Boolean.class, 0, 1);
    }

    /**
     * Static factory method for a property key with a percentage domain.
     *
     * @param key the key. It must not be {@code null}.
     * @return a property key
     */
    public static PropertyKey percentage(String key) {
        return new PropertyKey(key, Double.class, 0, 1);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof PropertyKey) {
            PropertyKey other = (PropertyKey) obj;
            return this.key.equals(other.key);
        }
        return false;
    }

    /**
     * Gets the key.
     *
     * @return the key
     */
    public String getKey() {
        return key;
    }

    /**
     * Gets the domain class.
     *
     * @return the domain class
     */
    public Class<?> getDomainClass() {
        return domainClass;
    }

    /**
     * Gets the min value of the domain.
     *
     * @return the min value
     */
    public double getMinValue() {
        return minValue;
    }

    /**
     * Gets the max value of the domain.
     *
     * @return the max value
     */
    public double getMaxValue() {
        return maxValue;
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }

    @Override
    public String toString() {
        return key;
    }
}
