package de.rwth.swc.coffee4j.evaluation.command.analysis;

import de.rwth.swc.coffee4j.evaluation.command.BaseCommand;
import de.rwth.swc.coffee4j.evaluation.command.options.VersionProvider;
import de.rwth.swc.coffee4j.evaluation.utils.ExitCode;
import picocli.CommandLine;

/**
 * Parent command for all analysis commands.
 * <p>
 * Executing it directly will fail with a message requiring one of the sub-commands to be supplied.
 */
@CommandLine.Command(
        name = "analysis",
        subcommands = {
                AnalysisRunCommand.class,
                AnalysisExportCommand.class,
                AnalysisClearCommand.class
        },
        versionProvider = VersionProvider.class,
        mixinStandardHelpOptions = true,
        description = "Parent command for all analysis sub-commands."
)
public class AnalysisCommand extends BaseCommand {

    @CommandLine.Spec
    private CommandLine.Model.CommandSpec spec;

    @Override
    protected ExitCode execute() {
        throw new CommandLine.ParameterException(spec.commandLine(), "Missing required sub-command.");
    }
}