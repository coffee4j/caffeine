package de.rwth.swc.coffee4j.evaluation.analysis.scenario;

import de.rwth.swc.coffee4j.evaluation.analysis.PropertyKey;
import de.rwth.swc.coffee4j.evaluation.model.ScenarioIdentifier;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

/**
 * Storage container for the results of a {@link ScenarioAnalyzer}.
 */
public final class ScenarioProperties {

    private final ScenarioIdentifier identifier;
    private final Map<PropertyKey, Number> properties;

    /**
     * Constructor.
     *
     * @param identifier the identifier of the scenario
     * @param properties the calculated metrics
     */
    public ScenarioProperties(ScenarioIdentifier identifier, Map<PropertyKey, Number> properties) {
        this.identifier = Objects.requireNonNull(identifier);
        this.properties = Objects.requireNonNull(properties);
    }

    /**
     * Gets the identifier of the analyzed scenario.
     *
     * @return the identifier
     */
    public ScenarioIdentifier getIdentifier() {
        return identifier;
    }


    /**
     * Gets the calculated metrics.
     *
     * @return the calculated metrics
     */
    public Map<PropertyKey, Number> getProperties() {
        return Collections.unmodifiableMap(properties);
    }

}
