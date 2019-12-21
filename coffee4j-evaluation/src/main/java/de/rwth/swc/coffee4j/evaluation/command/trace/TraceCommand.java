package de.rwth.swc.coffee4j.evaluation.command.trace;

import de.rwth.swc.coffee4j.evaluation.command.BaseCommand;
import de.rwth.swc.coffee4j.evaluation.command.options.VersionProvider;
import de.rwth.swc.coffee4j.evaluation.utils.ExitCode;
import picocli.CommandLine;

/**
 * Parent command for all sub-commands of the Trace component.
 * <p>
 * It cannot be executed directly.
 */
@CommandLine.Command(
        name = "trace",
        subcommands = {
                TraceRunCommand.class,
                TraceClearCommand.class,
                Coffee4jConfigCommand.class
        },
        versionProvider = VersionProvider.class,
        mixinStandardHelpOptions = true,
        description = "Parent command for all sub-commands tasked with tracing."
)
public class TraceCommand extends BaseCommand {

    @CommandLine.Spec
    private CommandLine.Model.CommandSpec spec;

    @Override
    protected ExitCode execute() {
        throw new CommandLine.ParameterException(spec.commandLine(), "Missing required sub-command.");
    }
}
