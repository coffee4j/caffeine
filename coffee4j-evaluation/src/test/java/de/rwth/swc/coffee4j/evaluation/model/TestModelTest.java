package de.rwth.swc.coffee4j.evaluation.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TestModelTest {

    @Test
    void shouldConstructThroughBuilder() {

        TestModel model = new TestModel.Builder("TestModel")
                .withParameters(2, 2, 2)
                .buildModel();

        assertEquals("TestModel", model.getIdentifier().getModelName());
        assertArrayEquals(new int[]{2, 2, 2}, model.getParameters());

    }

    @Test
    void shouldThrowForMismatchedNumberOfParametersInConstraint() {
        assertThrows(IllegalArgumentException.class, () -> new TestModel.Builder("TestModel")
                .withParameters(2, 2, 2)
                .withConstraint("C0", 1, 1)
                .buildModel());
    }

}