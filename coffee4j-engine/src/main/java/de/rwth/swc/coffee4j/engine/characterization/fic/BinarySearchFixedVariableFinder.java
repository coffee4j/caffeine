package de.rwth.swc.coffee4j.engine.characterization.fic;

import de.rwth.swc.coffee4j.engine.TestModel;
import de.rwth.swc.coffee4j.engine.TestResult;
import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.ints.IntSet;

import java.util.Optional;

class BinarySearchFixedVariableFinder implements FixedVariableFinder {

    private final IntSet interaction = new IntArraySet();
    private final IntSet free;
    private final int[] seedTestCase;
    private final TestModel model;
    private final IntSet low = new IntArraySet();
    private final IntSet high = new IntArraySet();
    private IntSet candidates = new IntArraySet();
    private Mode mode = Mode.INIT;

    BinarySearchFixedVariableFinder(IntSet tabu, int[] seedTestCase, TestModel model) {
        free = new IntArraySet(tabu);
        this.seedTestCase = seedTestCase;
        this.model = model;

        for (int i = 0; i < model.getNumberOfParameters(); i++) {
            if (!free.contains(i)) {
                candidates.add(i);
            }
        }
    }

    public Optional<int[]> runIteration(TestResult result) {
        if (mode == Mode.INIT) {
            mode = Mode.FULL_CHECK;
            return Optional.of(getModifiedTestCase(candidates));
        } else if (mode == Mode.FULL_CHECK) {
            if (result.isUnsuccessful()) {
                return Optional.empty();
            }
            mode = Mode.LOW_CHECK;
            partition(candidates, low, high);
            return Optional.of(getModifiedTestCase(low));
        } else {
            if (result.isSuccessful()) {
                candidates = new IntArraySet(low);
            } else {
                candidates = new IntArraySet(high);
                free.addAll(low);
            }
            if (candidates.size() == 1) {
                interaction.add(candidates.stream().findAny().get().intValue());
                mode = Mode.FULL_CHECK;
                candidates = new IntArraySet();
                for (int i = 0; i < model.getNumberOfParameters(); i++) {
                    if (!free.contains(i) && !interaction.contains(i)) {
                        candidates.add(i);
                    }
                }
                return Optional.of(getModifiedTestCase(candidates));
            }
            partition(candidates, low, high);
            return Optional.of(getModifiedTestCase(low));
        }

    }

    @Override
    public TestModel getModel() {
        return model;
    }

    @Override
    public int[] getSeedTestCase() {
        return seedTestCase;
    }

    private void partition(IntSet candidates, IntSet low, IntSet high) {
        low.clear();
        high.clear();

        for (int i : candidates) {
            if (2 * low.size() < candidates.size()) {
                low.add(i);
            } else {
                high.add(i);
            }

        }
    }

    @Override
    public IntSet getInteraction() {
        return interaction;
    }

    @Override
    public IntSet getFreeVariables() {
        return free;
    }

    private enum Mode {
        INIT, FULL_CHECK, LOW_CHECK
    }
}
