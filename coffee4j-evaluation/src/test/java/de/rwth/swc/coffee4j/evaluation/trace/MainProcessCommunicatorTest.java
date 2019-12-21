package de.rwth.swc.coffee4j.evaluation.trace;

import de.rwth.swc.coffee4j.evaluation.TestData;
import okio.Buffer;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class MainProcessCommunicatorTest {

    @Test
    void shouldConfirmToProtocol() {

        TraceIteration.Builder builder = new Trace.Builder()
                .fromScenario(TestData.MODEL_1.getScenario("S0"))
                .fromAlgorithm(TestData.ALGORITHM_1).iteration();
        Buffer source = new Buffer();
        Buffer sink = new Buffer();
        source.writeUtf8("? 0 1 1\n");
        source.writeUtf8("? 0 0 0\n");
        source.writeUtf8("? 1 1 1\n");
        source.writeUtf8("? 1 1 2\n");
        source.writeUtf8("# A3 VIOLATED\n");
        source.writeUtf8("! 1 1 1\n");
        source.writeUtf8("# A1 SATISFIED\n");
        MainProcessCommunicator communicator = new MainProcessCommunicator(TestData.MODEL_1,
                TestData.MODEL_1.getScenario("S0"), builder, source, sink, false);
        communicator.run();
        assertEquals("2\n" +
                "2 3 4\n" +
                "FORBIDDEN 0 0 1\n" +
                "FORBIDDEN -1 2 2\n" +
                "START\n" +
                "SUCCESS\n" +
                "FAIL C0\n" +
                "SUCCESS\n" +
                "SUCCESS\n", sink.readUtf8());
        Trace trace = builder.complete().build();
        assertArrayEquals(new int[][]{
                new int[]{0, 1, 1},
                new int[]{0, 0, 0},
                new int[]{1, 1, 1},
                new int[]{1, 1, 2}}, trace.getTraceIterations().get(0).getTestCases().toArray(new int[0][0]));
        assertArrayEquals(new int[][]{new int[]{1, 1, 1}}, trace.getTraceIterations().get(0)
                .getFailureInducingCombinations().toArray(new int[0][0]));
        assertEquals(Map.of("A1", true, "A3", false), trace.getTraceIterations().get(0).getAssumptions());

    }

}