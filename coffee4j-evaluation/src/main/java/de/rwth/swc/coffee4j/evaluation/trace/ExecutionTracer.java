package de.rwth.swc.coffee4j.evaluation.trace;

import de.rwth.swc.coffee4j.evaluation.model.TestModel;
import de.rwth.swc.coffee4j.evaluation.model.TestScenario;
import de.rwth.swc.coffee4j.evaluation.trace.processor.LoggingLineProcessor;
import de.rwth.swc.coffee4j.evaluation.utils.ExitCode;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Class tasked with starting the fault characterization algorithm, initializing the communication interfaces and
 * gathering the execution results.
 * <p>
 * It starts a new process for each iteration by looking up the command in the {@link TraceConfiguration}. This comes
 * with the usual constraints of the {@link ProcessBuilder}. The {@link ExecutionState} of an iteration is decided by
 * the exit code of the algorithm process. It starts two separate threads for communication with the started process for
 * keeping both the STDOUT and STDERR buffers clear.
 * <p>
 * After execution the trace tries to clean up its thread-pool and any remaining algorithm processes. If the process
 * starts any sub-processes it also tries to clean up any children. Depending on the operation system that may not be
 * always reliable.
 */
public final class ExecutionTracer {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExecutionTracer.class);

    private static final String COMMAND_NOT_FOUND = "Command for algorithm {} not found in configuration. Skipping...";
    private static final String ITERATION_COMPLETE = "Command {} terminated successfully.";
    private static final String ITERATION_TIME_OUT = "Command {} terminated with a time out.";
    private static final String ITERATION_INVALID = "Error while executing {}.";
    private static final String ITERATION_PROCESS_ERROR = "Command {} terminated with error: {}.";
    private static final String ITERATION_MEMORY_OUT = "Command {} terminated with a memory out.";

    private final TraceConfiguration configuration;

    /**
     * Constructor.
     *
     * @param configuration the configuration for tracing
     */
    public ExecutionTracer(TraceConfiguration configuration) {
        this.configuration = Objects.requireNonNull(configuration);
    }

    /**
     * Start a trace with a given scenario and algorithm.
     * <p>
     * Any algorithm that does not have a command in the config emits a warning and is skipped.
     *
     * @param model         the model to evaluate
     * @param scenario      the scenario to evaluate
     * @param algorithmName the algorithm to run
     * @return the computed trace
     */
    public Trace trace(TestModel model, TestScenario scenario, String algorithmName) {
        Trace.Builder traceBuilder = new Trace.Builder()
                .fromScenario(scenario)
                .fromAlgorithm(algorithmName);
        for (int iteration = 0; iteration < configuration.getNumberOfIterations(); iteration++) {
            try {
                String[] command = configuration.getCommand(algorithmName);
                traceIteration(traceBuilder, model, scenario, command);
            } catch (NoSuchElementException e) {
                LOGGER.warn(COMMAND_NOT_FOUND, algorithmName);
            }
        }

        return traceBuilder.build();
    }

    private void traceIteration(Trace.Builder traceBuilder, TestModel model, TestScenario scenario, String... command) {
        ExecutorService threadPool = Executors.newFixedThreadPool(2);
        TraceIteration.Builder builder = traceBuilder.iteration();
        Process process;
        try {
            process = new ProcessBuilder(command).start();
        } catch (IOException e) {
            LOGGER.error(ITERATION_INVALID, String.join(" ", command));
            shutdown(null, threadPool);
            builder.invalid();
            return;
        }
        try (BufferedSource input = Okio.buffer(Okio.source(process.getInputStream()));
             BufferedSource error = Okio.buffer(Okio.source(process.getErrorStream()));
             BufferedSink sink = Okio.buffer(Okio.sink(process.getOutputStream()))) {

            ProcessCommunicator errorProcessor = new ReadingProcessCommunicator(Collections.singletonList(
                    new LoggingLineProcessor(LoggingLineProcessor.Level.ERROR)),
                    error, sink);

            ProcessCommunicator mainProcessor = new MainProcessCommunicator(model,
                    scenario,
                    builder,
                    input,
                    sink, configuration.isIgnoreConstraints());

            long start = System.nanoTime();
            threadPool.submit(errorProcessor);
            threadPool.submit(mainProcessor);
            boolean isTimeout = runWithTimeout(process);
            long end = System.nanoTime();

            if (isTimeout) {
                LOGGER.atInfo().addArgument(() -> String.join(" ", command))
                        .log(ITERATION_TIME_OUT);
                builder.timeOut();
                shutdown(process, threadPool);
            } else {
                int exitCode = process.exitValue();
                if (exitCode == ExitCode.SUCCESS.exitCode()) {
                    LOGGER.atInfo().addArgument(() -> String.join(" ", command))
                            .log(ITERATION_COMPLETE);
                    builder.timeNano(end - start);
                    builder.complete();
                } else if (exitCode == ExitCode.MEMORY_OUT.exitCode()) {
                    LOGGER.atInfo().addArgument(() -> String.join(" ", command))
                            .log(ITERATION_MEMORY_OUT);
                    builder.memoryOut();
                } else {
                    LOGGER.atError().addArgument(() -> String.join(" ", command))
                            .addArgument(exitCode).log(ITERATION_PROCESS_ERROR);
                    builder.invalid();
                }
            }

        } catch (IOException e) {
            LOGGER.atError().addArgument(() -> String.join(" ", command))
                    .log(ITERATION_INVALID);
            shutdown(process, threadPool);
            builder.invalid();
        } catch (InterruptedException e) {
            LOGGER.atError().addArgument(() -> String.join(" ", command))
                    .log(ITERATION_INVALID);
            Thread.currentThread().interrupt();
            shutdown(process, threadPool);
            builder.invalid();
        } finally {
            shutdown(process, threadPool);
        }
    }

    private boolean runWithTimeout(Process process) throws InterruptedException {
        if (configuration.getTimeout().isNegative() || configuration.getTimeout().isZero()) {
            process.waitFor();
            return false;
        } else {
            return !process.waitFor(configuration.getTimeout().toMillis(), TimeUnit.MILLISECONDS);
        }
    }

    private void shutdown(Process process, ExecutorService threadPool) {
        if (process != null) {
            process.descendants().forEach(ProcessHandle::destroy);
            process.destroy();
        }
        threadPool.shutdownNow();
    }

}
