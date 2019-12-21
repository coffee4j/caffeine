package de.rwth.swc.coffee4j.evaluation.command.options;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import de.rwth.swc.coffee4j.evaluation.trace.TraceConfiguration;
import okio.BufferedSource;
import okio.Okio;
import picocli.CommandLine;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * {@link picocli.CommandLine.Mixin} for configuring the input of the trace executor.
 */
public class TraceOptions {

    @CommandLine.Option(
            names = {"--timeout"},
            description = "Timeout per iteration in  ISO-8601 duration format.%nDefault: No Timeout",
            paramLabel = "DURATION",
            defaultValue = "-PT1S")
    private Duration timeout;

    @CommandLine.Option(names = {"--iterations"},
            description = "Number of Iterations.%nDefault: ${DEFAULT-VALUE}",
            paramLabel = "N",
            defaultValue = "1")
    private int iterations;

    @CommandLine.Option(
            names = {"--config"},
            description = "Algorithm configuration. Use gen-coffee4j-config to generate default.",
            required = true,
            paramLabel = "PATH",
            arity = "1..*")
    private List<Path> algorithmConfigurationPaths;

    @CommandLine.Option(
            names = {"--ignore-constraints"},
            description = "If any constraints for the input model should be ignored." +
                    " Overrides any set constraints in the scenarios",
            defaultValue = "false")
    private boolean ignoreConstraints;

    @CommandLine.Parameters(arity = "0..*")
    private List<String> algorithms;

    /**
     * Gets the algorithms selected to be run.
     * <p>
     * May be {@code null}. This means that every possible algorithm should be used.
     *
     * @return the active algorithms
     */
    public List<String> getActiveAlgorithms() {
        return algorithms;
    }

    /**
     * Computes and gets the trace configuration from the command line options.
     *
     * @return the trace configuration
     * @throws IOException if reading the config file fails
     */
    public TraceConfiguration getTraceConfiguration() throws IOException {
        JsonAdapter<Map<String, String>> adapter = new Moshi.Builder().build()
                .adapter(Types.newParameterizedType(Map.class, String.class, String.class));
        Map<String, String> commands = new HashMap<>();
        for (Path path : algorithmConfigurationPaths) {
            try (BufferedSource source = Okio.buffer(Okio.source(path))) {
                commands.putAll(Objects.requireNonNull(adapter.fromJson(source)));
            }
        }
        return new TraceConfiguration(commands, iterations, timeout, ignoreConstraints);
    }

}
