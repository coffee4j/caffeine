package de.rwth.swc.coffee4j.evaluation.trace;

import de.rwth.swc.coffee4j.evaluation.utils.IntArrayWrapper;

import java.util.*;

/**
 * Class that contains the information about a single execution of a fault characterization algorithm.
 * <p>
 * It stores the execution state, the execution time in milliseconds, the used test cases and the found failure inducing
 * combinations.
 * <p>
 * Additionally, it contains a map for each dynamic assumption that was reported during the execution. All fields may
 * have an empty default value for invalid, time-out or memory-out states. This includes a 0 for the execution time and
 * empty collections for the other fields.
 */
public final class TraceIteration {

    private final ExecutionState state;
    private final long executionTime;
    private final List<int[]> testCases;
    private final List<int[]> failureInducingCombinations;
    private final Map<String, Boolean> assumptions;

    private TraceIteration(Builder builder) {
        this.state = builder.state;
        this.executionTime = builder.executionTime;
        this.testCases = Objects.requireNonNull(builder.testCases);
        this.failureInducingCombinations = Objects.requireNonNull(builder.failureInducingCombinations);
        this.assumptions = Objects.requireNonNull(builder.assumptions);
    }

    /**
     * Gets the execution state.
     *
     * @return the execution state.
     */
    public ExecutionState getState() {
        return state;
    }

    /**
     * Gets the execution time in milliseconds.
     * <p>
     * May be 0 for not completed traces.
     *
     * @return the execution time
     */
    public long getExecutionTime() {
        return executionTime;
    }

    /**
     * Gets an immutable copy of the test cases used during the execution.
     * <p>
     * The list elements are given in order of occurrence and the list may contain duplicates. May be empty for not
     * completed traces.
     *
     * @return the used test cases
     */
    public List<int[]> getTestCases() {
        return Collections.unmodifiableList(testCases);
    }

    /**
     * Gets an immutable copy of the failure inducing combinations returned during the execution.
     * <p>
     * The list elements are given in order of occurrence and the list may contain duplicates. May be empty for not
     * completed traces.
     *
     * @return the found failure inducing combinations
     */
    public List<int[]> getFailureInducingCombinations() {
        return Collections.unmodifiableList(failureInducingCombinations);
    }

    /**
     * Gets an immutable copy of the assumptions reported during the execution.
     * <p>
     * Each assumption is given as a map entry with a unique key per assumption and {@code true} if the assumption was
     * satisfied and {@code false} if it was violated.
     *
     * @return the assumptions
     */
    public Map<String, Boolean> getAssumptions() {
        return Collections.unmodifiableMap(assumptions);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof TraceIteration) {
            TraceIteration other = (TraceIteration) obj;
            return this.state.equals(other.state) &&
                    this.executionTime == other.executionTime &&
                    this.assumptions.equals(other.assumptions) &&
                    IntArrayWrapper.wrapToList(this.testCases)
                            .equals(IntArrayWrapper.wrapToList(other.testCases)) &&
                    IntArrayWrapper.wrapToList(this.failureInducingCombinations)
                            .equals(IntArrayWrapper.wrapToList(other.failureInducingCombinations));
        }
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return Objects.hash(state, executionTime,
                IntArrayWrapper.wrapToList(testCases),
                IntArrayWrapper.wrapToList(failureInducingCombinations),
                assumptions);
    }

    /**
     * A builder for iteration traces.
     * <p>
     * It can only be called from the parent {@link Trace.Builder}. This guarantees no iterations without a parent
     * trace.
     */
    public static class Builder {
        private final Trace.Builder parent;
        private final List<int[]> testCases = new ArrayList<>();
        private final List<int[]> failureInducingCombinations = new ArrayList<>();
        private final Map<String, Boolean> assumptions = new HashMap<>();
        private ExecutionState state;
        private long executionTime = 0;

        Builder(Trace.Builder parent) {
            this.parent = Objects.requireNonNull(parent);
        }

        /**
         * Adds a test case.
         *
         * @param testCase the test case
         * @return the builder for method chaining
         */
        public Builder testCase(int... testCase) {
            testCases.add(testCase);
            return this;
        }

        /**
         * Adds a failure inducing combination.
         *
         * @param combination the failure inducing combination
         * @return the builder for method chaining
         */
        public Builder failureInducingCombination(int... combination) {
            failureInducingCombinations.add(combination);
            return this;
        }

        /**
         * Sets the execution time in nanoseconds.
         * <p>
         * It will be converted to milliseconds internally with the expected loss of precision. This mostly does not
         * matter because nanosecond resolution is well within margin of error for execution tracing.
         *
         * @param executionTime the time in nanoseconds
         * @return the builder for method chaining
         */
        public Builder timeNano(long executionTime) {
            this.executionTime = executionTime / 1000000;
            return this;
        }

        /**
         * Sets the execution time in milliseconds.
         *
         * @param executionTime the time in milliseconds
         * @return the builder for method chaining
         */
        public Builder timeMilli(long executionTime) {
            this.executionTime = executionTime;
            return this;
        }

        /**
         * Adds a satisfied assumption.
         *
         * @param key the key for this assumption
         * @return the builder for method chaining
         */
        public Builder satisfiedAssumption(String key) {
            assumptions.put(key, true);
            return this;
        }

        /**
         * Adds a violated assumption.
         *
         * @param key the key for this assumption
         * @return the builder for method chaining
         */
        public Builder violatedAssumption(String key) {
            assumptions.put(key, false);
            return this;
        }

        /**
         * Adds a assumption with a boolean parameter that indicates whether it is satisfied or not.
         *
         * @param key       the key for this assumption
         * @param satisfied {@code true} if the assumption was satisfied, {@code false} if it was violated
         * @return the builder for method chaining
         */
        public Builder assumption(String key, boolean satisfied) {
            assumptions.put(key, satisfied);
            return this;
        }

        /**
         * Complete this iteration and mark the execution state as complete.
         * <p>
         * It returns the parent builder to continue with further iterations.
         *
         * @return the parent builder
         */
        public Trace.Builder complete() {
            this.state = ExecutionState.COMPLETED;
            return parent;
        }


        /**
         * Complete this iteration and mark the execution state as a timeout.
         * <p>
         * This may lead to not storing any of the previously given information. It returns the parent builder to
         * continue with further iterations.
         *
         * @return the parent builder
         */
        public Trace.Builder timeOut() {
            this.state = ExecutionState.TIME_OUT;
            return parent;
        }

        /**
         * Complete this iteration and mark the execution state as a memory out.
         * <p>
         * This may lead to not storing any of the previously given information. It returns the parent builder to
         * continue with further iterations.
         *
         * @return the parent builder
         */
        public Trace.Builder memoryOut() {
            this.state = ExecutionState.MEMORY_OUT;
            return parent;
        }

        /**
         * Complete this iteration and mark the execution state as invalid.
         * <p>
         * This may lead to not storing any of the previously given information. It returns the parent builder to
         * continue with further iterations.
         *
         * @return the parent builder
         */
        public Trace.Builder invalid() {
            this.state = ExecutionState.INVALID;
            return parent;
        }

        /**
         * Complete this iteration and mark the execution state.
         * <p>
         * Any state other than {@code COMPLETED} may lead to not storing any of the previously given information. It
         * returns the parent builder to continue with further iterations.
         *
         * @param state the execution state
         * @return the parent builder
         */
        public Trace.Builder state(ExecutionState state) {
            this.state = state;
            return parent;
        }

        /**
         * Gets a new iteration from the stored data.
         *
         * @return the trace iteration
         */
        TraceIteration getResult() {
            return new TraceIteration(this);
        }

    }

}
