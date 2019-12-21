package de.rwth.swc.coffee4j.engine.characterization.fic;

import de.rwth.swc.coffee4j.engine.TestModel;
import de.rwth.swc.coffee4j.engine.TestResult;
import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.ints.IntSet;

import java.util.Arrays;
import java.util.Optional;

interface FixedVariableFinder {

    Optional<int[]> runIteration(TestResult result);

    TestModel getModel();

    int[] getSeedTestCase();

    IntSet getInteraction();

    IntSet getFreeVariables();

    default int[] getModifiedTestCase(IntSet testParameters) {
        IntSet modifiedParameters = new IntArraySet(testParameters);
        modifiedParameters.addAll(getFreeVariables());
        int[] result = Arrays.copyOf(getSeedTestCase(), getSeedTestCase().length);
        for (int i : modifiedParameters) {
            result[i] = (result[i] + 1) % getModel().getSizeOfParameter(i);
        }
        return result;
    }

}
