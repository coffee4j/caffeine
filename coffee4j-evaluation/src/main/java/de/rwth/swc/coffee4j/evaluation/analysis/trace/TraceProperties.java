package de.rwth.swc.coffee4j.evaluation.analysis.trace;

import de.rwth.swc.coffee4j.evaluation.analysis.PropertyKey;
import de.rwth.swc.coffee4j.evaluation.trace.ExecutionState;
import de.rwth.swc.coffee4j.evaluation.trace.TraceIdentifier;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Storage container for the results of a {@link TraceAnalyzer}.
 */
public class TraceProperties {

    private final TraceIdentifier identifier;
    private final List<IterationProperties> propertiesForIteration;

    /**
     * Constructor.
     *
     * @param identifier             the trace identifier. It must not be {@code null}.
     * @param propertiesForIteration the calculated metrics for each iteration. It must not be {@code null}.
     */
    public TraceProperties(TraceIdentifier identifier,
                           List<IterationProperties> propertiesForIteration) {
        this.identifier = Objects.requireNonNull(identifier);
        this.propertiesForIteration = Objects.requireNonNull(propertiesForIteration);
    }

    /**
     * Gets the trace identifier.
     *
     * @return the trace identifier
     */
    public TraceIdentifier getIdentifier() {
        return identifier;
    }

    /**
     * Gets the results for all iterations.
     *
     * @return the iteration results
     */
    public List<IterationProperties> getPropertiesForIteration() {
        return Collections.unmodifiableList(propertiesForIteration);
    }


    /**
     * Storage container for the results of each trace iteration.
     * <p>
     * The assumptions reported by the trace algorithm are kept intentionally separate from the calculated metrics
     * because the assumption keys can vary between the evaluated algorithms and therefore may be different for each
     * execution. Therefore, they may have to be handled differently during file storage and visualization.
     */
    public static class IterationProperties {

        private final ExecutionState state;
        private final Map<PropertyKey, Number> properties;

        /**
         * Constructor.
         *
         * @param state      the completion state of this iteration. It must not be {@code null}.
         * @param properties the calculated metrics. It must not be {@code null}.
         */
        public IterationProperties(ExecutionState state, Map<PropertyKey, Number> properties) {
            this.state = Objects.requireNonNull(state);
            this.properties = Objects.requireNonNull(properties);
        }

        /**
         * Gets the completion state of this trace iteration.
         *
         * @return the state
         */
        public ExecutionState getState() {
            return state;
        }

        /**
         * Gets an immutable copy of the calculated metrics.
         *
         * @return the calculated metrics
         */
        public Map<PropertyKey, Number> getProperties() {
            return Collections.unmodifiableMap(properties);
        }

    }
}
