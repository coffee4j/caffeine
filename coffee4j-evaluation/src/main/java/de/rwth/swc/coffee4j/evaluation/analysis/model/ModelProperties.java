package de.rwth.swc.coffee4j.evaluation.analysis.model;

import de.rwth.swc.coffee4j.evaluation.analysis.PropertyKey;
import de.rwth.swc.coffee4j.evaluation.model.ModelIdentifier;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

/**
 * Storage container for the results of a {@link ModelAnalyzer}.
 * <p>
 * It contains an {@link ModelIdentifier} that binds it to a certain {@link de.rwth.swc.coffee4j.evaluation.model.TestModel}.
 * Moreover, it stores all context metrics that were computed for this model in a map.
 */
public final class ModelProperties {

    private final ModelIdentifier identifier;
    private final Map<PropertyKey, Number> properties;

    /**
     * Constructor.
     *
     * @param identifier the identifier of the model. It must not be {@code null}.
     * @param properties a map of computed metrics. It must not be {@code null}, nor contain {@code null}.
     */
    public ModelProperties(ModelIdentifier identifier, Map<PropertyKey, Number> properties) {
        this.identifier = Objects.requireNonNull(identifier);
        this.properties = Objects.requireNonNull(properties);
    }

    /**
     * Gets the model identifier;
     *
     * @return the model identifier
     */
    public ModelIdentifier getIdentifier() {
        return identifier;
    }

    /**
     * Gets an immutable copy of the stored metrics.
     *
     * @return the metrics
     */
    public Map<PropertyKey, Number> getProperties() {
        return Collections.unmodifiableMap(properties);
    }

}
