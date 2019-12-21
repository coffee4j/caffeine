package de.rwth.swc.coffee4j.evaluation.analysis;

import okio.BufferedSink;

import java.io.IOException;
import java.util.stream.Stream;

/**
 * Interface for classes that transform the internal representation of analysis results to some external one.
 * <p>
 * This is meant for aggregating all analysis results and storing them in a single file. The exporting process is
 * handled lazily to not need to load all results into memory at once.
 */
public interface AnalysisExporter {

    /**
     * Exports the analysis results.
     * <p>
     * The given sink should not be closed by the exporter, this is handled externally.
     *
     * @param sink    the sink to write to
     * @param results a stream of results to write
     * @param keyInfo information about the used keys
     * @throws IOException if writing fails
     */
    void export(BufferedSink sink, Stream<AnalysisResult> results, KeyInfo keyInfo) throws IOException;

}
