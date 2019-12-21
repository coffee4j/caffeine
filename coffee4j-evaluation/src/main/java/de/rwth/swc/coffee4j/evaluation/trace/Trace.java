package de.rwth.swc.coffee4j.evaluation.trace;

import de.rwth.swc.coffee4j.evaluation.model.ScenarioIdentifier;
import de.rwth.swc.coffee4j.evaluation.model.TestScenario;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * A class containing the results of the execution of a fault characterization algorithm.
 * <p>
 * Each trace consists of:
 * <ul>
 *     <li> an identifier containing names of the model, scenario and algorithm
 *     <li> the results for each iteration
 * </ul>
 * <p>
 * Traces can only be constructed through the inner {@link Builder}.
 */
public final class Trace {

    private final TraceIdentifier identifier;
    private final List<TraceIteration> traceIterations;

    private Trace(Builder builder) {
        this.identifier = new TraceIdentifier(builder.scenarioIdentifier, builder.algorithmName);
        this.traceIterations = Objects.requireNonNull(builder.traceIterations);
    }

    /**
     * Gets the identifier for this trace.
     *
     * @return the trace identifier
     */
    public TraceIdentifier getIdentifier() {
        return this.identifier;
    }

    /**
     * Gets an immutable copy of the trace iterations.
     * <p>
     * The iterations are in order of execution.
     *
     * @return the iteration traces
     */
    public List<TraceIteration> getTraceIterations() {
        return Collections.unmodifiableList(traceIterations);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Trace) {
            Trace other = (Trace) obj;
            return this.identifier.equals(other.identifier) && this.traceIterations.equals(other.traceIterations);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return identifier.hashCode();
    }

    @Override
    public String toString() {
        return identifier.toString();
    }

    /**
     * Builder class for traces.
     * <p>
     * The only required fields are either {@link #fromIdentifier(TraceIdentifier)} and {@link #fromAlgorithm(String)}
     * or {@link #fromIdentifier(TraceIdentifier)} to set the necessary relations to the evaluated models.
     */
    public static class Builder {

        private final List<TraceIteration> traceIterations = new ArrayList<>();
        private ScenarioIdentifier scenarioIdentifier;
        private String algorithmName;
        private TraceIteration.Builder currentIteration;


        /**
         * Sets the evaluated scenario.
         *
         * @param testScenario the scenario
         * @return the builder for method chaining
         */
        public Builder fromScenario(TestScenario testScenario) {
            this.scenarioIdentifier = testScenario.getIdentifier();
            return this;
        }


        /**
         * Sets the used algorithm.
         *
         * @param algorithm the algorithm name
         * @return the builder for method chaining
         */
        public Builder fromAlgorithm(String algorithm) {
            this.algorithmName = algorithm;
            return this;
        }

        /**
         * Sets the used scenario and algorithm to the one contained in the identifier.
         *
         * @param identifier the trace identifier
         * @return the builder for method chaining
         */
        public Builder fromIdentifier(TraceIdentifier identifier) {
            this.scenarioIdentifier = identifier.getScenarioIdentifier();
            this.algorithmName = identifier.getAlgorithmName();
            return this;
        }

        /**
         * Starts a new trace iteration.
         *
         * @return the builder for the trace iteration
         */
        public TraceIteration.Builder iteration() {
            if (currentIteration != null) {
                traceIterations.add(currentIteration.getResult());
            }
            currentIteration = new TraceIteration.Builder(this);
            return currentIteration;
        }

        /**
         * Create the trace with the stored attributes.
         *
         * @return the trace
         */
        public Trace build() {
            if (currentIteration != null) {
                traceIterations.add(currentIteration.getResult());
            }
            return new Trace(this);
        }

    }
}
