package de.rwth.swc.coffee4j.evaluation.command.options;

import de.rwth.swc.coffee4j.evaluation.model.JsonModelAdapter;
import de.rwth.swc.coffee4j.evaluation.model.ModelImporter;
import de.rwth.swc.coffee4j.evaluation.model.ctw.CtwModelImporter;
import picocli.CommandLine;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Command line options for the {@link de.rwth.swc.coffee4j.evaluation.command.model.ModelImportCommand}.
 * <p>
 * It provides a path to either a model or a directory containing models, and a format. Additionally, it can get all
 * files that match the given format from the specified path. This allows the command to focus on handling the actual
 * importing step.
 */
public class ModelImportOptions {

    @CommandLine.Option(
            names = {"--path"},
            description = "One or more files and directories containing test models.",
            required = true,
            arity = "1..*",
            paramLabel = "PATH")
    private List<Path> input;

    @CommandLine.Option(
            names = "--format",
            description = "The import format. Values: ${COMPLETION-CANDIDATES} Default: ${DEFAULT-VALUE}",
            defaultValue = "JSON",
            required = true,
            paramLabel = "FORMAT"
    )
    private Format format;

    /**
     * Gets all files that match the extensions of the given importer in the specified path.
     * <p>
     * The returned list may be empty if no models could be found.
     *
     * @return a list of paths that contain files matching the current format.
     * @throws IOException if access to the file system fails
     */
    public List<Path> getMatchingFiles() throws IOException {
        List<Path> result = new ArrayList<>();
        for (Path path : input) {
            for (String extension : format.extensions) {
                try (Stream<Path> files = Files.walk(path)) {
                    files.filter(p -> p.toString().endsWith(extension))
                            .filter(Files::isRegularFile)
                            .forEach(result::add);
                }
            }
        }
        return result;
    }

    /**
     * Gets a model importer of the specified format.
     *
     * @return a model importer
     */
    public ModelImporter getImporter() {
        return format.importer;
    }


    /**
     * Enum for model import formats.
     */
    public enum Format {
        /**
         * Import JSON files.
         */
        JSON(new JsonModelAdapter(), new String[]{".json"}),
        /**
         * Import CTWedge or CitLab files.
         */
        CTW(new CtwModelImporter(), new String[]{".ctw", ".citl"});

        private final ModelImporter importer;
        private final String[] extensions;

        Format(ModelImporter importer, String[] extensions) {
            this.importer = importer;
            this.extensions = extensions;
        }
    }

}
