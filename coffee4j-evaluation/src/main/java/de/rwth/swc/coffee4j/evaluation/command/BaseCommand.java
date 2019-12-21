package de.rwth.swc.coffee4j.evaluation.command;

import de.rwth.swc.coffee4j.evaluation.command.options.LogOptions;
import de.rwth.swc.coffee4j.evaluation.utils.ExitCode;
import picocli.CommandLine;

import java.util.concurrent.Callable;

/**
 * Abstract base class for all commands of the benchmarking application.
 * <p>
 * It deals with providing the usage layout, and sets the given log level. It is intended to be subclassed by all
 * commands of the benchmark infrastructure.
 */
@CommandLine.Command(
        synopsisHeading = "%nUsage:%n",
        descriptionHeading = "%nDescription:%n",
        parameterListHeading = "%nParameters:%n",
        optionListHeading = "%nOptions:%n",
        commandListHeading = "%nCommands:%n")
public abstract class BaseCommand implements Callable<Integer> {

    @CommandLine.Mixin
    private LogOptions logOptions;

    @Override
    public Integer call() {
        logOptions.setLogLevel();
        return execute().exitCode();
    }

    /**
     * Execute this command.
     *
     * @return the exit code to be returned to the user.
     */
    protected abstract ExitCode execute();
}
