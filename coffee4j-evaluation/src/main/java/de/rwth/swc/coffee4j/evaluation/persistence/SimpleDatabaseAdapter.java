package de.rwth.swc.coffee4j.evaluation.persistence;

import de.rwth.swc.coffee4j.evaluation.analysis.AnalysisResult;
import de.rwth.swc.coffee4j.evaluation.analysis.KeyInfo;
import de.rwth.swc.coffee4j.evaluation.analysis.model.ModelProperties;
import de.rwth.swc.coffee4j.evaluation.analysis.scenario.ScenarioProperties;
import de.rwth.swc.coffee4j.evaluation.analysis.trace.TraceProperties;
import de.rwth.swc.coffee4j.evaluation.model.ModelIdentifier;
import de.rwth.swc.coffee4j.evaluation.model.ScenarioIdentifier;
import de.rwth.swc.coffee4j.evaluation.model.TestModel;
import de.rwth.swc.coffee4j.evaluation.persistence.analysis.AnalysisRepository;
import de.rwth.swc.coffee4j.evaluation.persistence.model.ModelInfo;
import de.rwth.swc.coffee4j.evaluation.persistence.model.ModelRepository;
import de.rwth.swc.coffee4j.evaluation.persistence.trace.TraceInfo;
import de.rwth.swc.coffee4j.evaluation.persistence.trace.TraceRepository;
import de.rwth.swc.coffee4j.evaluation.trace.Trace;
import de.rwth.swc.coffee4j.evaluation.trace.TraceIdentifier;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * {@link DatabaseAdapter} that uses Java hash maps to store the data internally.
 * <p>
 * It enforces the same referential constraints as other adapters, i. e. writing a trace fo a non-exiting model will
 * throw an exception. This class is mainly used for testing to forgo the need for a real database connection. Any use
 * in production is only viable for small benchmarking runs.
 */
public class SimpleDatabaseAdapter implements DatabaseAdapter {

    private final Map<ModelIdentifier, TestModel> models;
    private final Map<TraceIdentifier, Trace> traces;
    private final Map<ModelIdentifier, ModelProperties> modelProperties;
    private final Map<ScenarioIdentifier, ScenarioProperties> scenarioProperties;
    private final Map<TraceIdentifier, TraceProperties> traceProperties;

    /**
     * Constructor.
     */
    public SimpleDatabaseAdapter() {
        this.models = new HashMap<>();
        this.traces = new HashMap<>();
        this.modelProperties = new HashMap<>();
        this.scenarioProperties = new HashMap<>();
        this.traceProperties = new HashMap<>();
    }

    /**
     * Copy constructor.
     *
     * @param db the adapter to copy
     */
    public SimpleDatabaseAdapter(SimpleDatabaseAdapter db) {
        this.models = new HashMap<>(db.models);
        this.traces = new HashMap<>(db.traces);
        this.modelProperties = new HashMap<>(db.modelProperties);
        this.scenarioProperties = new HashMap<>(db.scenarioProperties);
        this.traceProperties = new HashMap<>(db.traceProperties);
    }


    @Override
    public ModelRepository getModelRepository() {
        return new ModelRepository() {
            @Override
            public TestModel get(ModelIdentifier identifier) {
                return Optional.ofNullable(models.get(identifier)).orElseThrow(NoSuchElementException::new);
            }

            @Override
            public boolean exists(ModelIdentifier identifier) {
                return models.containsKey(identifier);
            }

            @Override
            public Stream<TestModel> getAll() {
                return models.values().stream();
            }

            @Override
            public void write(TestModel model) {
                models.put(model.getIdentifier(), model);
            }

            @Override
            public void clear() {
                models.clear();
                traces.clear();
                modelProperties.clear();
                scenarioProperties.clear();
                traceProperties.clear();
            }

            @Override
            public ModelInfo info() {
                return new ModelInfo(models.size(), (int) models.values().stream()
                        .mapToLong(m -> m.getScenarios().values().size()).sum());
            }
        };
    }

    @Override
    public TraceRepository getTraceRepository() {
        return new TraceRepository() {
            @Override
            public Trace get(TraceIdentifier identifier) {
                return Optional.ofNullable(traces.get(identifier)).orElseThrow(NoSuchElementException::new);
            }

            @Override
            public boolean exists(TraceIdentifier identifier) {
                return traces.containsKey(identifier);
            }

            @Override
            public Stream<Trace> getAll() {
                return traces.values().stream();
            }

            @Override
            public void write(Trace trace) {
                if (!models.containsKey(trace.getIdentifier().getModelIdentifier())) {
                    throw new NoSuchElementException();
                }
                traces.put(trace.getIdentifier(), trace);
            }

            @Override
            public void clear() {
                traces.clear();
                traceProperties.clear();
            }

            @Override
            public TraceInfo info() {
                return new TraceInfo(traces.size());
            }
        };
    }

    @Override
    public AnalysisRepository getAnalysisRepository() {
        return new AnalysisRepository() {
            @Override
            public AnalysisResult get(TraceIdentifier identifier) {
                return new AnalysisResult(
                        Optional.ofNullable(modelProperties.get(identifier.getModelIdentifier()))
                                .orElseThrow(NoSuchElementException::new),
                        Optional.ofNullable(scenarioProperties.get(identifier.getScenarioIdentifier()))
                                .orElseThrow(NoSuchElementException::new),
                        Optional.ofNullable(traceProperties.get(identifier))
                                .orElseThrow(NoSuchElementException::new));
            }

            @Override
            public Stream<AnalysisResult> getAll() {
                return traceProperties.keySet().stream().map(this::get);
            }

            @Override
            public boolean exists(ModelIdentifier identifier) {
                return modelProperties.containsKey(identifier);
            }

            @Override
            public boolean exists(ScenarioIdentifier identifier) {
                return scenarioProperties.containsKey(identifier);
            }

            @Override
            public boolean exists(TraceIdentifier identifier) {
                return traceProperties.containsKey(identifier);
            }

            @Override
            public void write(ModelProperties properties) {
                if (!models.containsKey(properties.getIdentifier())) {
                    throw new NoSuchElementException();
                }
                modelProperties.put(properties.getIdentifier(), properties);
            }

            @Override
            public void write(ScenarioProperties properties) {
                if (!models.containsKey(properties.getIdentifier().getModelIdentifier())) {
                    throw new NoSuchElementException();
                }
                scenarioProperties.put(properties.getIdentifier(), properties);
            }

            @Override
            public void write(TraceProperties properties) {
                if (!traces.containsKey(properties.getIdentifier())) {
                    throw new NoSuchElementException();
                }
                traceProperties.put(properties.getIdentifier(), properties);
            }

            @Override
            public void clear() {
                modelProperties.clear();
                scenarioProperties.clear();
                traceProperties.clear();
            }

            @Override
            public KeyInfo getKeyInfo() {
                return new KeyInfo(
                        modelProperties.values().stream()
                                .flatMap(s -> s.getProperties().keySet().stream())
                                .distinct().collect(Collectors.toList()),
                        scenarioProperties.values().stream()
                                .flatMap(s -> s.getProperties().keySet().stream())
                                .distinct().collect(Collectors.toList()),
                        traceProperties.values().stream()
                                .flatMap(s -> s.getPropertiesForIteration().stream())
                                .flatMap(s -> s.getProperties().keySet().stream())
                                .distinct().collect(Collectors.toList())
                );
            }
        };
    }

    @Override
    public void close() {
        // Do nothing
    }
}
