package de.rwth.swc.coffee4j.evaluation.adapter.command;

import picocli.CommandLine;

/**
 * Main entry point for the jar file.
 */
public class Main {

    /**
     * Entry point for the jar file.
     * <p>
     * The given arguments are parsed and the respective command executed.
     * Upon termination the method will call {@link System#exit(int)} with a status code indicating the outcome.
     * Options are 0 for success, 1 for a general error, 2 for invalid command syntax, and 27 for an {@link OutOfMemoryError}.
     *
     * @param args the command line arguments.
     */
    public static void main(String[] args) {
        CommandLine cmd = new CommandLine(new Coffee4JAdapterCommand());
        cmd.setCaseInsensitiveEnumValuesAllowed(true);
        try {
            int exitCode = cmd.execute(args);
            System.exit(exitCode);
        } catch (OutOfMemoryError e) {
            System.exit(27);
        }
    }

}
