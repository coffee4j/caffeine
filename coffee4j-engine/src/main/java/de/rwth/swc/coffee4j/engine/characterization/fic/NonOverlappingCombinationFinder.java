package de.rwth.swc.coffee4j.engine.characterization.fic;

import de.rwth.swc.coffee4j.engine.TestModel;
import de.rwth.swc.coffee4j.engine.TestResult;
import de.rwth.swc.coffee4j.engine.util.CombinationUtil;
import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.ints.IntSet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


/**
 * Reeification of the FINOVLOP method.
 * Run iterations until no new test cases are returned, then all faulty interactions can be retrieved.
 * May return subinteractions if there are overlapping faulty interactions in the seed test case.
 */
class NonOverlappingCombinationFinder {

    private final List<IntSet> interactions = new ArrayList<>();
    private final IntSet tabu = new IntArraySet();
    private final int[] seedTestCase;
    private final TestModel model;
    private final FixedVariableFinderFactory finderFactory;

    private Mode mode = Mode.FINOVLOP;
    private FixedVariableFinder finder;


    NonOverlappingCombinationFinder(int[] seedTestCase,
                                    TestModel model,
                                    FixedVariableFinderFactory fixedVariableFinderFactory) {
        this.seedTestCase = seedTestCase;
        this.model = model;
        this.finderFactory = fixedVariableFinderFactory;
    }

    Optional<int[]> runIteration(TestResult result) {
        if (mode == Mode.FINOVLOP) {
            if (result.isSuccessful()) {
                return Optional.empty();
            }

            finder = finderFactory.createFixedVariableFinder(tabu, seedTestCase, model);
            mode = Mode.FIC;

        }

        Optional<int[]> newTestCase = finder.runIteration(result);
        if (newTestCase.isEmpty()) {
            IntSet interaction = finder.getInteraction();
            interactions.add(interaction);
            tabu.addAll(interaction);
            if (interaction.isEmpty()) {
                return Optional.empty();
            }
            mode = Mode.FINOVLOP;
            return Optional.of(getNewTestCase());
        } else {
            return newTestCase;
        }

    }

    List<int[]> getInteractions() {
        return interactions.stream().map(this::getFaultyInteraction).collect(Collectors.toList());
    }

    private int[] getFaultyInteraction(IntSet interaction) {
        int[] result = CombinationUtil.emptyCombination(seedTestCase.length);
        for (int i : interaction) {
            result[i] = seedTestCase[i];
        }
        return result;
    }

    private int[] getNewTestCase() {
        int[] result = Arrays.copyOf(seedTestCase, seedTestCase.length);
        for (int i : tabu) {
            result[i] = (result[i] + 1) % model.getSizeOfParameter(i);
        }
        return result;
    }

    private enum Mode {
        FINOVLOP, FIC
    }

}


