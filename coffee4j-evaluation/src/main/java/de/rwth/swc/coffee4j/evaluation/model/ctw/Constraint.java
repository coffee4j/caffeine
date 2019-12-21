package de.rwth.swc.coffee4j.evaluation.model.ctw;

import java.util.Map;
import java.util.Set;

interface Constraint {

    Set<String> getInvolvedParameters();

    boolean test(Map<Parameter, Integer> assignment);

}
