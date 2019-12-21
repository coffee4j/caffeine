package de.rwth.swc.coffee4j.evaluation.analysis;

import de.rwth.swc.coffee4j.evaluation.analysis.trace.TraceProperties;
import de.rwth.swc.coffee4j.evaluation.trace.TraceIdentifier;
import okio.BufferedSink;

import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * {@link AnalysisExporter} that exports the results to a comma-separated file.
 * <p>
 * It contains a header with all used keys and a row for each iteration.
 */
public class CsvAnalysisExporter implements AnalysisExporter {

    @Override
    public void export(BufferedSink sink, Stream<AnalysisResult> results, KeyInfo keyInfo) throws IOException {
        writeHeader(sink, keyInfo);
        try {
            results.forEach(result -> writeLine(sink, keyInfo, result));
        } catch (CsvWriteException e) {
            throw new IOException(e);
        }
    }

    private void writeHeader(BufferedSink sink, KeyInfo keyInfo) throws IOException {
        sink.writeUtf8("Model, Scenario, Algorithm, Iteration, State, ");
        sink.writeUtf8(keyInfo.getModelKeys().stream().map(PropertyKey::getKey).collect(Collectors.joining(",")));
        sink.writeUtf8(",");
        sink.writeUtf8(keyInfo.getScenarioKeys().stream().map(PropertyKey::getKey).collect(Collectors.joining(",")));
        sink.writeUtf8(",");
        sink.writeUtf8(keyInfo.getTraceKeys().stream().map(PropertyKey::getKey).collect(Collectors.joining(",")));
    }

    private void writeLine(BufferedSink sink, KeyInfo keyInfo, AnalysisResult analysisResult) {

        TraceIdentifier identifier = analysisResult.getTraceProperties().getIdentifier();
        try {
            for (int i = 0; i < analysisResult.getTraceProperties().getPropertiesForIteration().size(); i++) {
                sink.writeUtf8("\n");
                TraceProperties.IterationProperties iterationProperties = analysisResult
                        .getTraceProperties()
                        .getPropertiesForIteration().get(i);

                sink.writeUtf8(String.join(",",
                        identifier.getModelIdentifier().getModelName(),
                        identifier.getScenarioIdentifier().getScenarioName(),
                        identifier.getAlgorithmName(),
                        Integer.toString(i)));
                sink.writeUtf8(",");
                sink.writeUtf8(iterationProperties.getState().toString());
                sink.writeUtf8(",");
                sink.writeUtf8(keyInfo.getModelKeys().stream().map(key -> getSafeProperty(key, analysisResult
                        .getModelProperties()
                        .getProperties())).collect(Collectors.joining(",")));
                sink.writeUtf8(",");
                sink.writeUtf8(keyInfo.getScenarioKeys().stream().map(key -> getSafeProperty(key, analysisResult
                        .getScenarioProperties()
                        .getProperties())).collect(Collectors.joining(",")));
                sink.writeUtf8(",");
                sink.writeUtf8(keyInfo.getTraceKeys().stream().map(key -> getSafeProperty(key, iterationProperties.getProperties()))
                        .collect(Collectors.joining(",")));
            }
        } catch (IOException e) {
            throw new CsvWriteException(e);
        }
    }

    private String getSafeProperty(PropertyKey key, Map<PropertyKey, Number> properties) {
        Number value = properties.get(key);
        return value != null ? value.toString() : "";
    }

    /**
     * Exception that wraps the checked IO exceptions during exporting.
     * <p>
     * Should be unwrapped at the call site again. This allows for using some methods in Java8 streams.
     */
    public static class CsvWriteException extends RuntimeException {
        CsvWriteException(Throwable e) {
            super(e);
        }
    }

}
