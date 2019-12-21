package de.rwth.swc.coffee4j.evaluation.model.ctw;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BinaryOperator;

class BinaryConstraint implements Constraint {

    private final Constraint left;
    private final Constraint right;
    private final Operator op;

    public BinaryConstraint(Constraint left, Constraint right, Operator op) {
        this.left = left;
        this.right = right;
        this.op = op;
    }


    @Override
    public Set<String> getInvolvedParameters() {
        Set<String> involved = new HashSet<>(left.getInvolvedParameters());
        involved.addAll(right.getInvolvedParameters());
        return involved;
    }

    @Override
    public boolean test(Map<Parameter, Integer> assignment) {
        return op.function.apply(left.test(assignment), right.test(assignment));
    }

    enum Operator {
        AND((a, b) -> a && b),
        OR((a, b) -> a || b),
        IMPLIES((a, b) -> !a || b),
        IFF(Boolean::equals);

        private final BinaryOperator<Boolean> function;


        Operator(BinaryOperator<Boolean> function) {
            this.function = function;
        }
    }
}
