package de.rwth.swc.coffee4j.evaluation.analysis.trace;

import de.rwth.swc.coffee4j.evaluation.analysis.PropertyKey;
import de.rwth.swc.coffee4j.evaluation.model.TestModel;
import de.rwth.swc.coffee4j.evaluation.model.TestScenario;
import de.rwth.swc.coffee4j.evaluation.trace.ExecutionState;
import de.rwth.swc.coffee4j.evaluation.trace.Trace;
import de.rwth.swc.coffee4j.evaluation.trace.TraceIteration;

import java.util.*;

/**
 * Analyzer that uses given {@link TraceAnalysis} to compute the {@link TraceProperties} for a {@link Trace}.
 */
public class TraceAnalyzer {

    private final List<TraceAnalysis> analyses;

    /**
     * Constructor.
     *
     * @param analyses A list of analyses to use. It must not be {@code null}, nor contain {@code null}.
     */
    TraceAnalyzer(List<TraceAnalysis> analyses) {
        this.analyses = Objects.requireNonNull(analyses);
    }

    /**
     * Compute the trace performance metrics.
     *
     * @param model    the model the trace belongs to. It must not be {@code null}.
     * @param scenario the scenario the trace belongs to. It must not be {@code null}.
     * @param trace    the trace to analyze. It must not be {@code null}.
     * @return the computed trace properties
     */
    public TraceProperties analyze(TestModel model, TestScenario scenario, Trace trace) {
        Objects.requireNonNull(model);
        Objects.requireNonNull(scenario);
        Objects.requireNonNull(trace);
        Objects.requireNonNull(trace.getTraceIterations());

        List<TraceProperties.IterationProperties> iterationResults = new ArrayList<>();

        for (TraceIteration iteration : trace.getTraceIterations()) {
            Map<PropertyKey, Number> metrics = new HashMap<>();
            if (iteration.getState() == ExecutionState.COMPLETED) {
                for (TraceAnalysis analysis : analyses) {
                    metrics.putAll(analysis.analyze(model, scenario, trace, iteration));
                }
            }
            iterationResults.add(new TraceProperties.IterationProperties(iteration.getState(), metrics));
        }

        return new TraceProperties(trace.getIdentifier(), iterationResults);
    }

}
