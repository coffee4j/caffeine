package de.rwth.swc.coffee4j.evaluation.trace;

import de.rwth.swc.coffee4j.evaluation.trace.processor.LineProcessor;
import okio.BufferedSink;
import okio.BufferedSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.InputMismatchException;
import java.util.List;

/**
 * A class tasked with implementing the communication protocol between infrastructure and fault characterization
 * algorithm.
 * <p>
 * It is intended to be subclassed with classes that have different information to send at initialization. The actual
 * communication is done by the {@link LineProcessor} instances.
 */
public abstract class ProcessCommunicator implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessCommunicator.class);

    private static final String COMMUNICATION_ERROR = "Error communicating with process.";
    private static final String INVALID_INPUT = "Ignoring invalid input: {}";

    private final List<LineProcessor> processors;
    private final BufferedSource source;
    private final BufferedSink sink;

    /**
     * Constructor.
     *
     * @param processors the line processors implementing the communication
     * @param source     the process output
     * @param sink       the process input
     */
    protected ProcessCommunicator(List<LineProcessor> processors,
                                  BufferedSource source,
                                  BufferedSink sink) {
        this.processors = processors;
        this.source = source;
        this.sink = sink;
    }

    @Override
    public void run() {
        try {
            String initialOutput = getInitialOutput();
            if (!initialOutput.isBlank()) {
                LOGGER.trace(initialOutput);
                sink.writeUtf8(initialOutput);
                sink.flush();
            }
            for (String line; (line = source.readUtf8Line()) != null; ) {
                for (LineProcessor processor : processors) {
                    processLine(line, processor);
                }
            }
        } catch (IOException e) {
            LOGGER.error(COMMUNICATION_ERROR);
        }

    }

    private void processLine(String line, LineProcessor processor) throws IOException {
        try {
            processor.processLine(line, sink);
        } catch (InputMismatchException e) {
            LOGGER.warn(INVALID_INPUT, e.getMessage());
        }
    }

    /**
     * Gets the output to send to the algorithm at initialization.
     *
     * @return the initial output
     */
    protected abstract String getInitialOutput();
}
