package de.rwth.swc.coffee4j.evaluation.command.analysis;


import de.rwth.swc.coffee4j.evaluation.analysis.Analyzer;
import de.rwth.swc.coffee4j.evaluation.analysis.DefaultAnalyzer;
import de.rwth.swc.coffee4j.evaluation.analysis.model.ModelProperties;
import de.rwth.swc.coffee4j.evaluation.analysis.scenario.ScenarioProperties;
import de.rwth.swc.coffee4j.evaluation.analysis.trace.TraceProperties;
import de.rwth.swc.coffee4j.evaluation.command.DatabaseAdapterFactory;
import de.rwth.swc.coffee4j.evaluation.command.DatabaseCommand;
import de.rwth.swc.coffee4j.evaluation.command.options.VersionProvider;
import de.rwth.swc.coffee4j.evaluation.model.TestModel;
import de.rwth.swc.coffee4j.evaluation.model.TestScenario;
import de.rwth.swc.coffee4j.evaluation.persistence.DatabaseAdapter;
import de.rwth.swc.coffee4j.evaluation.persistence.analysis.AnalysisRepository;
import de.rwth.swc.coffee4j.evaluation.persistence.model.ModelRepository;
import de.rwth.swc.coffee4j.evaluation.persistence.trace.TraceRepository;
import de.rwth.swc.coffee4j.evaluation.trace.Trace;
import de.rwth.swc.coffee4j.evaluation.utils.ExitCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

import java.util.Iterator;
import java.util.stream.Stream;

/**
 * {@link DatabaseCommand} that runs the default analyses on all stored models, scenarios and traces.
 * <p>
 * Each analysis checks for the existence of an associated result first. If one is found this particular step is
 * skipped. The analyzed data is streamed from the {@link DatabaseAdapter} so that memory constraints are dealt with by
 * the implementing component.
 */
@CommandLine.Command(
        name = "run",
        versionProvider = VersionProvider.class,
        mixinStandardHelpOptions = true,
        description = "Run an analysis on the saved models."
)
public class AnalysisRunCommand extends DatabaseCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(AnalysisRunCommand.class);

    private final Analyzer analyzer = new DefaultAnalyzer();

    /**
     * Constructor with the database factory to match the {@link DatabaseCommand} signature.
     *
     * @param dbFactory the dbFactory. It must not be {@code null}.
     */
    public AnalysisRunCommand(DatabaseAdapterFactory dbFactory) {
        super(dbFactory);
    }

    @Override
    protected ExitCode execute(DatabaseAdapter db) {

        ModelRepository modelRepository = db.getModelRepository();
        TraceRepository traceRepository = db.getTraceRepository();
        AnalysisRepository analysisRepository = db.getAnalysisRepository();

        try (Stream<Trace> modelStream = traceRepository.getAll()) {
            Iterator<Trace> iterator = modelStream.iterator();
            while (iterator.hasNext()) {
                Trace trace = iterator.next();
                TestModel model = modelRepository.get(trace.getIdentifier().getModelIdentifier());
                TestScenario scenario = model.getScenario(trace.getIdentifier().getScenarioIdentifier().getScenarioName());

                LOGGER.info("Analyzing {}.", trace.getIdentifier());

                if (!analysisRepository.exists(model.getIdentifier())) {
                    ModelProperties modelProperties = analyzer.analyzeModel(model);
                    analysisRepository.write(modelProperties);
                } else {
                    LOGGER.info("Analysis for model {} already present. Skipping...", model.getIdentifier());
                }

                if (!analysisRepository.exists(scenario.getIdentifier())) {
                    ScenarioProperties scenarioProperties = analyzer.analyzeScenario(model, scenario);
                    analysisRepository.write(scenarioProperties);
                } else {
                    LOGGER.info("Analysis for scenario {} already present. Skipping...", scenario.getIdentifier());
                }

                if (!analysisRepository.exists(trace.getIdentifier())) {
                    TraceProperties modelProperties = analyzer.analyzeTrace(model, scenario, trace);
                    analysisRepository.write(modelProperties);
                } else {
                    LOGGER.info("Analysis for trace {} already present. Skipping...", trace.getIdentifier());
                }

            }
        }
        return ExitCode.SUCCESS;
    }
}
