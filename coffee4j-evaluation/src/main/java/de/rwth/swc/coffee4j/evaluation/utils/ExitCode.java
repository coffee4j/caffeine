package de.rwth.swc.coffee4j.evaluation.utils;

/**
 * Enum representing exit codes for command execution.
 */
public enum ExitCode {
    /**
     * The command execution was successful.
     */
    SUCCESS(0),
    /**
     * The command execution terminated with an unspecified error.
     */
    ERROR(1),
    /**
     * The command was called with invalid input.
     */
    INVALID_INPUT(2),
    /**
     * The command terminated because it ran out of memory.
     */
    MEMORY_OUT(27);

    private final int code;

    ExitCode(int code) {
        this.code = code;
    }

    /**
     * Gets the numerical representation of this exit code.
     *
     * @return an integer representing the exit code
     */
    public int exitCode() {
        return code;
    }
}
