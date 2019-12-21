package de.rwth.swc.coffee4j.evaluation.analysis.model;

import de.rwth.swc.coffee4j.evaluation.analysis.PropertyKey;
import de.rwth.swc.coffee4j.evaluation.model.TestModel;

import java.util.Map;

/**
 * Analysis that computes a number of metrics for a {@link TestModel}.
 */
public interface ModelAnalysis {

    /**
     * Compute the metrics belonging to this analysis.
     *
     * @param model the model to be analyzed. It must not be {@code null}.
     * @return the computed metrics. It must not be {@code null}.
     */
    Map<PropertyKey, Number> analyze(TestModel model);

}
