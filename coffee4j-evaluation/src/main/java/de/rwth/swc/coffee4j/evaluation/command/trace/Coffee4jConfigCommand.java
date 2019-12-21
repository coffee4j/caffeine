package de.rwth.swc.coffee4j.evaluation.command.trace;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import de.rwth.swc.coffee4j.evaluation.command.BaseCommand;
import de.rwth.swc.coffee4j.evaluation.command.options.VersionProvider;
import de.rwth.swc.coffee4j.evaluation.utils.ExitCode;
import de.rwth.swc.coffee4j.evaluation.utils.IOUtils;
import okio.BufferedSink;
import okio.Okio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * Command that creates the trace configuration for coffee4j.
 * <p>
 * Preferably this command should be moved to the coffee4j adapter, so that this component can be kept free of any
 * dependencies on framework specific logic.
 * <p>
 * To be able to create the configuration this command requires the path to the JAR for the adapter, an output path, and
 * a filename for the written config file.
 */
@CommandLine.Command(
        name = "coffee4j-config",
        versionProvider = VersionProvider.class,
        mixinStandardHelpOptions = true,
        description = "Generate the algorithm configuration for coffee4j."
)
public class Coffee4jConfigCommand extends BaseCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(Coffee4jConfigCommand.class);

    @CommandLine.Option(
            names = {"--jar"},
            description = "The path to the coffee4j-adapter jar.",
            required = true,
            paramLabel = "PATH")
    private Path coffee4jAdapterJar;

    @CommandLine.Option(
            names = {"--output"},
            description = "The output directory for this config file.",
            defaultValue = "",
            paramLabel = "PATH")
    private Path output;

    @CommandLine.Option(
            names = {"--name"},
            description = "The name for this config file.",
            defaultValue = "coffee4j-config.json",
            paramLabel = "NAME")
    private String fileName;

    @Override
    protected ExitCode execute() {
        try {
            Path outputDirectory = IOUtils.getOutputDirectory(output);
            Path outputPath = outputDirectory.resolve(fileName);

            Path jarPath = coffee4jAdapterJar.toRealPath();

            Map<String, String> config = new HashMap<>();
            config.put("Aifl", toJavaCommand(jarPath, "-a Aifl -g Ipog"));
            config.put("IterAifl", toJavaCommand(jarPath, "-a IterAifl -g Ipog"));
            config.put("IDD", toJavaCommand(jarPath, "-a IDD -g Ipog"));
            config.put("Ipog", toJavaCommand(jarPath, "-a NoOp -g Ipog"));
            config.put("Aetg", toJavaCommand(jarPath, "-a NoOp -g Aetg"));
            config.put("Ict", toJavaCommand(jarPath, "-a Ict"));
            config.put("FIC", toJavaCommand(jarPath, "-a FIC -g Ipog"));
            config.put("FIC_BS", toJavaCommand(jarPath, "-a FIC_BS -g Ipog"));
            config.put("Csp", toJavaCommand(jarPath, "-a Csp -g Ipog"));
            config.put("Ala", toJavaCommand(jarPath, "-a Ala -g Ipog"));
            config.put("Ben", toJavaCommand(jarPath, "-a Ben -g Ipog"));

            JsonAdapter<Map<String, String>> adapter = new Moshi.Builder().build()
                    .adapter(Types.newParameterizedType(Map.class, String.class, String.class));
            adapter = adapter.indent("  ");

            try (BufferedSink sink = Okio.buffer(Okio.sink(outputPath))) {
                adapter.toJson(sink, config);
            }
        } catch (IOException e) {
            LOGGER.error("Error creating config: {}", e.getMessage());
        }
        return ExitCode.SUCCESS;
    }

    private String toJavaCommand(Path jar, String... arguments) {
        return "java -Xmx1G -jar " + jar.toAbsolutePath().toString() + " " + String.join(" ", arguments);
    }

}
