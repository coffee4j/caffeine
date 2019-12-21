package de.rwth.swc.coffee4j.evaluation.command.trace;

import de.rwth.swc.coffee4j.evaluation.command.DatabaseAdapterFactory;
import de.rwth.swc.coffee4j.evaluation.command.DatabaseCommand;
import de.rwth.swc.coffee4j.evaluation.command.options.VersionProvider;
import de.rwth.swc.coffee4j.evaluation.persistence.DatabaseAdapter;
import de.rwth.swc.coffee4j.evaluation.utils.ExitCode;
import picocli.CommandLine;

/**
 * {@link DatabaseCommand} that clears all stored traces from the {@link de.rwth.swc.coffee4j.evaluation.persistence.trace.TraceRepository}.
 * <p>
 * This may also delete all associated analyses from the {@link de.rwth.swc.coffee4j.evaluation.persistence.analysis.AnalysisRepository},
 * but this is not guaranteed.
 */
@CommandLine.Command(
        name = "clear",
        versionProvider = VersionProvider.class,
        mixinStandardHelpOptions = true,
        description = "Clear all stored traces and analyses."
)
public class TraceClearCommand extends DatabaseCommand {

    /**
     * Constructor with the database factory to match the {@link DatabaseCommand} signature.
     *
     * @param dbFactory the dbFactory. It must not be {@code null}.
     */
    public TraceClearCommand(DatabaseAdapterFactory dbFactory) {
        super(dbFactory);
    }

    @Override
    protected ExitCode execute(DatabaseAdapter db) {
        db.getTraceRepository().clear();
        return ExitCode.SUCCESS;
    }
}
