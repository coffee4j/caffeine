package de.rwth.swc.coffee4j.evaluation.command.model;

import de.rwth.swc.coffee4j.evaluation.command.DatabaseAdapterFactory;
import de.rwth.swc.coffee4j.evaluation.command.DatabaseCommand;
import de.rwth.swc.coffee4j.evaluation.command.options.RandomSystemModelOptions;
import de.rwth.swc.coffee4j.evaluation.command.options.VersionProvider;
import de.rwth.swc.coffee4j.evaluation.model.RandomModelGenerator;
import de.rwth.swc.coffee4j.evaluation.model.TestModel;
import de.rwth.swc.coffee4j.evaluation.persistence.DatabaseAdapter;
import de.rwth.swc.coffee4j.evaluation.utils.ExitCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

import java.util.Collection;

/**
 * {@link DatabaseCommand} that generates random models from a given configuration.
 */
@CommandLine.Command(
        name = "generate",
        versionProvider = VersionProvider.class,
        mixinStandardHelpOptions = true,
        description = "Generate random test models."
)
public class ModelGenerateCommand extends DatabaseCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(ModelGenerateCommand.class);

    @CommandLine.Mixin
    private RandomSystemModelOptions generationOptions;


    /**
     * Constructor with the database factory to match the {@link DatabaseCommand} signature.
     *
     * @param dbFactory the dbFactory. It must not be {@code null}.
     */
    public ModelGenerateCommand(DatabaseAdapterFactory dbFactory) {
        super(dbFactory);
    }


    @Override
    protected ExitCode execute(DatabaseAdapter db) {

        RandomModelGenerator generator = new RandomModelGenerator(generationOptions.getConfiguration());
        Collection<TestModel> testModels = generator.generateSystemModels(generationOptions.getNumberOfSystemModels());
        for (TestModel testModel : testModels) {
            db.getModelRepository().write(testModel);
            LOGGER.info("Generated model {}.", testModel.getIdentifier());
        }
        return ExitCode.SUCCESS;
    }
}
