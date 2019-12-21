package de.rwth.swc.coffee4j.evaluation.command.trace;

import de.rwth.swc.coffee4j.evaluation.command.DatabaseAdapterFactory;
import de.rwth.swc.coffee4j.evaluation.command.DatabaseCommand;
import de.rwth.swc.coffee4j.evaluation.command.options.TraceOptions;
import de.rwth.swc.coffee4j.evaluation.command.options.VersionProvider;
import de.rwth.swc.coffee4j.evaluation.model.TestModel;
import de.rwth.swc.coffee4j.evaluation.model.TestScenario;
import de.rwth.swc.coffee4j.evaluation.persistence.DatabaseAdapter;
import de.rwth.swc.coffee4j.evaluation.persistence.model.ModelInfo;
import de.rwth.swc.coffee4j.evaluation.persistence.model.ModelRepository;
import de.rwth.swc.coffee4j.evaluation.persistence.trace.TraceRepository;
import de.rwth.swc.coffee4j.evaluation.trace.ExecutionTracer;
import de.rwth.swc.coffee4j.evaluation.trace.Trace;
import de.rwth.swc.coffee4j.evaluation.trace.TraceConfiguration;
import de.rwth.swc.coffee4j.evaluation.trace.TraceIdentifier;
import de.rwth.swc.coffee4j.evaluation.utils.ExitCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

/**
 * {@link DatabaseCommand} that executes the given fault characterization algorithms on all stored scenarios.
 * <p>
 * It tries to translate the given algorithm names to commands by using a specified configuration file. If an algorithm
 * is not present in this file, a warning is emitted and the execution is skipped. Otherwise the execution checks for
 * the existence of previous traces during execution and skips previously computed scenarios.
 * <p>
 * Models and Scenarios are streamed from the {@link DatabaseAdapter} so that memory concerns are delegated there.
 */
@CommandLine.Command(
        name = "run",
        versionProvider = VersionProvider.class,
        mixinStandardHelpOptions = true,
        description = "Run an characterization algorithm on the stored models."
)
public class TraceRunCommand extends DatabaseCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(TraceRunCommand.class);

    @CommandLine.Mixin
    private TraceOptions traceOptions;

    /**
     * Constructor with the database factory to match the {@link DatabaseCommand} signature.
     *
     * @param dbFactory the dbFactory. It must not be {@code null}.
     */
    public TraceRunCommand(DatabaseAdapterFactory dbFactory) {
        super(dbFactory);
    }

    @Override
    protected ExitCode execute(DatabaseAdapter db) {

        ModelRepository modelRepository = db.getModelRepository();
        TraceRepository traceRepository = db.getTraceRepository();

        ModelInfo info = modelRepository.info();
        try (Stream<TestModel> modelStream = modelRepository.getAll()) {
            TraceConfiguration traceConfiguration = traceOptions.getTraceConfiguration();
            List<String> activeAlgorithms = traceOptions.getActiveAlgorithms();
            if (activeAlgorithms == null) {
                activeAlgorithms = new ArrayList<>(traceConfiguration.getAllAlgorithms());
            }
            int numberOfAlgorithms = activeAlgorithms.size();
            int totalTraces = numberOfAlgorithms * info.getNumberOfScenarios();
            int current = 0;
            Iterator<TestModel> iterator = modelStream.iterator();
            ExecutionTracer tracer = new ExecutionTracer(traceConfiguration);
            while (iterator.hasNext()) {
                TestModel model = iterator.next();
                if (model.getScenarios().isEmpty()) {
                    LOGGER.warn("{} has no scenarios.", model.getIdentifier());
                }
                for (TestScenario scenario : model.getScenarios().values()) {
                    for (String algorithm : activeAlgorithms) {
                        current++;
                        TraceIdentifier identifier = new TraceIdentifier(scenario.getIdentifier(), algorithm);
                        if (!traceRepository.exists(identifier)) {
                            LOGGER.info("[{}/{}] Starting trace for {} on {} of {}.", current, totalTraces, algorithm,
                                    scenario.getIdentifier().getScenarioName(),
                                    scenario.getIdentifier().getModelIdentifier());
                            Trace trace = tracer.trace(model, scenario, algorithm);
                            traceRepository.write(trace);
                        } else {
                            LOGGER.info("Trace {} already present. Skipping...", identifier);
                        }
                    }
                }
            }
        } catch (IOException e) {
            LOGGER.error("Error during tracing: {}", e.getMessage());
            return ExitCode.ERROR;
        }

        return ExitCode.SUCCESS;
    }
}
