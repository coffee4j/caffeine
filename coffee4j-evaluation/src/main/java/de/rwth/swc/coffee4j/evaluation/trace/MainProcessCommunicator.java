package de.rwth.swc.coffee4j.evaluation.trace;

import de.rwth.swc.coffee4j.evaluation.model.TestModel;
import de.rwth.swc.coffee4j.evaluation.model.TestScenario;
import de.rwth.swc.coffee4j.evaluation.trace.processor.AssumptionLineProcessor;
import de.rwth.swc.coffee4j.evaluation.trace.processor.LoggingLineProcessor;
import de.rwth.swc.coffee4j.evaluation.trace.processor.ResultLineProcessor;
import de.rwth.swc.coffee4j.evaluation.trace.processor.TestCaseLineProcessor;
import okio.BufferedSink;
import okio.BufferedSource;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * {@link ProcessCommunicator} which handles the implementation of the main communication protocol over STDOUT.
 * <p>
 * It initializes the necessary {@link de.rwth.swc.coffee4j.evaluation.trace.processor.LineProcessor} for handling test
 * cases, assumptions, and failure inducing combinations. It also includes the option to log all communication.
 */
final class MainProcessCommunicator extends ProcessCommunicator {

    private static final String FORBIDDEN_PREFIX = "FORBIDDEN ";
    private static final String START_TOKEN = "START";

    private final boolean ignoreConstraints;
    private final TestScenario scenario;
    private final TestModel model;

    MainProcessCommunicator(TestModel model, TestScenario testScenario, TraceIteration.Builder builder,
                            BufferedSource source, BufferedSink sink, boolean ignoreConstraints) {
        super(List.of(new LoggingLineProcessor(LoggingLineProcessor.Level.TRACE),
                new TestCaseLineProcessor(new TestOracle(model, testScenario, ignoreConstraints), builder),
                new ResultLineProcessor(builder),
                new AssumptionLineProcessor(builder)),
                source, sink);
        this.model = model;
        this.scenario = testScenario;
        this.ignoreConstraints = ignoreConstraints;
    }

    @Override
    protected String getInitialOutput() {
        StringBuilder sb = new StringBuilder()
                .append(scenario.getStrength())
                .append("\n")
                .append(IntStream.of(model.getParameters()).
                        mapToObj(Integer::toString)
                        .collect(Collectors.joining(" ")))
                .append("\n");

        if (!ignoreConstraints) {
            for (int[] constraint : model.getConstraintsForScenario(scenario).values()) {
                sb.append(FORBIDDEN_PREFIX)
                        .append(IntStream.of(constraint)
                                .mapToObj(Integer::toString)
                                .collect(Collectors.joining(" ")))
                        .append("\n");
            }
        }
        sb.append(START_TOKEN)
                .append("\n");
        return sb.toString();
    }

}
