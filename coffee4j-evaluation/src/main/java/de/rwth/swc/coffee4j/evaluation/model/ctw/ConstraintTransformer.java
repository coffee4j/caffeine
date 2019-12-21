package de.rwth.swc.coffee4j.evaluation.model.ctw;

import java.util.List;

interface ConstraintTransformer {

    List<int[]> transformConstraint(Constraint constraint);

}
