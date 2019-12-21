package de.rwth.swc.coffee4j.evaluation.model.ctw;

import de.rwth.swc.coffee4j.evaluation.utils.Combinator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * {@link ConstraintTransformer} that creates the forbidden combinations by enumerating possible combinations with all
 * involved parameters.
 */
public class NaiveConstraintTransformer implements ConstraintTransformer {

    private final Map<String, Parameter> parameters;
    private final List<String> parameterOrder;

    /**
     * Constructor.
     *
     * @param parameters     a map of parameter names to parameters. It must not be {@code null}.
     * @param parameterOrder the order of parameters. It must not be {@code null}.
     */
    public NaiveConstraintTransformer(Map<String, Parameter> parameters, List<String> parameterOrder) {
        this.parameters = Objects.requireNonNull(parameters);
        this.parameterOrder = Objects.requireNonNull(parameterOrder);
    }


    @Override
    public List<int[]> transformConstraint(Constraint constraint) {
        List<Parameter> involved = constraint
                .getInvolvedParameters()
                .stream().map(parameters::get)
                .collect(Collectors.toList());

        int[] involvedSizes = involved.stream().mapToInt(Parameter::getNumberOfValues).toArray();

        List<int[]> forbiddenCombinations = new ArrayList<>();
        for (int[] combination : Combinator.computeCombinations(involvedSizes, involvedSizes.length)) {
            Map<Parameter, Integer> assignment = IntStream.range(0, involved.size()).boxed()
                    .collect(Collectors.toMap(involved::get, i -> combination[i]));
            if (constraint.test(assignment)) {
                forbiddenCombinations.add(createCombination(assignment));
            }
        }

        return forbiddenCombinations;
    }

    private int[] createCombination(Map<Parameter, Integer> assignment) {
        return parameterOrder.stream().map(parameters::get)
                .mapToInt(p -> assignment.getOrDefault(p, -1))
                .toArray();
    }
}
