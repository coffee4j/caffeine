package de.rwth.swc.coffee4j.evaluation.persistence.analysis;

import de.rwth.swc.coffee4j.evaluation.TestData;
import de.rwth.swc.coffee4j.evaluation.analysis.AnalysisResult;
import de.rwth.swc.coffee4j.evaluation.analysis.CsvAnalysisExporter;
import okio.Buffer;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CsvAnalysisExporterTest {

    @Test
    void shouldExportResults() throws IOException {

        AnalysisResult analysis = new AnalysisResult(TestData.PROP_MODEL_1, TestData.PROP_MODEL_1_S0, TestData.PROP_TRACE_1);
        Buffer result = new Buffer();
        CsvAnalysisExporter exporter = new CsvAnalysisExporter();

        exporter.export(result, Stream.of(analysis), TestData.KEY_INFO);

        assertEquals("Model, Scenario, Algorithm, Iteration, State, M_BoundedDouble,M_UnboundedInt,M_BoundedDouble,M_HalfOpenLong,S_Boolean,T_A,T_B,T_C\n" +
                "MODEL_1,S0,ALGORITHM_1,0,COMPLETED,33.3,2147483647,33.3,9223372036854775807,0,0.66,-2000.01,44\n" +
                "MODEL_1,S0,ALGORITHM_1,1,TIME_OUT,33.3,2147483647,33.3,9223372036854775807,0,,,\n" +
                "MODEL_1,S0,ALGORITHM_1,2,MEMORY_OUT,33.3,2147483647,33.3,9223372036854775807,0,,,\n" +
                "MODEL_1,S0,ALGORITHM_1,3,INVALID,33.3,2147483647,33.3,9223372036854775807,0,0.66,-2000.01,44", result.readUtf8());

    }

}