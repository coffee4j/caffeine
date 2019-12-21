package de.rwth.swc.coffee4j.evaluation.adapter;

import de.rwth.swc.coffee4j.engine.TestResult;

import java.util.InputMismatchException;
import java.util.List;

class InputParser {

    private static int[] decodeIntArray(String array) throws InputMismatchException {
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

    void readConstraint(String nextLine, List<int[]> forbiddenCombinations, List<int[]> errorCombinations) {
        if (nextLine.startsWith("FORBIDDEN")) {
            forbiddenCombinations.add(decodeIntArray(nextLine.substring(10)));
        } else if (nextLine.startsWith("ERROR ")) {
            errorCombinations.add(decodeIntArray(nextLine.substring(6)));
        } else {
            throw new InputMismatchException("Invalid constraint: " + nextLine);
        }
    }

    TestResult readResponse(String line) {
        if (line.startsWith("SUCCESS")) {
            return TestResult.success();
        } else if (line.startsWith("FAIL")) {
            String[] failureResponse = line.split(" ");
            if (failureResponse.length == 1) {
                return TestResult.failure(new IllegalArgumentException("DEFAULT"));
            } else {
                return TestResult.failure(new IllegalArgumentException(failureResponse[1]));
            }
        } else {
            throw new InputMismatchException("Invalid Test Result: " + line);
        }
    }

    int readStrength(String line) {
        try {
            return Integer.parseInt(line.strip());
        } catch (NumberFormatException e) {
            throw new InputMismatchException("Illegal testing strength: " + line);
        }
    }

    int[] readParameters(String line) {
        return decodeIntArray(line);
    }
}
