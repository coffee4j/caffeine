package de.rwth.swc.coffee4j.engine.characterization.fic;

import de.rwth.swc.coffee4j.engine.TestModel;
import it.unimi.dsi.fastutil.ints.IntSet;

@FunctionalInterface
interface FixedVariableFinderFactory {


    FixedVariableFinder createFixedVariableFinder(IntSet tabu, int[] seedTestCase, TestModel model);

}
