package de.rwth.swc.coffee4j.evaluation.analysis;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class PropertyKeyTest {

    @Test
    void positiveIntegerShouldBeUnbounded() {
        PropertyKey key = PropertyKey.positiveInteger("Test");
        assertEquals(Double.POSITIVE_INFINITY, key.getMaxValue());
    }

    @Test
    void booleanShouldBeModeledAsZeroOrOne() {
        PropertyKey key = PropertyKey.bool("Test");
        assertEquals(Boolean.class, key.getDomainClass());
        assertEquals(0, key.getMinValue());
        assertEquals(1, key.getMaxValue());
    }

    @Test
    void percentageKeyShouldHaveRangeZeroToOne() {
        PropertyKey key = PropertyKey.percentage("Test");
        assertEquals(Double.class, key.getDomainClass());
        assertEquals(0, key.getMinValue());
        assertEquals(1, key.getMaxValue());

    }

    @Test
    void shouldThrowForInvalidRange() {
        assertThrows(IllegalArgumentException.class,
                () -> new PropertyKey("Test", Integer.class, 2, -2));
    }

    @Test
    void shouldEnforceUniquenessOfKey() {
        PropertyKey key1 = PropertyKey.bool("Test");
        PropertyKey key2 = PropertyKey.percentage("Test");
        PropertyKey key3 = PropertyKey.positiveInteger("NotTest");

        assertEquals(key1, key2);
        assertNotEquals(key2, key3);
        assertNotEquals(key1, key3);

        Set<PropertyKey> hashSet = new HashSet<>();
        hashSet.add(key1);
        hashSet.add(key2);
        hashSet.add(key3);
        assertEquals(2, hashSet.size());

    }

}