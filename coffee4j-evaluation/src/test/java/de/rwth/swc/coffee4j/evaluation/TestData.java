package de.rwth.swc.coffee4j.evaluation;

import de.rwth.swc.coffee4j.evaluation.analysis.KeyInfo;
import de.rwth.swc.coffee4j.evaluation.analysis.PropertyKey;
import de.rwth.swc.coffee4j.evaluation.analysis.model.ModelProperties;
import de.rwth.swc.coffee4j.evaluation.analysis.scenario.ScenarioProperties;
import de.rwth.swc.coffee4j.evaluation.analysis.trace.TraceProperties;
import de.rwth.swc.coffee4j.evaluation.model.TestModel;
import de.rwth.swc.coffee4j.evaluation.trace.ExecutionState;
import de.rwth.swc.coffee4j.evaluation.trace.Trace;

import java.util.List;
import java.util.Map;

public interface TestData {

    String ALGORITHM_1 = "ALGORITHM_1";
    String ALGORITHM_2 = "ALGORITHM_2";

    TestModel MODEL_1 = new TestModel.Builder("MODEL_1")
            .withParameters(2, 3, 4)
            .withConstraint("C0", 0, 0, 0)
            .withConstraint("C1", 0, 0, 1)
            .withConstraint("C2", -1, 2, 2)
            .withConstraint("C3", -1, 2, 3)
            .scenario("S0")
            .withStrength(2)
            .withFault("C0")
            .withConstraints("C1", "C2")
            .buildScenario()
            .scenario("S1")
            .withStrength(2)
            .withFaults("C0", "C1", "C2")
            .buildScenario()
            .scenario("S2")
            .withStrength(3)
            .withFaults("C1", "C2")
            .buildScenario()
            .buildModel();

    TestModel MODEL_2 = new TestModel.Builder("MODEL_2")
            .withParameters(10)
            .withConstraint("C0", 0)
            .withConstraint("C1", 1)
            .scenario("S0")
            .withStrength(1)
            .withFault("C0")
            .withConstraints("C1")
            .buildScenario()
            .buildModel();

    TestModel MODEL_LARGE = new TestModel.Builder("MODEL_LARGE")
            .withParameters(4, 4, 4, 5, 5, 4, 4, 4)
            .withConstraint("C0", -1, -1, -1, -1, -1, -1, 0, 0)
            .withConstraint("C1", 1, 1, -1, -1, -1, -1, -1, -1)
            .scenario("S0")
            .withFaults("C0", "C1")
            .buildScenario()
            .buildModel();

    TestModel MODEL_EMPTY = new TestModel.Builder("MODEL_EMPTY")
            .withParameters(2, 2, 2)
            .buildModel();

    TestModel MODEL_EMPTY_SCENARIO = new TestModel.Builder("MODEL_EMPTY_SCENARIO")
            .withParameters(10)
            .withConstraint("C0", 0)
            .withConstraint("C1", 1)
            .scenario("S0")
            .withStrength(1)
            .buildScenario()
            .buildModel();

    Trace TRACE_1 = new Trace.Builder()
            .fromScenario(MODEL_1.getScenario("S0"))
            .fromAlgorithm(ALGORITHM_1)
            .iteration()
            .testCase(0, 0, 0)
            .testCase(0, 0, 1)
            .testCase(0, 1, 0)
            .testCase(1, 0, 0)
            .failureInducingCombination(1, 1, 1)
            .failureInducingCombination(1, 1, 2)
            .satisfiedAssumption("A1")
            .violatedAssumption("A2")
        .timeNano(1000)
            .complete()
            .iteration()
            .timeOut()
            .iteration()
            .memoryOut()
            .iteration()
            .invalid()
            .build();

    Trace TRACE_2 = new Trace.Builder()
            .fromScenario(MODEL_1.getScenario("S1"))
            .fromAlgorithm(ALGORITHM_1)
            .iteration()
            .testCase(0, 0, 0)
            .testCase(0, 0, 1)
            .testCase(0, 1, 0)
            .testCase(1, 0, 0)
            .failureInducingCombination(1, 1, 1)
            .failureInducingCombination(1, 1, 2)
            .satisfiedAssumption("A1")
            .violatedAssumption("A2")
        .timeNano(1000)
            .complete()
            .iteration()
            .timeOut()
            .iteration()
            .memoryOut()
            .iteration()
            .invalid()
            .build();

