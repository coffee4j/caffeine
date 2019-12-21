package de.rwth.swc.coffee4j.evaluation.command.model;

import de.rwth.swc.coffee4j.evaluation.command.DatabaseAdapterFactory;
import de.rwth.swc.coffee4j.evaluation.command.DatabaseCommand;
import de.rwth.swc.coffee4j.evaluation.command.options.ModelImportOptions;
import de.rwth.swc.coffee4j.evaluation.command.options.VersionProvider;
import de.rwth.swc.coffee4j.evaluation.model.InvalidModelException;
import de.rwth.swc.coffee4j.evaluation.model.ModelImporter;
import de.rwth.swc.coffee4j.evaluation.model.TestModel;
import de.rwth.swc.coffee4j.evaluation.persistence.DatabaseAdapter;
import de.rwth.swc.coffee4j.evaluation.persistence.model.ModelRepository;
import de.rwth.swc.coffee4j.evaluation.utils.ExitCode;
import okio.BufferedSource;
import okio.Okio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * Command for importing models from an external representations.
 * <p>
 * Converters have to be registered in {@link ModelImportOptions} to be able to use them in this command.
 */
@CommandLine.Command(
        name = "import",
        versionProvider = VersionProvider.class,
        mixinStandardHelpOptions = true,
        description = "Import test models."
)
public class ModelImportCommand extends DatabaseCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(ModelImportCommand.class);

    @CommandLine.Mixin
    private ModelImportOptions modelImportOptions;

    /**
     * Constructor with the database factory to match the {@link DatabaseCommand} signature.
     *
     * @param dbFactory the dbFactory. It must not be {@code null}.
     */
    public ModelImportCommand(DatabaseAdapterFactory dbFactory) {
        super(dbFactory);
    }


    private void importFiles(Path path, ModelImporter importer, ModelRepository modelRepository) {
        try (BufferedSource source = Okio.buffer(Okio.source(path))) {
            LOGGER.info("Importing {}.", path);
            TestModel model = importer.importModel(source);
            modelRepository.write(model);
        } catch (InvalidModelException e) {
            LOGGER.error("Error while parsing {}: {}", path, e.getMessage());
        } catch (IOException e) {
            LOGGER.error("Error while importing {}: {}", path, e.getMessage());
        }
    }

    @Override
    protected ExitCode execute(DatabaseAdapter db) {
        ModelImporter importer = modelImportOptions.getImporter();
        try {
            List<Path> matchingFiles = modelImportOptions.getMatchingFiles();
            LOGGER.debug("Found matching files: {}", matchingFiles);
            for (Path path : matchingFiles) {
                importFiles(path, importer, db.getModelRepository());
            }
        } catch (IOException e) {
            LOGGER.error("Error reading files: {}", e.getMessage());
            return ExitCode.ERROR;
        }
        return ExitCode.SUCCESS;
    }
}
