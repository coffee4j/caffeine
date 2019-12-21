package de.rwth.swc.coffee4j.engine.constraint;

import de.rwth.swc.coffee4j.engine.TestModel;
import de.rwth.swc.coffee4j.engine.TupleList;
import de.rwth.swc.coffee4j.engine.util.CombinationUtil;
import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.ints.IntSet;
import org.chocosolver.solver.Model;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class DynamicHardConstraintChecker extends ModelBasedConstraintChecker {

    private final IntSet involvedParameters = new IntArraySet();
    private final InternalConstraintConverter converter = new InternalConstraintConverter();

    public DynamicHardConstraintChecker(final TestModel inputParameterModel, List<InternalConstraint> exclusionConstraints, List<InternalConstraint> errorConstraints) {
        super(createModel(inputParameterModel, exclusionConstraints, errorConstraints));
        inputParameterModel.getErrorTupleLists().stream().map(TupleList::getInvolvedParameters).flatMapToInt(Arrays::stream).forEach(involvedParameters::add);
        inputParameterModel.getForbiddenTupleLists().stream().map(TupleList::getInvolvedParameters).flatMapToInt(Arrays::stream).forEach(involvedParameters::add);
    }

    private static Model createModel(TestModel inputParameterModel, Collection<InternalConstraint> exclusionConstraints, Collection<InternalConstraint> errorConstraints) {
        final Model model = new Model();
        createVariables(inputParameterModel, model);
        createConstraints(exclusionConstraints, errorConstraints, model);

        return model;
    }

    private static void createVariables(TestModel inputParameterModel, Model model) {
        for (int i = 0; i < inputParameterModel.getNumberOfParameters(); i++) {
            int parameterSize = inputParameterModel.getParameterSizes()[i];
            String key = String.valueOf(i);

            model.intVar(key, 0, parameterSize - 1);
        }
    }

    private static void createConstraints(Collection<InternalConstraint> exclusionConstraints, Collection<InternalConstraint> errorConstraints, Model model) {
        for (InternalConstraint constraint : exclusionConstraints) {
            constraint.apply(model).post();
        }

        for (InternalConstraint errorConstraint : errorConstraints) {
            errorConstraint.apply(model).post();
        }
    }

    public void addConstraint(int[] forbiddenCombination) {
        int numberOfSetParameters = CombinationUtil.numberOfSetParameters(forbiddenCombination);
        if (numberOfSetParameters == 0) {
            model.post(model.falseConstraint());
        } else {
            int[] parameters = new int[numberOfSetParameters];
            int[] values = new int[numberOfSetParameters];
            int current = 0;
            for (int parameter = 0; parameter < forbiddenCombination.length; parameter++) {
                if (forbiddenCombination[parameter] != CombinationUtil.NO_VALUE) {
                    involvedParameters.add(parameter);
                    parameters[current] = parameter;
                    values[current] = forbiddenCombination[parameter];
                    current++;
                }
            }
            model.post(converter.createConstraints(parameters, values, model).getOpposite());
        }
    }

    public IntSet getInvolvedParameters() {
        return involvedParameters;
    }
}
