package de.rwth.swc.coffee4j.evaluation.command.options;

import de.rwth.swc.coffee4j.evaluation.analysis.AnalysisExporter;
import de.rwth.swc.coffee4j.evaluation.analysis.CsvAnalysisExporter;
import picocli.CommandLine;

import java.nio.file.Path;

/**
 * Command line options for the {@link de.rwth.swc.coffee4j.evaluation.command.analysis.AnalysisExportCommand}.
 * <p>
 * It provides the output path and an {@link AnalysisExporter}. The path may needs to be checked for validity before
 * using it.
 */
public class AnalysisExportOptions {

    @CommandLine.Option(
            names = {"--path"},
            description = "The output directory.",
            required = true,
            paramLabel = "PATH")
    private Path output;

    @CommandLine.Option(
            names = "--format",
            description = "The export format. Values: ${COMPLETION-CANDIDATES} Default: ${DEFAULT-VALUE}",
            defaultValue = "CSV",
            required = true,
            paramLabel = "FORMAT"
    )
    private Format format;

    /**
     * The specified exporter.
     * <p>
     * It is guaranteed to be valid, otherwise an exception would have been thrown beforehand.
     *
     * @return the exporter.
     */
    public AnalysisExporter getExporter() {
        return format.exporter;
    }

    /**
     * Gets the specified output path.
     * <p>
     * This may not be null, but could point to not yet existing directories or files.
     *
     * @return the output path
     */
    public Path getOutputPath() {
        return output;
    }


    /**
     * Enum for the analysis export format.
     */
    public enum Format {
        /**
         * Export in CSV.
         */
        CSV(new CsvAnalysisExporter());

        private final AnalysisExporter exporter;

        Format(AnalysisExporter exporter) {
            this.exporter = exporter;
        }
    }
}
