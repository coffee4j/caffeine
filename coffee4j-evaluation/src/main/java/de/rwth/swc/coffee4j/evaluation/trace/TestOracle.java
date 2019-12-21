package de.rwth.swc.coffee4j.evaluation.trace;

import de.rwth.swc.coffee4j.evaluation.model.TestModel;
import de.rwth.swc.coffee4j.evaluation.model.TestScenario;
import de.rwth.swc.coffee4j.evaluation.utils.CombinationUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Class for representing a testing oracle.
 * <p>
 * It returns the test results for test cases based on the stored failure inducing combinations and constraints. All
 * constraints are treated as error constraints, this means that a constraint violation is treated the same as a failed
 * test case. It also includes an option for ignoring constraints.
 */
public final class TestOracle {

    private final Map<String, int[]> faults;

    TestOracle(TestModel model, TestScenario testScenario, boolean ignoreConstraints) {
        this.faults = new HashMap<>(model.getFaultsForScenario(testScenario));
        if (!ignoreConstraints) {
            this.faults.putAll(model.getConstraintsForScenario(testScenario));
        }
    }

    /**
     * Gets the rest result for a test case.
     *
     * @param testCase the test case to check
     * @return the result of executing the test case
     */
    public TestResult getTestResult(int[] testCase) {
        return faults.entrySet().stream()
                .filter(fault -> CombinationUtil.contains(testCase, fault.getValue()))
                .findFirst().map(fault -> TestResult.failure(new IllegalArgumentException(fault.getKey())))
                .orElse(TestResult.success());
    }

}
