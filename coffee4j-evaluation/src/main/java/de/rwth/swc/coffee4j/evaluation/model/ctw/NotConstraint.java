package de.rwth.swc.coffee4j.evaluation.model.ctw;

import java.util.Map;
import java.util.Set;

class NotConstraint implements Constraint {

    private final Constraint inner;

    NotConstraint(Constraint inner) {
        this.inner = inner;
    }

    @Override
    public Set<String> getInvolvedParameters() {
        return inner.getInvolvedParameters();
    }

    @Override
    public boolean test(Map<Parameter, Integer> assignment) {
        return !inner.test(assignment);
    }
}
