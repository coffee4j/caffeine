package de.rwth.swc.coffee4j.evaluation.trace;

import de.rwth.swc.coffee4j.evaluation.trace.processor.LineProcessor;
import okio.BufferedSink;
import okio.BufferedSource;

import java.util.List;

/**
 * {@link ProcessCommunicator} that does not include any initial information to send to the algorithm.
 */
public class ReadingProcessCommunicator extends ProcessCommunicator {

    /**
     * Constructor.
     *
     * @param processors the line processors
     * @param source     the process output
     * @param sink       the process input
     */
    public ReadingProcessCommunicator(List<LineProcessor> processors, BufferedSource source, BufferedSink sink) {
        super(processors, source, sink);

    }

    @Override
    protected String getInitialOutput() {
        return "";
    }
}
