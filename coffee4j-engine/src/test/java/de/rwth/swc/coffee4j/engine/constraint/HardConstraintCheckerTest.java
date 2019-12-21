package de.rwth.swc.coffee4j.engine.constraint;

import de.rwth.swc.coffee4j.engine.TestModel;
import de.rwth.swc.coffee4j.engine.TupleList;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HardConstraintCheckerTest {

    @Test
    void checkIsValid() {
        final List<TupleList> forbiddenTupleLists = new ArrayList<>();
        forbiddenTupleLists.add(new TupleList(1, new int[]{0, 1}, Arrays.asList(new int[]{0, 0}, new int[]{1, 1})));

        final TestModel model = new TestModel(2, new int[]{2, 2, 2}, forbiddenTupleLists, Collections.emptyList());

        final ConstraintChecker checker = new ConstraintCheckerFactory(model).createHardConstraintsChecker();

        assertTrue(checker.isValid(new int[]{1, 0, 1}));
        assertFalse(checker.isValid(new int[]{0, 0, 1}));
    }

    @Test
    void checkIsValidWithAdditionalParameter() {
        final List<TupleList> forbiddenTupleLists = new ArrayList<>();
        forbiddenTupleLists.add(new TupleList(1, new int[]{0, 1}, Arrays.asList(new int[]{0, 0}, new int[]{1, 1})));

        final TestModel model = new TestModel(2, new int[]{2, 2, 2}, forbiddenTupleLists, Collections.emptyList());

        final ConstraintChecker checker = new ConstraintCheckerFactory(model).createHardConstraintsChecker();

        assertTrue(checker.isExtensionValid(new int[]{1, 0}, 2, 0));
        assertFalse(checker.isExtensionValid(new int[]{0, 0}, 2, 0));
    }
}
