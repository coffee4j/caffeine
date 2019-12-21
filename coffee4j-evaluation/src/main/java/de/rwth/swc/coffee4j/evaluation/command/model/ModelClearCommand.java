package de.rwth.swc.coffee4j.evaluation.command.model;

import de.rwth.swc.coffee4j.evaluation.command.DatabaseAdapterFactory;
import de.rwth.swc.coffee4j.evaluation.command.DatabaseCommand;
import de.rwth.swc.coffee4j.evaluation.command.options.VersionProvider;
import de.rwth.swc.coffee4j.evaluation.persistence.DatabaseAdapter;
import de.rwth.swc.coffee4j.evaluation.utils.ExitCode;
import picocli.CommandLine;

/**
 * Command that clears all stored models.
 * <p>
 * Because of the way the storage is implemented, this may also clear all traces and analysis results.
 */
@CommandLine.Command(
        name = "clear",
        versionProvider = VersionProvider.class,
        mixinStandardHelpOptions = true,
        description = "Clear all stored models, traces and analyses."
)
public class ModelClearCommand extends DatabaseCommand {
    /**
     * Constructor with the database factory to match the {@link DatabaseCommand} signature.
     *
     * @param dbFactory the dbFactory. It must not be {@code null}.
     */
    public ModelClearCommand(DatabaseAdapterFactory dbFactory) {
        super(dbFactory);
    }

    @Override
    protected ExitCode execute(DatabaseAdapter db) {
        db.getModelRepository().clear();
        return ExitCode.SUCCESS;
    }
}
