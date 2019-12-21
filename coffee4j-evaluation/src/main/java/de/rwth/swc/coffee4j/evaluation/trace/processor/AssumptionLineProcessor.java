package de.rwth.swc.coffee4j.evaluation.trace.processor;

import de.rwth.swc.coffee4j.evaluation.trace.TraceIteration;
import okio.BufferedSink;

import java.util.InputMismatchException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * {@link LineProcessor} handling assumptions.
 * <p>
 * An assumption is of the form "# [key] (SATISFIED | VIOLATED)" where [key] is an unique identifier for the assumption.
 * Example: "# assumption1 SATISFIED" reports that the assumption "assumption1" was satisfied.
 */
public class AssumptionLineProcessor implements LineProcessor {


    private static final String SATISFIED_TOKEN = "SATISFIED";
    private static final String FAILED_TOKEN = "VIOLATED";
    private static final Pattern PATTERN = Pattern.compile("^# (.*)", Pattern.DOTALL);

    private final TraceIteration.Builder builder;

    /**
     * Constructor.
     *
     * @param builder the iteration builder to store any assumptions in
     */
    public AssumptionLineProcessor(TraceIteration.Builder builder) {
        this.builder = builder;
    }

    @Override
    public void processLine(String line, BufferedSink output) {
        Matcher matcher = PATTERN.matcher(line);
        if (matcher.matches()) {
            String[] split = matcher.group(1).split(" ");
            if (split.length != 2 || !(split[1].equals(SATISFIED_TOKEN) || split[1].equals(FAILED_TOKEN))) {
                throw new InputMismatchException("Assumption invalid: " + line);
            }
            String key = split[0];
            boolean satisfied = split[1].equals(SATISFIED_TOKEN);
            if (satisfied) {
                builder.satisfiedAssumption(key);
            } else {
                builder.violatedAssumption(key);
            }
        }
    }

}
