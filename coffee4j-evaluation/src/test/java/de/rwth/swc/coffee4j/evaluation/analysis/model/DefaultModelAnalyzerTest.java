package de.rwth.swc.coffee4j.evaluation.analysis.model;

import de.rwth.swc.coffee4j.evaluation.TestData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class DefaultModelAnalyzerTest {

    @Test
    void shouldCreateNonEmptyAnalyzer() {

        ModelAnalyzer analyzer = new DefaultModelAnalyzer();
        ModelProperties properties = analyzer.analyze(TestData.MODEL_1);
        assertEquals(TestData.MODEL_1.getIdentifier(), properties.getIdentifier());
        assertFalse(properties.getProperties().isEmpty());

    }

}