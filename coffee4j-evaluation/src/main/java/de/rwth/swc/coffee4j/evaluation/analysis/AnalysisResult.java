package de.rwth.swc.coffee4j.evaluation.analysis;

import de.rwth.swc.coffee4j.evaluation.analysis.model.ModelProperties;
import de.rwth.swc.coffee4j.evaluation.analysis.scenario.ScenarioProperties;
import de.rwth.swc.coffee4j.evaluation.analysis.trace.TraceProperties;

import java.util.Objects;


/**
 * Class which gathers {@link ModelProperties}, {@link ScenarioProperties}, {@link TraceProperties} to a single result.
 */
public class AnalysisResult {

    private final ModelProperties modelProperties;
    private final ScenarioProperties scenarioProperties;
    private final TraceProperties traceProperties;

    /**
     * Constructor.
     *
     * @param modelProperties    the properties for the model. It must not be {@code null}.
     * @param scenarioProperties the properties for the scenario. It must not be {@code null}.
     * @param traceProperties    the properties for the trace. It must not be {@code null}.
     */
    public AnalysisResult(ModelProperties modelProperties,
                          ScenarioProperties scenarioProperties,
                          TraceProperties traceProperties) {
        this.modelProperties = Objects.requireNonNull(modelProperties);
        this.scenarioProperties = Objects.requireNonNull(scenarioProperties);
        this.traceProperties = Objects.requireNonNull(traceProperties);
    }

    /**
     * Gets the stored trace properties.
     *
     * @return the trace properties
     */
    public TraceProperties getTraceProperties() {
        return traceProperties;
    }

    /**
     * Gets the stored model properties.
     *
     * @return the model properties
     */
    public ModelProperties getModelProperties() {
        return modelProperties;
    }

    /**
     * Gets the stored scenario properties.
     *
     * @return the scenario properties
     */
    public ScenarioProperties getScenarioProperties() {
        return scenarioProperties;
    }

}
