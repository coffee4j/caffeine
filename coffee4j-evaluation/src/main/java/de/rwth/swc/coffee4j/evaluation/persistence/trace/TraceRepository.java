package de.rwth.swc.coffee4j.evaluation.persistence.trace;

import de.rwth.swc.coffee4j.evaluation.trace.Trace;
import de.rwth.swc.coffee4j.evaluation.trace.TraceIdentifier;

import java.util.stream.Stream;

/**
 * Repository for persisting {@link Trace} instances.
 */
public interface TraceRepository {

    /**
     * Gets a trace by its identifier.
     *
     * @param identifier the identifier
     * @return the trace
     * @throws java.util.NoSuchElementException if no trace with this identifier is stored
     */
    Trace get(TraceIdentifier identifier);

    /**
     * Checks whether a trace with a given identifier exists.
     *
     * @param identifier the identifier to check
     * @return whether it exists
     */
    boolean exists(TraceIdentifier identifier);

    /**
     * Gets a stream containing a stored traces.
     * <p>
     * This stream may keep an open connection to the database and has to be treated as a closable resource. It allows
     * for lazily loading traces from the database as they are needed, without running out of memory in th process.
     *
     * @return a stream of all stored traces.
     */
    Stream<Trace> getAll();

    /**
     * Persists a trace to the storage.
     * <p>
     * This may throw an exception if no related {@link de.rwth.swc.coffee4j.evaluation.model.TestModel} can be found.
     * This prevents having traces without the necessary model information for analysis.
     *
     * @param trace the trace to persist
     */
    void write(Trace trace);

    /**
     * Clear all traces.
     * <p>
     * This may also clear all analysis results of these traces.
     */
    void clear();

    /**
     * Gets information about the stored traces.
     *
     * @return the trace information
     */
    TraceInfo info();

}
