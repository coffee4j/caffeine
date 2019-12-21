package de.rwth.swc.coffee4j.evaluation.model;

import okio.BufferedSink;

import java.io.IOException;

/**
 * Interface for classes that transform a {@link TestModel} from the internal representation to some external one.
 */
public interface ModelExporter {

    /**
     * Export a model to a data sink.
     * <p>
     * The sink should not be closed by the exporter, this is handled externally.
     *
     * @param sink  the sink
     * @param model the model to export
     * @throws IOException if writing to the sink fails
     */
    void export(BufferedSink sink, TestModel model) throws IOException;

}
