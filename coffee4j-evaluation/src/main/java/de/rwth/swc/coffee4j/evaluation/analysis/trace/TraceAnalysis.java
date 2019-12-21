package de.rwth.swc.coffee4j.evaluation.analysis.trace;

import de.rwth.swc.coffee4j.evaluation.analysis.PropertyKey;
import de.rwth.swc.coffee4j.evaluation.model.TestModel;
import de.rwth.swc.coffee4j.evaluation.model.TestScenario;
import de.rwth.swc.coffee4j.evaluation.trace.Trace;
import de.rwth.swc.coffee4j.evaluation.trace.TraceIteration;

import java.util.Map;

/**
 * Analysis that computes a number of metrics for a {@link Trace}.
 */
public interface TraceAnalysis {

    /**
     * Analyze the given trace.
     *
     * @param model        the model the trace belongs to. It must not be {@code null}.
     * @param testScenario the scenario the trace belongs to. It must not be {@code null}.
     * @param trace        the trace to analyze. It must not be {@code null}.
     * @param iteration    the iteration. It must not be {@code null}.
     * @return the calculated metrics
     */
    Map<PropertyKey, Number> analyze(TestModel model, TestScenario testScenario, Trace trace, TraceIteration iteration);

}
