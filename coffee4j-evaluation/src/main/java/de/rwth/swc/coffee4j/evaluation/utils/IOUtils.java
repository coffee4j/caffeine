package de.rwth.swc.coffee4j.evaluation.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * An interfaces containing some static utility methods mostly concerned with conversion between internal and external
 * representations.
 */
public interface IOUtils {

    /**
     * Casts a primitive integer array to an object array.
     *
     * @param value the array to convert
     * @return an object array
     */
    static Object[] castArrayToObj(int[] value) {
        return IntStream.of(value).boxed().toArray();
    }

    /**
     * Casts an object array to an primitive integer array.
     * <p>
     * Will throw the normal exceptions if the objects cannot be cast to integer.
     *
     * @param array the object array
     * @return the integer array
     */
    static int[] castArrayToInt(Object[] array) {
        return Stream.of(array).mapToInt(i -> (int) i).toArray();
    }

    /**
     * Cast an object array to a string array.
     * <p>
     * This will call the toString() method for each element.
     *
     * @param array the object array
     * @return a string array
     */
    static String[] castArrayToString(Object[] array) {
        return Stream.of(array).map(Object::toString).toArray(String[]::new);
    }

    /**
     * Prepares an output directory for writing.
     * <p>
     * If the given array is null, then it will initialize from the current working directory. It creates any missing
     * directories in the given path, so that later writes do not throw exceptions.
     *
     * @param output the path to initialize
     * @return the converted path
     * @throws IOException if any directories could not be created or the path does not represent a valid output
     *                     directory
     */
    static Path getOutputDirectory(Path output) throws IOException {
        Path result = output;
        if (Objects.isNull(result)) {
            result = Paths.get("");
        }
        if (!Files.exists(result)) {
            result = Files.createDirectories(result);
        }
        if (!Files.isDirectory(result)) {
            throw new IOException(
                    result.toAbsolutePath().toString() + " is not a valid output directory.");
        }
        return result;
    }

}
