package de.rwth.swc.coffee4j.evaluation.command.analysis;

import de.rwth.swc.coffee4j.evaluation.command.DatabaseAdapterFactory;
import de.rwth.swc.coffee4j.evaluation.command.DatabaseCommand;
import de.rwth.swc.coffee4j.evaluation.command.options.VersionProvider;
import de.rwth.swc.coffee4j.evaluation.persistence.DatabaseAdapter;
import de.rwth.swc.coffee4j.evaluation.utils.ExitCode;
import picocli.CommandLine;


/**
 * {@link DatabaseCommand} that clears the {@link de.rwth.swc.coffee4j.evaluation.persistence.analysis.AnalysisRepository}.
 */
@CommandLine.Command(
        name = "clear",
        versionProvider = VersionProvider.class,
        mixinStandardHelpOptions = true,
        description = "Clear all stored analyses."
)
public class AnalysisClearCommand extends DatabaseCommand {

    /**
     * Constructor with the database factory to match the {@link DatabaseCommand} signature.
     *
     * @param dbFactory the dbFactory. It must not be {@code null}.
     */
    public AnalysisClearCommand(DatabaseAdapterFactory dbFactory) {
        super(dbFactory);
    }

    @Override
    protected ExitCode execute(DatabaseAdapter db) {
        db.getAnalysisRepository().clear();
        return ExitCode.SUCCESS;
    }
}
