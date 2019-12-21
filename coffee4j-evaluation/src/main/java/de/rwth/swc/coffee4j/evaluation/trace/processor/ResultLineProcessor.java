package de.rwth.swc.coffee4j.evaluation.trace.processor;

import de.rwth.swc.coffee4j.evaluation.trace.TraceIteration;
import okio.BufferedSink;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * {@link LineProcessor} handling failure inducing combinations.
 * <p>
 * A failure inducing combination line is of the form "! [combination]". Example: "! -1 2 1 -1 " reports that the
 * combination [-, 2, 1, -] is failure inducing.
 */
public class ResultLineProcessor implements LineProcessor {

    private static final Pattern PATTERN = Pattern.compile("^! (.*)", Pattern.DOTALL);

    private final TraceIteration.Builder builder;

    /**
     * Constructor.
     *
     * @param builder the iteration builder to store the results in
     */
    public ResultLineProcessor(TraceIteration.Builder builder) {
        this.builder = builder;
    }

    @Override
    public void processLine(String line, BufferedSink output) {
        Matcher matcher = PATTERN.matcher(line);
        if (matcher.matches()) {
            int[] challenge = LineProcessor.decodeIntArray(matcher.group(1));
            builder.failureInducingCombination(challenge);
        }
    }

}
