package de.rwth.swc.coffee4j.evaluation.adapter;

import de.rwth.swc.coffee4j.engine.TestResult;
import de.rwth.swc.coffee4j.engine.characterization.FaultCharacterizationAlgorithm;
import de.rwth.swc.coffee4j.engine.characterization.FaultCharacterizationConfiguration;
import de.rwth.swc.coffee4j.engine.generator.TestInputGroup;
import de.rwth.swc.coffee4j.engine.generator.TestInputGroupGenerator;
import de.rwth.swc.coffee4j.engine.report.Reporter;
import org.junit.jupiter.api.Test;
import org.mockito.AdditionalAnswers;
import org.mockito.InOrder;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.Collections;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class Coffee4JAdapterTest {

    private static final String GENERATOR_ID = "GEN_ID";

    @Test
    void shouldInitCorrectlyWithoutConstraints() throws IOException {

        TestInputGroupGenerator generator = getMockedGenerator(List.of(new int[]{0, 0, 0}));
        FaultCharacterizationAlgorithmProvider algo = getMockedCharacterization(FaultCharacterizationAlgorithmProvider.Type.ADAPTIVE,
                List.of(List.of(new int[]{1, 1, 1})), List.of(new int[]{0, 1, 1}));
        IOAdapter io = getMockedTraceIO(List.of("2", "2 2 2", "START", "SUCCESS", "SUCCESS"));

        int returnCode = new Coffee4JAdapter(generator, algo, io).call();
        assertEquals(0, returnCode);
    }

    @Test
    void shouldThrowForInvalidStrength() {

        TestInputGroupGenerator gen = getMockedGenerator(List.of(new int[]{0, 0, 0}));
        FaultCharacterizationAlgorithmProvider algo = getMockedCharacterization(FaultCharacterizationAlgorithmProvider.Type.ADAPTIVE,
                List.of(List.of(new int[]{1, 1, 1})), List.of(new int[]{0, 1, 1}));
        IOAdapter io = getMockedTraceIO(List.of("abc", "2 2 2", "START", "SUCCESS", "SUCCESS"));
        assertThrows(InputMismatchException.class, (() -> new Coffee4JAdapter(gen, algo, io).call()));
    }

    @Test
    void shouldThrowForInvalidParameters() {

        TestInputGroupGenerator gen = getMockedGenerator(List.of(new int[]{0, 0, 0}));
        FaultCharacterizationAlgorithmProvider algo = getMockedCharacterization(FaultCharacterizationAlgorithmProvider.Type.ADAPTIVE,
                List.of(List.of(new int[]{1, 1, 1})), List.of(new int[]{0, 1, 1}));
        IOAdapter io = getMockedTraceIO(List.of("2", "a b v", "START", "SUCCESS", "SUCCESS"));
        assertThrows(InputMismatchException.class, (() -> new Coffee4JAdapter(gen, algo, io).call()));
    }

    @Test
    void shouldThrowForInvalidStartToken() {

        TestInputGroupGenerator gen = getMockedGenerator(List.of(new int[]{0, 0, 0}));
        FaultCharacterizationAlgorithmProvider algo = getMockedCharacterization(FaultCharacterizationAlgorithmProvider.Type.ADAPTIVE,
                List.of(List.of(new int[]{1, 1, 1})), List.of(new int[]{0, 1, 1}));
        IOAdapter io = getMockedTraceIO(List.of("2", "2 2 2", "Srtra", "SUCCESS", "SUCCESS"));
        assertThrows(InputMismatchException.class, (() -> new Coffee4JAdapter(gen, algo, io).call()));
    }

    @Test
    void shouldCommunicateAlgorithmExecution() throws IOException {

        TestInputGroupGenerator gen = getMockedGenerator(List.of(new int[]{0, 0, 0}));
        FaultCharacterizationAlgorithmProvider algo = getMockedCharacterization(FaultCharacterizationAlgorithmProvider.Type.ADAPTIVE,
                List.of(List.of(new int[]{1, 1, 1})), List.of(new int[]{0, 1, 1}));
        IOAdapter io = getMockedTraceIO(List.of("2", "2 2 2", "START", "SUCCESS", "SUCCESS"));

        InOrder inOrder = Mockito.inOrder(io);
        new Coffee4JAdapter(gen, algo, io).call();
        inOrder.verify(io).write("? 0 0 0");
        inOrder.verify(io).write("? 1 1 1");
        inOrder.verify(io).write("! 0 1 1");
    }

    @Test
    void shouldStartInterleavedAlgorithmExecutionEmpty() throws IOException {

        TestInputGroupGenerator gen = getMockedGenerator(List.of(new int[]{0, 0, 0}));
        FaultCharacterizationAlgorithmProvider algo = getMockedCharacterization(FaultCharacterizationAlgorithmProvider.Type.INTERLEAVED,
                List.of(List.of(new int[]{1, 1, 1})), List.of(new int[]{0, 1, 1}));
        IOAdapter io = getMockedTraceIO(List.of("2", "2 2 2", "START", "SUCCESS", "SUCCESS"));

        InOrder inOrder = Mockito.inOrder(io);
        new Coffee4JAdapter(gen, algo, io).call();
        inOrder.verify(io).write("? 1 1 1");
        inOrder.verify(io).write("! 0 1 1");
    }

    @Test
    void shouldReceiveConstraints() throws IOException {

        TestInputGroupGenerator gen = getMockedGenerator(List.of(new int[]{0, 0, 0}));
        FaultCharacterizationAlgorithmProvider algo = getMockedCharacterization(FaultCharacterizationAlgorithmProvider.Type.ADAPTIVE,
                List.of(List.of(new int[]{1, 1, 1}, new int[]{2, 1, 1}, new int[]{1, 2, 1}, new int[]{1, 1, 2})), List.of(new int[]{0, 1, 1}));
        IOAdapter io = getMockedTraceIO(List.of("1",
                "4 4 2 2",
                "FORBIDDEN 2 2 -1 -1",
                "FORBIDDEN 3 3 -1 -1",
                "FORBIDDEN 1 1 -1 -1",
                "FORBIDDEN 0 0 -1 -1",
                "START",
                "SUCCESS",
                "SUCCESS",
                "SUCCESS",
                "FAIL C1"));

        int returnCode = new Coffee4JAdapter(gen, algo, io).call();
        assertEquals(0, returnCode);

    }


    @Test
    void shouldThrowForInvalidConstraints() {

        TestInputGroupGenerator gen = getMockedGenerator(List.of(new int[]{0, 0, 0}));
        FaultCharacterizationAlgorithmProvider algo = getMockedCharacterization(FaultCharacterizationAlgorithmProvider.Type.ADAPTIVE,
                List.of(List.of(new int[]{1, 1, 1})), List.of(new int[]{0, 1, 1}));
        IOAdapter io = getMockedTraceIO(List.of("2", "2 2 2", "ERRR 1 1 1", "FORBIDDEN 0 0 0", "START", "SUCCESS", "SUCCESS"));

        assertThrows(InputMismatchException.class, (() -> new Coffee4JAdapter(gen, algo, io).call()));

    }

    @Test
    void shouldSendAssumptions() throws IOException {
        TestInputGroupGenerator gen = getMockedGenerator(Collections.emptyList());
        FaultCharacterizationAlgorithmProvider algo = mock(FaultCharacterizationAlgorithmProvider.class);
        when(algo.create(any(FaultCharacterizationConfiguration.class))).thenAnswer(invocationOnMock -> new AssumptionTestAlgorithm(invocationOnMock.getArgument(0)));
        when(algo.getType()).thenReturn(FaultCharacterizationAlgorithmProvider.Type.ADAPTIVE);
        IOAdapter io = getMockedTraceIO(List.of("2", "2 2 2", "START"));

        InOrder inOrder = Mockito.inOrder(io);
        new Coffee4JAdapter(gen, algo, io).call();
        inOrder.verify(io).write("# A1 SATISFIED");
        inOrder.verify(io).write("# A2 VIOLATED");
        inOrder.verify(io).write("# A3 VIOLATED");
    }

    private IOAdapter getMockedTraceIO(List<String> outputs) {
        IOAdapter io = mock(IOAdapter.class);
        when(io.hasNextLine()).thenAnswer(AdditionalAnswers.returnsElementsOf(outputs.stream().map(s -> true).collect(Collectors.toList()))).thenReturn(false);
        when(io.nextLine()).thenAnswer(AdditionalAnswers.returnsElementsOf(outputs));

        return io;
    }

    private TestInputGroupGenerator getMockedGenerator(List<int[]> testCases) {
        TestInputGroupGenerator generator = mock(TestInputGroupGenerator.class);
        when(generator.generate(any(), any())).thenReturn(Collections.singletonList(() -> new TestInputGroup(GENERATOR_ID, testCases)));
        return generator;

    }

    private FaultCharacterizationAlgorithmProvider getMockedCharacterization(FaultCharacterizationAlgorithmProvider.Type type,
                                                                             List<List<int[]>> testCases,
                                                                             List<int[]> failureInducingCombinations) {
        FaultCharacterizationAlgorithm algorithm = mock(FaultCharacterizationAlgorithm.class);
        when(algorithm.computeNextTestInputs(any()))
                .thenAnswer(AdditionalAnswers.returnsElementsOf(testCases))
                .thenReturn(Collections.emptyList());
        when(algorithm.computeFailureInducingCombinations()).thenReturn(failureInducingCombinations);

        FaultCharacterizationAlgorithmProvider input = mock(FaultCharacterizationAlgorithmProvider.class);
        when(input.getType()).thenReturn(type);
        when(input.create(any())).thenReturn(algorithm);
        return input;
    }

    private static class AssumptionTestAlgorithm implements de.rwth.swc.coffee4j.engine.characterization.FaultCharacterizationAlgorithm {

        private final Reporter reporter;

        private AssumptionTestAlgorithm(FaultCharacterizationConfiguration configuration) {
            this.reporter = configuration.getReporter();
        }

        @Override
        public List<int[]> computeNextTestInputs(Map<int[], TestResult> testResults) {
            reporter.reportAssumptionSatisfaction("A1");
            reporter.reportAssumptionViolation("A2");
            return Collections.emptyList();
        }

        @Override
        public List<int[]> computeFailureInducingCombinations() {
            reporter.reportAssumptionViolation("A3");
            return Collections.emptyList();
        }
    }

}