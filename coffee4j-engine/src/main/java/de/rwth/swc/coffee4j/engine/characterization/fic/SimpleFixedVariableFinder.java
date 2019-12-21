package de.rwth.swc.coffee4j.engine.characterization.fic;

import de.rwth.swc.coffee4j.engine.TestModel;
import de.rwth.swc.coffee4j.engine.TestResult;
import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.ints.IntSet;

import java.util.Optional;

class SimpleFixedVariableFinder implements FixedVariableFinder {


    private final IntSet interaction = new IntArraySet();
    private final IntSet free;
    private final IntSet candidates = new IntArraySet();
    private final int[] seedTestCase;
    private final TestModel model;

    private int currentParam = -1;

    SimpleFixedVariableFinder(IntSet tabu, int[] seedTestCase, TestModel model) {
        free = new IntArraySet(tabu);
        this.seedTestCase = seedTestCase;
        this.model = model;

        for (int i = 0; i < model.getNumberOfParameters(); i++) {
            if (!free.contains(i)) {
                candidates.add(i);
            }
        }
    }

    @Override
    public Optional<int[]> runIteration(TestResult result) {
        if (result.isSuccessful()) {
            interaction.add(currentParam);
        } else {
            if (currentParam != -1) {
                free.add(currentParam);
            }
        }
        candidates.remove(currentParam);
        if (candidates.isEmpty()) {
            return Optional.empty();
        }
        currentParam = candidates.stream().findAny().get();
        IntSet modifiedParameters = new IntArraySet();
        if (currentParam != -1) {
            modifiedParameters.add(currentParam);
        }
        return Optional.of(getModifiedTestCase(modifiedParameters));

    }

    @Override
    public TestModel getModel() {
        return model;
    }

    @Override
    public int[] getSeedTestCase() {
        return seedTestCase;
    }


    @Override
    public IntSet getInteraction() {
        return interaction;
    }

    @Override
    public IntSet getFreeVariables() {
        return free;
    }
}
