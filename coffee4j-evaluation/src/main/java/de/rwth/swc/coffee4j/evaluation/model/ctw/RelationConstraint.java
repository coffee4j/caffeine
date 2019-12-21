package de.rwth.swc.coffee4j.evaluation.model.ctw;

import java.util.Map;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class RelationConstraint implements Constraint {

    private final Parameter left;
    private final Parameter right;
    private final Operator op;

    RelationConstraint(Parameter left, Parameter right, Operator op) {
        this.left = left;
        this.right = right;
        this.op = op;
    }

    @Override
    public Set<String> getInvolvedParameters() {
        return Stream.of(left, right)
                .filter(i -> !(i instanceof FixedParameter))
                .map(Parameter::getName)
                .collect(Collectors.toSet());
    }

    @Override
    public boolean test(Map<Parameter, Integer> assignment) {
        return op.function.test(
                getValue(assignment, left),
                getValue(assignment, right));
    }

    private Integer getValue(Map<Parameter, Integer> assignment, Parameter left) {
        return left instanceof FixedParameter ? ((FixedParameter) left).getValue() : assignment.get(left);
    }

    enum Operator {
        GT((a, b) -> a > b),
        GE((a, b) -> a >= b),
        LE((a, b) -> a <= b),
        LT((a, b) -> a < b),
        EQ(Integer::equals),
        NEQ((a, b) -> !a.equals(b));

        private final BiPredicate<Integer, Integer> function;

        Operator(BiPredicate<Integer, Integer> function) {
            this.function = function;
        }
    }

}
