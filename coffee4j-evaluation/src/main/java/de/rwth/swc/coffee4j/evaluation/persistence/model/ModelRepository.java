package de.rwth.swc.coffee4j.evaluation.persistence.model;

import de.rwth.swc.coffee4j.evaluation.model.ModelIdentifier;
import de.rwth.swc.coffee4j.evaluation.model.TestModel;

import java.util.stream.Stream;

/**
 * Repository for persisting {@link TestModel} instances.
 */
public interface ModelRepository {

    /**
     * Gets a model by its identifier.
     *
     * @param identifier the identifier
     * @return the model
     * @throws java.util.NoSuchElementException if no model with this identifier is found
     */
    TestModel get(ModelIdentifier identifier);

    /**
     * Checks whether a model exists with a given identifier.
     *
     * @param identifier the identifier
     * @return whether it exists
     */
    boolean exists(ModelIdentifier identifier);

    /**
     * Gets a stream containing a stored models.
     * <p>
     * This stream may keep an open connection to the database and has to be treated as a closable resource. It allows
     * for lazily loading traces from the database as they are needed, without running out of memory in th process.
     *
     * @return a stream of all stored models.
     */
    Stream<TestModel> getAll();

    /**
     * Writes a model to storage.
     *
     * @param model the model to write
     */
    void write(TestModel model);

    /**
     * Clear all models.
     * <p>
     * This may also clear all traces and analysis results.
     */
    void clear();

    /**
     * Gets some information about the stored models.
     *
     * @return the model information
     */
    ModelInfo info();

}
