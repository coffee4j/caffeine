package de.rwth.swc.coffee4j.evaluation.command.model;

import de.rwth.swc.coffee4j.evaluation.command.DatabaseAdapterFactory;
import de.rwth.swc.coffee4j.evaluation.command.DatabaseCommand;
import de.rwth.swc.coffee4j.evaluation.command.options.VersionProvider;
import de.rwth.swc.coffee4j.evaluation.model.ModelIdentifier;
import de.rwth.swc.coffee4j.evaluation.model.TestModel;
import de.rwth.swc.coffee4j.evaluation.persistence.DatabaseAdapter;
import de.rwth.swc.coffee4j.evaluation.persistence.model.ModelInfo;
import de.rwth.swc.coffee4j.evaluation.persistence.model.ModelRepository;
import de.rwth.swc.coffee4j.evaluation.utils.ExitCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

import java.util.NoSuchElementException;

/**
 * Command that retrieves information about stored models.
 * <p>
 * Given a model name it prints some information specific to this model. Calling it without a name will print some
 * general information.
 */
@CommandLine.Command(
        name = "info",
        versionProvider = VersionProvider.class,
        mixinStandardHelpOptions = true,
        description = "Retrieve information about the stored models."
)
public class ModelInfoCommand extends DatabaseCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(ModelInfoCommand.class);

    @CommandLine.Parameters(description = "Name of the model", arity = "0..1")
    private String name;

    /**
     * Constructor with the database factory to match the {@link DatabaseCommand} signature.
     *
     * @param dbFactory the dbFactory. It must not be {@code null}.
     */
    public ModelInfoCommand(DatabaseAdapterFactory dbFactory) {
        super(dbFactory);
    }


    @Override
    protected ExitCode execute(DatabaseAdapter db) {
        ModelRepository modelRepository = db.getModelRepository();
        if (name == null || name.isBlank()) {
            ModelInfo info = modelRepository.info();
            LOGGER.info("Currently storing {} models with {} scenarios.",
                    info.getNumberOfModels(),
                    info.getNumberOfScenarios());
        } else {
            try {
                TestModel testModel = modelRepository.get(new ModelIdentifier(name));
                LOGGER.info("\n{}", testModel);
            } catch (NoSuchElementException e) {
                LOGGER.error("No such model stored in database.");
                return ExitCode.ERROR;
            }
        }
        return ExitCode.SUCCESS;
    }
}
