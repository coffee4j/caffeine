package de.rwth.swc.coffee4j.evaluation.command;

import picocli.CommandLine;

/**
 * Entry point for the benchmarking application.
 * <p>
 * It delegates command parsing to picocli and then executes the parsed command. For testing it can be initialized with
 * different {@link picocli.CommandLine.IFactory} instances, to provide a different {@link DatabaseAdapterFactory} to
 * each {@link DatabaseCommand}.
 */
public class Main {

    private final CommandLine cmd;

    /**
     * Constructor.
     *
     * @param factory the factory creating all sub-commands
     */
    public Main(CommandLine.IFactory factory) {
        this.cmd = new CommandLine(new MainCommand(), factory);
    }

    /**
     * Entry point.
     *
     * @param args the command line args.
     */
    public static void main(String[] args) {
        Main main = new Main(new H2CommandFactory());
        System.exit(main.run(args));
    }

    /**
     * Execute the given command.
     *
     * @param args the command arguments
     * @return the exit code
     */
    public int run(String... args) {
        return cmd.execute(args);
    }

}
