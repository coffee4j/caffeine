package de.rwth.swc.coffee4j.evaluation.trace;

import java.time.Duration;
import java.util.*;

/**
 * Configuration for an {@link ExecutionTracer}.
 * <p>
 * It contains a mapping from algorithm name to the respective commands, the number of trace iterations, the timeout
 * duration, and whether the execution should ignore constraints.
 * <p>
 * A negative or zero timeout duration runs the trace without a timeout.
 */
public final class TraceConfiguration {

    private final Map<String, String> commands;
    private final int iterations;
    private final Duration timeout;
    private final boolean ignoreConstraints;

    /**
     * Constructor.
     * <p>
     * Negative or zero timeout duration runs the trace without a timeout.
     *
     * @param commands          A map from algorithm name to their commands
     * @param iterations        the number of iterations
     * @param timeout           the timeout duration
     * @param ignoreConstraints if the execution should ignore constraints
     */
    public TraceConfiguration(Map<String, String> commands,
                              int iterations,
                              Duration timeout, boolean ignoreConstraints) {
        this.commands = Objects.requireNonNull(commands);
        this.ignoreConstraints = ignoreConstraints;
        this.iterations = iterations;
        this.timeout = Objects.requireNonNull(timeout);
    }

    /**
     * Gets the number of iterations.
     *
     * @return tne number of iterations
     */
    int getNumberOfIterations() {
        return iterations;
    }

    /**
     * Gets the timeout duration.
     * <p>
     * A negative or zero timeout duration runs the trace without a timeout.
     *
     * @return the timeout duration
     */
    Duration getTimeout() {
        return timeout;
    }

    /**
     * Gets a command for an algorithm.
     *
     * @param algorithmName the algorithm name
     * @return the command separated by empty spaces
     * @throws NoSuchElementException if no such algorithm with this name exists in the configuration
     */
    String[] getCommand(String algorithmName) {
        return Optional.ofNullable(commands.get(algorithmName))
                .map(this::prepareCommand)
                .orElseThrow(() -> new NoSuchElementException("Command " + algorithmName + "not found."));

    }

    /**
     * Gets all algorithms that have a command registered.
     *
     * @return a set of all algorithms
     */
    public Set<String> getAllAlgorithms() {
        return commands.keySet();
    }

    /**
     * Checks whether the execution should ignore constraints.
     *
     * @return whether the execution should ignore constraints
     */
    boolean isIgnoreConstraints() {
        return ignoreConstraints;
    }

    private String[] prepareCommand(String command) {
        return command.split(" ");
    }
}
