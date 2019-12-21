package de.rwth.swc.coffee4j.evaluation.trace.processor;

import de.rwth.swc.coffee4j.evaluation.trace.TestResult;
import okio.BufferedSink;

import java.io.IOException;
import java.util.InputMismatchException;

/**
 * Interface for classes which can read and respond to lines sent by the fault characterization process.
 * <p>
 * Care has to be taken if multiple processors respond to some of the same lines. This may lead to them being sent in an
 * arbitrary way and confusing the algorithm implementation.
 */
public interface LineProcessor {

    /**
     * Decode a string array of the form "a b c d e" to an integer array.
     * <p>
     * It splits the array at spaces and converts every element to an integer.
     *
     * @param array the string representing the array
     * @return an int array
     * @throws InputMismatchException if the array is invalid
     */
    static int[] decodeIntArray(String array) {
        String[] values = array.split(" ");
        int[] result = new int[values.length];
        for (int i = 0; i < values.length; i++) {
            try {
                result[i] = Integer.parseInt(values[i]);
            } catch (NumberFormatException e) {
                throw new InputMismatchException("Illegal value in array: " + values[i]);
            }
        }
        return result;
    }

    /**
     * Encode a test result for communication.
     *
     * @param testResult the test result to encode
     * @return a string representing the test result
     */
    static String encodeTestResult(TestResult testResult) {
        if (testResult.isSuccessful()) {
            return "SUCCESS";
        } else {
            return "FAIL " + testResult.getCauseForFailure().map(Throwable::getMessage).orElse("");
        }
    }

    /**
     * Process a single line.
     *
     * @param line   the line to process
     * @param output an output sink to the algorithm process
     * @throws IOException if writing fails for io reasons
     */
    void processLine(String line, BufferedSink output) throws IOException;

}
