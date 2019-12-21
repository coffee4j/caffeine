package de.rwth.swc.coffee4j.evaluation.analysis.model;

import de.rwth.swc.coffee4j.evaluation.analysis.PropertyKey;
import de.rwth.swc.coffee4j.evaluation.model.TestModel;

import java.util.Map;
import java.util.Objects;


/**
 * {@link ModelAnalysis} that copies some basic properties from the model to the properties.
 * <p>
 * This analysis does not contain any complex logic, it just copies values from the model to the result. This allows us
 * to include these values when exporting the {@link de.rwth.swc.coffee4j.evaluation.analysis.AnalysisResult} or
 * otherwise work with the {@link ModelProperties}
 */
public class ModelPropertiesAnalysis implements ModelAnalysis {

    private final String key;

    /**
     * Constructor.
     *
     * @param key the analysis key. It must not be {@code null}.
     */
    public ModelPropertiesAnalysis(String key) {
        this.key = Objects.requireNonNull(key);
    }


    @Override
    public Map<PropertyKey, Number> analyze(TestModel model) {
        return Map.of(
                PropertyKey.positiveInteger(key), model.getParameters().length
        );
    }
}
