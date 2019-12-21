package de.rwth.swc.coffee4j.evaluation.analysis.trace;

import de.rwth.swc.coffee4j.evaluation.analysis.PropertyKey;
import de.rwth.swc.coffee4j.evaluation.model.TestModel;
import de.rwth.swc.coffee4j.evaluation.model.TestScenario;
import de.rwth.swc.coffee4j.evaluation.trace.Trace;
import de.rwth.swc.coffee4j.evaluation.trace.TraceIteration;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * {@link TraceAnalysis} that copies the runtime assumptions from the trace to the analysis properties.
 * <p>
 * It also handles the transformation of boolean states into {@link Number} instances. If an iteration contains multiple
 * entries for an assumption, the latest one is chosen.
 */
public class AssumptionsAnalysis implements TraceAnalysis {

    @Override
    public Map<PropertyKey, Number> analyze(TestModel model,
                                            TestScenario testScenario,
                                            Trace trace, TraceIteration iteration) {
        return iteration.getAssumptions()
                .entrySet()
                .stream()
                .collect(Collectors.toMap(e -> PropertyKey.bool(e.getKey()), e -> e.getValue() ? 1 : 0));
    }

}