    Trace TRACE_3 = new Trace.Builder()
            .fromScenario(MODEL_2.getScenario("S0"))
            .fromAlgorithm(ALGORITHM_1)
            .iteration()
            .testCase(0)
            .testCase(0)
            .testCase(0)
            .testCase(1)
            .failureInducingCombination(1)
            .failureInducingCombination(2)
        .timeNano(10)
            .complete()
            .iteration()
            .testCase(1)
            .testCase(0)
            .testCase(0)
            .testCase(1)
            .failureInducingCombination(1)
            .failureInducingCombination(0)
        .timeNano(20)
            .complete()
            .build();

    Trace TRACE_INVALID_ITERATION = new Trace.Builder()
            .fromScenario(MODEL_1.getScenario("S0"))
            .fromAlgorithm(ALGORITHM_2)
            .iteration()
            .invalid()
            .build();

    Trace TRACE_LARGE = new Trace.Builder()
            .fromScenario(MODEL_LARGE.getScenario("S0"))
            .fromAlgorithm(ALGORITHM_1)
            .iteration()
            .testCase(0, 0, 0, 0, 0, 0, 0, 0)
            .failureInducingCombination(-1, -1, -1, -1, -1, -1, 0, 0)
            .failureInducingCombination(1, -1, -1, -1, -1, -1, -1, -1)
            .complete()
            .build();

    PropertyKey M_BOUNDED_INT_KEY = new PropertyKey("M_BoundedInt", Integer.class, -100, 200);
    PropertyKey M_UNBOUNDED_INT_KEY = new PropertyKey("M_UnboundedInt", Integer.class, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
    PropertyKey M_BOUNDED_DOUBLE_KEY = new PropertyKey("M_BoundedDouble", Double.class, 22.2, 44.4);
    PropertyKey M_HALF_OPEN_LONG = new PropertyKey("M_HalfOpenLong", Long.class, 0, Double.POSITIVE_INFINITY);

    List<PropertyKey> MODEL_KEYS = List.of(M_BOUNDED_DOUBLE_KEY, M_UNBOUNDED_INT_KEY, M_BOUNDED_DOUBLE_KEY, M_HALF_OPEN_LONG);

    PropertyKey S_BOOLEAN_KEY = new PropertyKey("S_Boolean", Boolean.class, 0, 1);

    List<PropertyKey> SCENARIO_KEYS = List.of(S_BOOLEAN_KEY);

    PropertyKey T_A_KEY = new PropertyKey("T_A", Double.class, 0, 1);
    PropertyKey T_B_KEY = new PropertyKey("T_B", Integer.class, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
    PropertyKey T_C_KEY = new PropertyKey("T_C", Double.class, 0, 100);

    List<PropertyKey> TRACE_KEYS = List.of(T_A_KEY, T_B_KEY, T_C_KEY);

    KeyInfo KEY_INFO = new KeyInfo(MODEL_KEYS, SCENARIO_KEYS, TRACE_KEYS);


    ModelProperties PROP_MODEL_1 = new ModelProperties(MODEL_1.getIdentifier(),
            Map.of(M_BOUNDED_INT_KEY, 14,
                    M_UNBOUNDED_INT_KEY, Integer.MAX_VALUE,
                    M_BOUNDED_DOUBLE_KEY, 33.3,
                    M_HALF_OPEN_LONG, Long.MAX_VALUE));

    ScenarioProperties PROP_MODEL_1_S0 = new ScenarioProperties(MODEL_1.getScenario("S0").getIdentifier(),
            Map.of(S_BOOLEAN_KEY, 0));

    TraceProperties PROP_TRACE_1 = new TraceProperties(TRACE_1.getIdentifier(), List.of(
            new TraceProperties.IterationProperties(ExecutionState.COMPLETED, Map.of(T_A_KEY, 0.66, T_B_KEY, -2000.01, T_C_KEY, 44)),
            new TraceProperties.IterationProperties(ExecutionState.TIME_OUT, Map.of()),
            new TraceProperties.IterationProperties(ExecutionState.MEMORY_OUT, Map.of()),
            new TraceProperties.IterationProperties(ExecutionState.INVALID, Map.of(T_A_KEY, 0.66, T_B_KEY, -2000.01, T_C_KEY, 44))
    ));

    TraceProperties PROP_TRACE_INVALID_ITERATION = new TraceProperties(TRACE_INVALID_ITERATION.getIdentifier(),
            List.of(new TraceProperties.IterationProperties(ExecutionState.INVALID, Map.of())));

}
