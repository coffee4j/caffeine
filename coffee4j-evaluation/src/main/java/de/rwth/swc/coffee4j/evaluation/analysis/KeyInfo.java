package de.rwth.swc.coffee4j.evaluation.analysis;

import java.util.Collections;
import java.util.List;

/**
 * Class containing soe information about the stored {@link PropertyKey} instances.
 * <p>
 * This is manly useful for later visualization. The keys are separated into model, trace and analysis keys according to
 * their usage.
 */
public class KeyInfo {

    private final List<PropertyKey> modelKeys;
    private final List<PropertyKey> scenarioKeys;
    private final List<PropertyKey> traceKeys;

    /**
     * Constructor.
     *
     * @param modelKeys    the keys used in model analyses
     * @param scenarioKeys the keys used in scenario analyses
     * @param traceKeys    the keys used in trace analyses
     */
    public KeyInfo(List<PropertyKey> modelKeys, List<PropertyKey> scenarioKeys, List<PropertyKey> traceKeys) {
        this.modelKeys = modelKeys != null ? modelKeys : Collections.emptyList();
        this.scenarioKeys = scenarioKeys != null ? scenarioKeys : Collections.emptyList();
        this.traceKeys = traceKeys != null ? traceKeys : Collections.emptyList();
    }

    /**
     * Gets all keys used by model analyses.
     *
     * @return the keys
     */
    public List<PropertyKey> getModelKeys() {
        return modelKeys;
    }

    /**
     * Gets all keys used by scenario analyses.
     *
     * @return the keys
     */
    public List<PropertyKey> getScenarioKeys() {
        return scenarioKeys;
    }

    /**
     * Gets all keys used by trace analyses.
     *
     * @return the keys
     */
    public List<PropertyKey> getTraceKeys() {
        return traceKeys;
    }

    @Override
    public String toString() {
        return List.of(modelKeys, scenarioKeys, traceKeys).toString();
    }
}
