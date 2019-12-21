package de.rwth.swc.coffee4j.evaluation.analysis;

import de.rwth.swc.coffee4j.evaluation.analysis.model.ModelAnalyzer;
import de.rwth.swc.coffee4j.evaluation.analysis.model.ModelProperties;
import de.rwth.swc.coffee4j.evaluation.analysis.scenario.ScenarioAnalyzer;
import de.rwth.swc.coffee4j.evaluation.analysis.scenario.ScenarioProperties;
import de.rwth.swc.coffee4j.evaluation.analysis.trace.TraceAnalyzer;
import de.rwth.swc.coffee4j.evaluation.analysis.trace.TraceProperties;
import de.rwth.swc.coffee4j.evaluation.model.TestModel;
import de.rwth.swc.coffee4j.evaluation.model.TestScenario;
import de.rwth.swc.coffee4j.evaluation.trace.Trace;

import java.util.Objects;

/**
 * Analyzer that calculates the properties for a given combination of {@link TestModel}, {@link TestScenario}, and
 * {@link Trace}.
 */
public class Analyzer {

    private final ModelAnalyzer modelAnalyzer;
    private final ScenarioAnalyzer scenarioAnalyzer;
    private final TraceAnalyzer traceAnalyzer;

    /**
     * Constructor.
     *
     * @param modelAnalyzer    the model analyzer to use. It must not be {@code null}.
     * @param scenarioAnalyzer the scenario analyzer to use. It must not be {@code null}.
     * @param traceAnalyzer    the trace analyzer to use. It must not be {@code null}.
     */
    public Analyzer(ModelAnalyzer modelAnalyzer, ScenarioAnalyzer scenarioAnalyzer, TraceAnalyzer traceAnalyzer) {
        this.modelAnalyzer = Objects.requireNonNull(modelAnalyzer);
        this.scenarioAnalyzer = Objects.requireNonNull(scenarioAnalyzer);
        this.traceAnalyzer = Objects.requireNonNull(traceAnalyzer);
    }

    /**
     * Analyze a model.
     * <p>
     * This delegates to the stored {@link ModelAnalyzer}.
     *
     * @param model the model to analyze. It must not be {@code null}.
     * @return the calculated properties
     */
    public ModelProperties analyzeModel(TestModel model) {
        return this.modelAnalyzer.analyze(Objects.requireNonNull(model));
    }

    /**
     * Analyze a scenario.
     * <p>
     * This delegates to the stored {@link ScenarioAnalyzer}.
     *
     * @param model    the model to analyze. It must not be {@code null}.
     * @param scenario the scenario to analyze. It must not be {@code null}.
     * @return the calculated properties
     */
    public ScenarioProperties analyzeScenario(TestModel model, TestScenario scenario) {
        return this.scenarioAnalyzer.analyze(Objects.requireNonNull(model),
                Objects.requireNonNull(scenario));
    }

    /**
     * Analyze a trace.
     * <p>
     * This delegates to the stored {@link TraceAnalyzer}.
     *
     * @param model    the model to analyze. It must not be {@code null}.
     * @param scenario the scenario to analyze. It must not be {@code null}.
     * @param trace    the trace to analyze. It must not be {@code null}.
     * @return the calculated properties
     */
    public TraceProperties analyzeTrace(TestModel model, TestScenario scenario, Trace trace) {
        return traceAnalyzer.analyze(Objects.requireNonNull(model),
                Objects.requireNonNull(scenario),
                Objects.requireNonNull(trace));
    }

}
