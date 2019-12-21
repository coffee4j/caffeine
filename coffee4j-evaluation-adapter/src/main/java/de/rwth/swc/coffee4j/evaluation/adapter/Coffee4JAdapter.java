package de.rwth.swc.coffee4j.evaluation.adapter;

import de.rwth.swc.coffee4j.engine.TestModel;
import de.rwth.swc.coffee4j.engine.TestResult;
import de.rwth.swc.coffee4j.engine.TupleList;
import de.rwth.swc.coffee4j.engine.characterization.FaultCharacterizationAlgorithm;
import de.rwth.swc.coffee4j.engine.characterization.FaultCharacterizationConfiguration;
import de.rwth.swc.coffee4j.engine.constraint.ConstraintCheckerFactory;
import de.rwth.swc.coffee4j.engine.generator.TestInputGroupGenerator;
import de.rwth.swc.coffee4j.engine.util.CombinationUtil;
import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.ints.IntSet;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Adapter that implements the communication protocol as defined by the benchmark infrastructure.
 * <p>
 * It reads from and writes to an {@link IOAdapter}. Moreover {@link #call()} terminates with the exit code 0 when the
 * execution was successful, and 1 if an error occurred.
 * The adapter does not provide graceful handling of invalid messages from the infrastructure. Should a message be malformed,
 * the adapter stops the execution and terminates with an exception.
 */
public class Coffee4JAdapter implements Callable<Integer> {

    private final static int ERROR_RESPONSE_CODE = 1;
    private final static int SUCCESS_RESPONSE_CODE = 0;

    private final TestInputGroupGenerator generator;
    private final FaultCharacterizationAlgorithmProvider faultCharacterizationAlgorithmProvider;
    private final IOAdapter io;

    private int tupleListId = 1;

    /**
     * Constructs a new adapter.
     *
     * @param generator                              the covering array generator. This may be {@code null} if the given fault characterization
     *                                               algorithm is interleaved.
     * @param faultCharacterizationAlgorithmProvider A provider for the {@link de.rwth.swc.coffee4j.engine.characterization.FaultCharacterizationAlgorithm} that should be used.
     *                                               This may not be {@code null}.
     * @param io                                     the communication adapter. This must not be {@code null}.
     */
    public Coffee4JAdapter(TestInputGroupGenerator generator,
                           FaultCharacterizationAlgorithmProvider faultCharacterizationAlgorithmProvider,
                           IOAdapter io) {
        this.faultCharacterizationAlgorithmProvider = Objects.requireNonNull(faultCharacterizationAlgorithmProvider);
        if (faultCharacterizationAlgorithmProvider.getType() != FaultCharacterizationAlgorithmProvider.Type.INTERLEAVED) {
            Objects.requireNonNull(generator);
        }
        this.generator = generator;
        this.io = Objects.requireNonNull(io);
    }

    private static String encodeIntArray(String prefix, int[] array) {
        return prefix + encodeIntArray(array);
    }

    private static String encodeIntArray(int[] array) {
        return IntStream.of(array).mapToObj(Integer::toString).collect(Collectors.joining(" "));
    }

    private void writeTestCaseChallenge(int[] testCase) throws IOException {
        io.write(encodeIntArray("? ", testCase));
    }

    private void writeResult(List<int[]> failureInducingCombinations) throws IOException {
        for (int[] failureInducingCombination : failureInducingCombinations) {
            io.write(encodeIntArray("! ", failureInducingCombination));
        }
    }

    @Override
    public Integer call() throws IOException {

        InputParser parser = new InputParser();

        int strength = parser.readStrength(io.nextLine());
        int[] parameters = parser.readParameters(io.nextLine());

        List<int[]> errorCombinations = new ArrayList<>();
        List<int[]> forbiddenCombinations = new ArrayList<>();
        String nextLine = io.nextLine();
        while (!nextLine.startsWith("START")) {
            parser.readConstraint(nextLine, forbiddenCombinations, errorCombinations);
            nextLine = io.nextLine();
        }

        List<TupleList> errorTupleLists = createTupleList(errorCombinations);
        List<TupleList> forbiddenTupleLists = createTupleList(forbiddenCombinations);

        TestModel inputParameterModel = new TestModel(strength, parameters, forbiddenTupleLists, errorTupleLists);

        FaultCharacterizationAlgorithm algorithm = faultCharacterizationAlgorithmProvider.
                create(new FaultCharacterizationConfiguration(inputParameterModel,
                        new ConstraintCheckerFactory(inputParameterModel).createHardConstraintsChecker(),
                        new AssumptionReporter(io)));

        List<int[]> failureInducingCombinations;
        switch (faultCharacterizationAlgorithmProvider.getType()) {
            case INTERLEAVED:
                failureInducingCombinations = runAlgorithm(io, Collections.emptyList(), algorithm, parser);
                break;
            case ADAPTIVE:
            case STATIC:
                List<int[]> initialTestCases = generator.generate(inputParameterModel, new AssumptionReporter(io))
                        .stream()
                        .flatMap(s -> s.get().getTestInputs().stream())
                        .collect(Collectors.toList());
                failureInducingCombinations = runAlgorithm(io, initialTestCases, algorithm, parser);
                break;
            default:
                return ERROR_RESPONSE_CODE;
        }
        writeResult(failureInducingCombinations);
        return SUCCESS_RESPONSE_CODE;
    }

    private List<int[]> runAlgorithm(IOAdapter io, List<int[]> initialTestCases,
                                     FaultCharacterizationAlgorithm algorithm, InputParser parser) throws IOException {
        Map<int[], TestResult> testResults = new HashMap<>();

        for (int[] initialTestCase : initialTestCases) {
            writeTestCaseChallenge(initialTestCase);
            TestResult response = parser.readResponse(io.nextLine());
            testResults.put(initialTestCase, response);
        }

        List<int[]> testCases = algorithm.computeNextTestInputs(testResults);
        while (!testCases.isEmpty()) {
            testResults.clear();
            for (int[] testCase : testCases) {
                writeTestCaseChallenge(testCase);
                TestResult response = parser.readResponse(io.nextLine());
                testResults.put(testCase, response);
            }
            testCases = algorithm.computeNextTestInputs(testResults);
        }

        return algorithm.computeFailureInducingCombinations();
    }

    private List<TupleList> createTupleList(List<int[]> combinations) {

        List<TupleList> result = new ArrayList<>();
        for (int[] combination : combinations) {
            IntSet involvedParameters = new IntArraySet();
            for (int i = 0; i < combination.length; i++) {
                int parameter = combination[i];
                if (parameter != CombinationUtil.NO_VALUE) {
                    involvedParameters.add(i);
                }
            }
            int[] involved = involvedParameters.toIntArray();
            int[] compact = new int[involved.length];
            for (int parameter = 0; parameter < involved.length; parameter++) {
                compact[parameter] = combination[involved[parameter]];
            }
            result.add(new TupleList(tupleListId, involved, Collections.singletonList(compact)));
            tupleListId++;
        }

        return result;
    }

}
