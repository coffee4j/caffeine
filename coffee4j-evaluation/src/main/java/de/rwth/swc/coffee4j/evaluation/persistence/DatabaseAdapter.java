package de.rwth.swc.coffee4j.evaluation.persistence;

import de.rwth.swc.coffee4j.evaluation.persistence.analysis.AnalysisRepository;
import de.rwth.swc.coffee4j.evaluation.persistence.model.ModelRepository;
import de.rwth.swc.coffee4j.evaluation.persistence.trace.TraceRepository;

import java.io.Closeable;


/**
 * Adapter that provides the interfaces to a data store.
 */
public interface DatabaseAdapter extends Closeable {

    /**
     * Gets a repository for {@link de.rwth.swc.coffee4j.evaluation.model.TestModel} instances.
     *
     * @return a model repository
     */
    ModelRepository getModelRepository();

    /**
     * Gets a repository for {@link de.rwth.swc.coffee4j.evaluation.trace.Trace} instances.
     *
     * @return a trace repository
     */
    TraceRepository getTraceRepository();

    /**
     * Gets a repository for analysis properties.
     *
     * @return an analysis repository
     */
    AnalysisRepository getAnalysisRepository();

}
