package de.rwth.swc.coffee4j.evaluation.persistence.analysis;

import de.rwth.swc.coffee4j.evaluation.analysis.AnalysisResult;
import de.rwth.swc.coffee4j.evaluation.analysis.KeyInfo;
import de.rwth.swc.coffee4j.evaluation.analysis.model.ModelProperties;
import de.rwth.swc.coffee4j.evaluation.analysis.scenario.ScenarioProperties;
import de.rwth.swc.coffee4j.evaluation.analysis.trace.TraceProperties;
import de.rwth.swc.coffee4j.evaluation.model.ModelIdentifier;
import de.rwth.swc.coffee4j.evaluation.model.ScenarioIdentifier;
import de.rwth.swc.coffee4j.evaluation.trace.TraceIdentifier;

import java.util.stream.Stream;

/**
 * Repository for all analysis results.
 * <p>
 * Like the analyses itself it allows for storing model, scenario and trace information separately.
 */
public interface AnalysisRepository {

    /**
     * Gets the model, scenario and trace information for a given identifier.
     *
     * @param identifier the identifier
     * @return an analysis result
     */
    AnalysisResult get(TraceIdentifier identifier);

    /**
     * Gets a stream containing a stored analysis results.
     * <p>
     * This stream may keep an open connection to the database and has to be treated as a closable resource. It allows
     * for lazily loading traces from the database as they are needed, without running out of memory in th process.
     *
     * @return a stream of all stored analysis results.
     */
    Stream<AnalysisResult> getAll();

    /**
     * Checks whether analysis information for a given model are already stored.
     *
     * @param identifier the model identifier
     * @return whether analysis results exist
     */
    boolean exists(ModelIdentifier identifier);

    /**
     * Checks whether analysis information for a given scenario are already stored.
     *
     * @param identifier the scenario identifier
     * @return whether analysis results exist
     */
    boolean exists(ScenarioIdentifier identifier);

    /**
     * Checks whether analysis information for a given trace are already stored.
     *
     * @param identifier the trace identifier
     * @return whether analysis results exist
     */
    boolean exists(TraceIdentifier identifier);

    /**
     * Writes the analysis results for a model.
     *
     * @param properties the analysis results
     */
    void write(ModelProperties properties);

    /**
     * Writes the analysis results for a scenario.
     *
     * @param properties the analysis results
     */
    void write(ScenarioProperties properties);

    /**
     * Writes the analysis results for a trace.
     * <p>
     * This may fail when the related model has no information stored.
     *
     * @param properties the analysis results
     */
    void write(TraceProperties properties);

    /**
     * Clears all analysis results
     */
    void clear();

    /**
     * Gets information about the stored property keys.
     *
     * @return the key information
     */
    KeyInfo getKeyInfo();

}
