package de.rwth.swc.coffee4j.evaluation.command.analysis;

import de.rwth.swc.coffee4j.evaluation.analysis.AnalysisResult;
import de.rwth.swc.coffee4j.evaluation.command.DatabaseAdapterFactory;
import de.rwth.swc.coffee4j.evaluation.command.DatabaseCommand;
import de.rwth.swc.coffee4j.evaluation.command.options.AnalysisExportOptions;
import de.rwth.swc.coffee4j.evaluation.command.options.VersionProvider;
import de.rwth.swc.coffee4j.evaluation.persistence.DatabaseAdapter;
import de.rwth.swc.coffee4j.evaluation.persistence.analysis.AnalysisRepository;
import de.rwth.swc.coffee4j.evaluation.utils.ExitCode;
import okio.BufferedSink;
import okio.Okio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

import java.io.IOException;
import java.util.stream.Stream;

/**
 * {@link DatabaseCommand} that exports all stored {@link AnalysisResult} instances with a given {@link
 * de.rwth.swc.coffee4j.evaluation.analysis.AnalysisExporter}.
 * <p>
 * Which exporter to choose is specified by the user in the {@link AnalysisExportOptions}.
 */
@CommandLine.Command(
        name = "export",
        versionProvider = VersionProvider.class,
        mixinStandardHelpOptions = true,
        description = "Export stored analyses."
)
public class AnalysisExportCommand extends DatabaseCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(AnalysisExportCommand.class);

    @CommandLine.Mixin
    private AnalysisExportOptions analysisExportOptions;

    /**
     * Constructor with the database factory to match the {@link DatabaseCommand} signature.
     *
     * @param dbFactory the dbFactory. It must not be {@code null}.
     */
    public AnalysisExportCommand(DatabaseAdapterFactory dbFactory) {
        super(dbFactory);
    }

    @Override
    protected ExitCode execute(DatabaseAdapter db) {

        AnalysisRepository analysisRepository = db.getAnalysisRepository();
        try (Stream<AnalysisResult> analysisStream = analysisRepository.getAll();
             BufferedSink sink = Okio.buffer(Okio.sink(analysisExportOptions.getOutputPath()))) {
            analysisExportOptions.getExporter().export(sink, analysisStream, analysisRepository.getKeyInfo());
        } catch (IOException e) {
            LOGGER.error("Error during analysis export : {}", e.getMessage());
        }

        return ExitCode.SUCCESS;
    }
}
