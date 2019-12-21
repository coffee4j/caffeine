package de.rwth.swc.coffee4j.evaluation.command.model;

import de.rwth.swc.coffee4j.evaluation.command.BaseCommand;
import de.rwth.swc.coffee4j.evaluation.command.options.VersionProvider;
import de.rwth.swc.coffee4j.evaluation.utils.ExitCode;
import picocli.CommandLine;

/**
 * Parent command for all sub-commands in the model domain.
 * <p>
 * Calling it directly returns an error.
 */
@CommandLine.Command(
        name = "model",
        subcommands = {
                ModelImportCommand.class,
                ModelExportCommand.class,
                ModelInfoCommand.class,
                ModelGenerateCommand.class,
                ModelClearCommand.class
        },
        versionProvider = VersionProvider.class,
        mixinStandardHelpOptions = true,
        description = "Parent command for all sub-commands manipulating test models."
)
public class ModelCommand extends BaseCommand {

    @CommandLine.Spec
    private CommandLine.Model.CommandSpec spec;

    @Override
    protected ExitCode execute() {
        throw new CommandLine.ParameterException(spec.commandLine(), "Missing required sub-command.");
    }
}
