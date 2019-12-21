package de.rwth.swc.coffee4j.evaluation.command.options;

import de.rwth.swc.coffee4j.evaluation.model.JsonModelAdapter;
import de.rwth.swc.coffee4j.evaluation.model.ModelExporter;
import de.rwth.swc.coffee4j.evaluation.utils.IOUtils;
import picocli.CommandLine;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Command line options for use in the {@link de.rwth.swc.coffee4j.evaluation.command.model.ModelExportCommand}.
 * <p>
 * It provides the output path and an {@link ModelExporter}. The path may needs to be checked for validity before using
 * it.
 */
public class ModelExportOptions {

    @CommandLine.Option(
            names = {"--path"},
            description = "The output directory.",
            required = true,
            paramLabel = "PATH")
    private Path output;

    @CommandLine.Option(
            names = "--format",
            description = "The export format. Values: ${COMPLETION-CANDIDATES} Default: ${DEFAULT-VALUE}",
            defaultValue = "JSON",
            required = true,
            paramLabel = "FORMAT"
    )
    private Format format;

    /**
     * Gets a prepared output directory.
     * <p>
     * It checks the path for validity and creates all necessary parent folders. Only the files themselves need to be
     * created by the exporter.
     *
     * @return the prepared path
     * @throws IOException if the path could not be prepared
     */
    public Path getOutputDirectory() throws IOException {
        return IOUtils.getOutputDirectory(output);
    }

    /**
     * Gets a {@link ModelExporter} with the given settings.
     *
     * @return a model exporter
     */
    public ModelExporter getExporter() {
        return format.exporter;
    }

    /**
     * Gets the extension that files should have when written with the current exporter.
     *
     * @return the current file extension
     */
    public String getExtension() {
        return format.extension;
    }

    /**
     * Enum for model export file formats.
     */
    public enum Format {
        /**
         * Export in JSON.
         */
        JSON(new JsonModelAdapter(), ".json");

        private final ModelExporter exporter;
        private final String extension;

        Format(ModelExporter exporter, String extension) {
            this.exporter = exporter;
            this.extension = extension;
        }
    }
}
