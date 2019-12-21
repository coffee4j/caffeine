package de.rwth.swc.coffee4j.engine.generator.aetg;

import de.rwth.swc.coffee4j.engine.TestModel;
import de.rwth.swc.coffee4j.engine.constraint.ConstraintCheckerFactory;
import de.rwth.swc.coffee4j.engine.constraint.DynamicHardConstraintChecker;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CoverageMapTest {

    private static final Set<int[]> COMBINATIONS = Set.of(new int[]{1, -1, -1}, new int[]{-1, 2, -1}, new int[]{1, -1, 2}, new int[]{-1, 1, -1}, new int[]{1, 2, 2}, new int[]{2, 2, -1});
    private static final DynamicHardConstraintChecker EMPTY_CHECKER = new ConstraintCheckerFactory(new TestModel(2, new int[]{3, 3, 3}, Collections.emptyList(), Collections.emptyList())).createDynamicHardConstraintChecker();

    @Test
    void doesCorrectlyInitialize() {
        CoverageMap map = new CoverageMap(COMBINATIONS, 3, EMPTY_CHECKER);
        assertTrue(map.hasUncoveredCombinations());
    }

    @Test
    void doesCorrectlyCoverCombinations() {

        CoverageMap map = new CoverageMap(COMBINATIONS, 3, EMPTY_CHECKER);
        map.updateSubCombinationCoverage(new int[]{1, 2, 1});
        assertEquals(1, map.getNumberOfUncoveredCombinations(new int[]{1, 1, 1}));
        assertTrue(map.hasUncoveredCombinations());
    }

    @Test
    void doesCorrectlyCountCombinationsPartial() {
        CoverageMap map = new CoverageMap(COMBINATIONS, 3, EMPTY_CHECKER);
        assertEquals(3, map.getNumberOfUncoveredCombinations(new int[]{1, -1, 1}));
    }

    @Test
    void doesCorrectlyCountCombinationsComplete() {
        CoverageMap map = new CoverageMap(COMBINATIONS, 3, EMPTY_CHECKER);
        assertEquals(2, map.getNumberOfUncoveredCombinations(new int[]{1, 1, 1}));
    }

    @Test
    void doesCorrectlyFindBestParameter() {
        CoverageMap map = new CoverageMap(COMBINATIONS, 3, EMPTY_CHECKER);

        AetgSatAlgorithm.ParameterValuePair mostCommonValue = map.getMostCommonValue(Collections.emptySet(), Collections.EMPTY_SET);
        assertEquals(0, mostCommonValue.getParameter());
        assertEquals(1, mostCommonValue.getValue());
    }


    @Test
    void doesCorrectlyFindBestParameterAfterRemoval() {
        CoverageMap map = new CoverageMap(COMBINATIONS, 3, EMPTY_CHECKER);
        map.updateSubCombinationCoverage(new int[]{1, 1, 1});
        AetgSatAlgorithm.ParameterValuePair mostCommonValue = map.getMostCommonValue(Collections.emptySet(), Collections.EMPTY_SET);
        assertEquals(1, mostCommonValue.getParameter());
        assertEquals(2, mostCommonValue.getValue());
    }

    @Test
    void doesCorrectlyFindBestParameterForbidden() {
        CoverageMap map = new CoverageMap(COMBINATIONS, 3, EMPTY_CHECKER);
        AetgSatAlgorithm.ParameterValuePair mostCommonValue = map.getMostCommonValue(Set.of(new AetgSatAlgorithm.ParameterValuePair(0, 1)), Collections.EMPTY_SET);
        assertEquals(1, mostCommonValue.getParameter());
        assertEquals(2, mostCommonValue.getValue());
    }

    @Test
    void doesHandleConstraints() {
        DynamicHardConstraintChecker checker = new ConstraintCheckerFactory(new TestModel(2, new int[]{3, 3, 3}, Collections.emptyList(), Collections.emptyList())).createDynamicHardConstraintChecker();
        checker.addConstraint(new int[]{1, -1, -1});
        CoverageMap map = new CoverageMap(COMBINATIONS, 3, checker);
        assertEquals(3, map.getNumberOfUncoveredCombinations());
    }

    @Test
    void doesHandleDynamicConstraints() {
        DynamicHardConstraintChecker checker = new ConstraintCheckerFactory(new TestModel(2, new int[]{3, 3, 3}, Collections.emptyList(), Collections.emptyList())).createDynamicHardConstraintChecker();
        CoverageMap map = new CoverageMap(COMBINATIONS, 3, checker);
        assertEquals(6, map.getNumberOfUncoveredCombinations());
        map.addForbiddenCombination(new int[]{1, -1, -1});
        assertEquals(3, map.getNumberOfUncoveredCombinations());
    }

    @Test
    void doesHandleImplicitConstraints() {
        TestModel model = new TestModel(2, new int[]{2, 2, 2}, Collections.emptyList(), Collections.emptyList());
        DynamicHardConstraintChecker checker = new ConstraintCheckerFactory(model).createDynamicHardConstraintChecker();
        CoverageMap map = new CoverageMap(model.getParameterSizes(), 2, checker);
        map.addForbiddenCombination(new int[]{0, 0, -1});
        map.addForbiddenCombination(new int[]{1, -1, 0});
        // only finds [0, -1, 0], because [-1, 0, 0] is implicitly covered
        assertEquals(1, map.getNumberOfUncoveredCombinations(new int[]{0, 0, 0}));
    }


}