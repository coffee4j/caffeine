package de.rwth.swc.coffee4j.evaluation.command;

import de.rwth.swc.coffee4j.evaluation.command.analysis.AnalysisCommand;
import de.rwth.swc.coffee4j.evaluation.command.model.ModelCommand;
import de.rwth.swc.coffee4j.evaluation.command.options.VersionProvider;
import de.rwth.swc.coffee4j.evaluation.command.trace.TraceCommand;
import de.rwth.swc.coffee4j.evaluation.utils.ExitCode;
import picocli.CommandLine;

/**
 * Parent command for all benchmarking commands.
 * <p>
 * It serves no purpose, other than to provide the necessary sub-commands. Calling it will throw an error and print a
 * help message.
 */
@CommandLine.Command(
        subcommands = {
                ModelCommand.class,
                TraceCommand.class,
                AnalysisCommand.class
        },
        versionProvider = VersionProvider.class,
        mixinStandardHelpOptions = true,
        description = "Parent command for evaluation commands."
)
public class MainCommand extends BaseCommand {

    @CommandLine.Spec
    private CommandLine.Model.CommandSpec spec;

    @Override
    protected ExitCode execute() {
        throw new CommandLine.ParameterException(spec.commandLine(), "Missing required sub-command.");
    }
}
