package de.rwth.swc.coffee4j.engine.characterization.csp;

import de.rwth.swc.coffee4j.engine.TestResult;
import de.rwth.swc.coffee4j.engine.characterization.FaultCharacterizationAlgorithm;
import de.rwth.swc.coffee4j.engine.characterization.FaultCharacterizationConfiguration;
import de.rwth.swc.coffee4j.engine.util.Preconditions;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.constraints.Constraint;
import org.chocosolver.solver.variables.IntVar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Csp is a static fault characterization algorithm that transforms the initial test suite into a pseudo-boolean
 * optimization problem. It is based on "Faulty Interaction Identification via Constraint Solving and Optimization"
 * <p>
 * It starts by assuming that the system contains a single fault of the form {@code (x1, x2, ..., xn)} where n is the
 * number of parameters. Then each test case is translated into a constraint. A successful test case {@code (t1, t2,
 * ..., tn)} is encoded as {@code (x1 != -1 && x1 != t1) || (x2 != -1 && x2 != ts) || ... || (xn != -1 && xn != tn)}. A
 * failing test case is translated to {@code (x1 = -1 || x1 = t1) && (x2 = -1 || x2 = ts) && ... && (xn = -1 || xn =
 * tn)}. If there is no assignement that satisfies the conjunction of these constraints, then the algorithm iteratively
 * adds new faults like {@code (y1, y2, ..., yn)} to the model. This is repeated until either a user-set maximum is
 * reached or a satisfying asssignment is found. This method guarantees that the covering array can be explained by the
 * lowest number of faults. To also reach the goal of returning the smallest faults, the problem is transformed into an
 * optimization problem. The objective function maximizes the number of -1 in each fault.
 * <p>
 * Important Information:
 * <ul>
 *     <li>Can only use data present in the initial covering array
 *     <li>Larger number of faults can quickly lead to long run times because the solving of the constraints can take a long time
 *     <li>Has no need for constraint handling because it does not generate new test cases
 * </ul>
 */
public class Csp implements FaultCharacterizationAlgorithm {

    private static final int MAX_NUMBER_OF_FICs = 5;
    private final List<IntVar[]> variables;
    private final List<IntVar> countVariables;
    private Model model;
    private final FaultCharacterizationConfiguration configuration;
    private Map<int[], TestResult> testResults;

    /**
     * Constructor.
     * <p>
     * The constraint checker is ignored.
     *
     * @param configuration the fault characterization configuration. This may not be {@code null}.
     */
    public Csp(FaultCharacterizationConfiguration configuration) {
        this.configuration = Preconditions.notNull(configuration);
        variables = new ArrayList<>();
        countVariables = new ArrayList<>();
    }

    private void buildVariables(int[] parameterSizes, int ficIndex) {
        variables.add(new IntVar[parameterSizes.length]);
        for (int i = 0; i < parameterSizes.length; i++) {
            variables.get(ficIndex)[i] = model.intVar(ficIndex + "-" + i, -1, parameterSizes[i] - 1);
        }
        IntVar count = model.intVar(0, variables.get(ficIndex).length);
        countVariables.add(count);
        model.count(-1, variables.get(ficIndex), count).post();
    }

    @Override
    public List<int[]> computeNextTestInputs(Map<int[], TestResult> testResults) {
        this.testResults = testResults;
        return Collections.emptyList();
    }

    private Constraint buildUnsuccessfulTest(int[] testCase, int numberOfFics) {
        Constraint[] ficConstraints = new Constraint[numberOfFics];
        for (int ficIndex = 0; ficIndex < numberOfFics; ficIndex++) {
            Constraint[] parameterConstraints = new Constraint[testCase.length];
            for (int parameter = 0; parameter < testCase.length; parameter++) {
                parameterConstraints[parameter] = model.or(
                        model.arithm(variables.get(ficIndex)[parameter], "=", -1),
                        model.arithm(variables.get(ficIndex)[parameter], "=", testCase[parameter]));
            }
            ficConstraints[ficIndex] = model.and(parameterConstraints);
        }
        return model.or(ficConstraints);
    }

    private Constraint buildSuccessfulTest(int[] testCase, int numberOfFics) {
        Constraint[] ficConstraints = new Constraint[numberOfFics];
        for (int ficIndex = 0; ficIndex < numberOfFics; ficIndex++) {
            Constraint[] parameterConstraints = new Constraint[testCase.length];
            for (int parameter = 0; parameter < testCase.length; parameter++) {
                parameterConstraints[parameter] = model.and(model.arithm(
                        variables.get(ficIndex)[parameter], "!=", -1),
                        model.arithm(variables.get(ficIndex)[parameter], "!=", testCase[parameter]));
            }
            ficConstraints[ficIndex] = model.or(parameterConstraints);
        }
        return model.and(ficConstraints);
    }

    private void buildModel(int numberOfFics) {
        this.variables.clear();
        this.countVariables.clear();
        this.model = new Model();
        for (int i = 0; i < numberOfFics; i++) {
            buildVariables(configuration.getTestModel().getParameterSizes(), i);
        }
        IntVar sum = model.intVar(0, variables.size() * configuration.getTestModel().getNumberOfParameters());
        this.model.sum(countVariables.toArray(new IntVar[0]), "=", sum).post();
        model.setObjective(true, sum);
    }

    @Override
    public List<int[]> computeFailureInducingCombinations() {
        boolean solutionFound = false;
        int numberOfFics = 1;
        while (!solutionFound && numberOfFics <= MAX_NUMBER_OF_FICs) {
            buildModel(numberOfFics);
            for (Map.Entry<int[], TestResult> entry : testResults.entrySet()) {
                if (entry.getValue().isSuccessful()) {
                    buildSuccessfulTest(entry.getKey(), numberOfFics).post();
                } else {
                    buildUnsuccessfulTest(entry.getKey(), numberOfFics).post();
                }
            }
            Solver solver = model.getSolver();
            solver.reset();
            solutionFound = solver.solve();
            numberOfFics++;
        }

        return getResults();
    }

    private List<int[]> getResults() {
        List<int[]> result = new ArrayList<>(variables.size());
        for (IntVar[] variable : variables) {
            int[] fic = new int[variable.length];
            for (int i = 0; i < fic.length; i++) {
                fic[i] = variable[i].getValue();
            }
            result.add(fic);
        }
        return result;
    }
}
