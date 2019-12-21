package de.rwth.swc.coffee4j.evaluation.command.model;

import de.rwth.swc.coffee4j.evaluation.command.DatabaseAdapterFactory;
import de.rwth.swc.coffee4j.evaluation.command.DatabaseCommand;
import de.rwth.swc.coffee4j.evaluation.command.options.ModelExportOptions;
import de.rwth.swc.coffee4j.evaluation.command.options.VersionProvider;
import de.rwth.swc.coffee4j.evaluation.model.ModelExporter;
import de.rwth.swc.coffee4j.evaluation.model.TestModel;
import de.rwth.swc.coffee4j.evaluation.persistence.DatabaseAdapter;
import de.rwth.swc.coffee4j.evaluation.utils.ExitCode;
import okio.BufferedSink;
import okio.Okio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.stream.Stream;

/**
 * Command that exports a stored model.
 */
@CommandLine.Command(
        name = "export",
        versionProvider = VersionProvider.class,
        mixinStandardHelpOptions = true,
        description = "Export the stored models."
)
public class ModelExportCommand extends DatabaseCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(ModelExportCommand.class);

    @CommandLine.Mixin
    private ModelExportOptions modelExportOptions;

    /**
     * Constructor with the database factory to match the {@link DatabaseCommand} signature.
     *
     * @param dbFactory the dbFactory. It must not be {@code null}.
     */
    public ModelExportCommand(DatabaseAdapterFactory dbFactory) {
        super(dbFactory);
    }


    @Override
    protected ExitCode execute(DatabaseAdapter db) {
        ModelExporter exporter = modelExportOptions.getExporter();
        try (Stream<TestModel> modelStream = db.getModelRepository().getAll()) {
            Iterator<TestModel> iterator = modelStream.iterator();
            while (iterator.hasNext()) {
                TestModel model = iterator.next();
                Path outputPath = modelExportOptions.getOutputDirectory().resolve(model.getIdentifier().getModelName()
                        + modelExportOptions.getExtension());
                try (BufferedSink sink = Okio.buffer(Okio.sink(outputPath))) {
                    exporter.export(sink, model);
                }
                LOGGER.info("Written model {} to {}", model.getIdentifier(), outputPath);
            }
        } catch (IOException e) {
            LOGGER.error("Error while exporting: {}", e.getMessage());
            return ExitCode.ERROR;
        }
        return ExitCode.SUCCESS;
    }
}
