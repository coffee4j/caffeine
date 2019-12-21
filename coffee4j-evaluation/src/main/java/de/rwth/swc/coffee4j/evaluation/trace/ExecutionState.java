package de.rwth.swc.coffee4j.evaluation.trace;

/**
 * Enum for the state of an algorithm execution.
 */
public enum ExecutionState {
    /**
     * The algorithm terminated successfully.
     */
    COMPLETED,
    /**
     * The execution was interrupted by a timeout.
     */
    TIME_OUT,
    /**
     * The execution terminated with a memory out.
     */
    MEMORY_OUT,
    /**
     * The execution terminated with some unspecified error.
     */
    INVALID
}
