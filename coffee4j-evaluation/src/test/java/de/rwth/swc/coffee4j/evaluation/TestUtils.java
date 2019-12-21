package de.rwth.swc.coffee4j.evaluation;

import de.rwth.swc.coffee4j.evaluation.analysis.PropertyKey;
import de.rwth.swc.coffee4j.evaluation.analysis.trace.TraceProperties;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public interface TestUtils {

    static void assertPropertyEquals(Map<PropertyKey, Number> expected, Map<PropertyKey, Number> actual) {

        assertEquals(expected.size(), actual.size());

        for (PropertyKey propertyKey : expected.keySet()) {
            assertEquals(expected.get(propertyKey).doubleValue(),
                    actual.get(propertyKey).doubleValue(), 0.000001, () -> "Error for " + propertyKey);
        }

    }

    static void assertPropertyEquals(List<TraceProperties.IterationProperties> expected,
                                     List<TraceProperties.IterationProperties> actual) {

        assertEquals(expected.size(), actual.size());
        for (int i = 0; i < expected.size(); i++) {
            TraceProperties.IterationProperties expectedIteration = expected.get(i);
            TraceProperties.IterationProperties actualIteration = actual.get(i);
            assertEquals(expectedIteration.getState(), actualIteration.getState());
            assertPropertyEquals(expectedIteration.getProperties(), actualIteration.getProperties());
        }

    }
}
