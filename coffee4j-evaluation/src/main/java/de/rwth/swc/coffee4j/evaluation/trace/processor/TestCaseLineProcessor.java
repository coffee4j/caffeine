package de.rwth.swc.coffee4j.evaluation.trace.processor;

import de.rwth.swc.coffee4j.evaluation.trace.TestOracle;
import de.rwth.swc.coffee4j.evaluation.trace.TraceIteration;
import okio.BufferedSink;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * {@link LineProcessor} handling test cases.
 * <p>
 * It delegates the challenge to an {@link TestOracle} and then reports the result.
 * <p>
 * An test case line is of the form "! [combination]". Example: "! -1 2 1 -1 " will return either "SUCCESS" or "FAILURE
 * [key]" where [key] is the constraint that caused the failure.
 */
public class TestCaseLineProcessor implements LineProcessor {

    private static final Pattern PATTERN = Pattern.compile("^\\? (.*)", Pattern.DOTALL);

    private final TestOracle oracle;
    private final TraceIteration.Builder builder;

    /**
     * Constructor.
     *
     * @param oracle  the test oracle
     * @param builder the iteration builder to store the results in
     */
    public TestCaseLineProcessor(TestOracle oracle, TraceIteration.Builder builder) {
        this.oracle = oracle;
        this.builder = builder;
    }

    @Override
    public void processLine(String line, BufferedSink output) throws IOException {
        Matcher matcher = PATTERN.matcher(line);
        if (matcher.matches()) {
            int[] challenge = LineProcessor.decodeIntArray(matcher.group(1));
            builder.testCase(challenge);
            output.writeUtf8(LineProcessor.encodeTestResult(oracle.getTestResult(challenge)) + "\n");
            output.flush();
        }

    }

}
