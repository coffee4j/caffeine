package de.rwth.swc.coffee4j.evaluation.analysis.model;

import de.rwth.swc.coffee4j.evaluation.analysis.PropertyKey;
import de.rwth.swc.coffee4j.evaluation.model.TestModel;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Analyzer that uses given {@link ModelAnalysis} to compute the {@link ModelProperties} for a {@link TestModel}.
 * <p>
 * It delegates any call to {@link #analyze(TestModel)} to the internal {@link ModelAnalysis} instances and aggregates
 * their results. If one or more analyses have overlapping keys, then the result contains only one of them arbitrarily.
 * Therefore, care has to be taken to have non-overlapping keys.
 */
public class ModelAnalyzer {

    private final Collection<ModelAnalysis> analyses;

    /**
     * Constructor.
     *
     * @param analyses a list of analyses to use. It must not be {@code null}, nor contain {@code null}
     */
    ModelAnalyzer(Collection<ModelAnalysis> analyses) {
        this.analyses = Objects.requireNonNull(analyses);
    }

    /**
     * Analyze the given model.
     *
     * @param model the model to analyze. It must not be {@code null}.
     * @return the computed metrics
     */
    public ModelProperties analyze(TestModel model) {
        Objects.requireNonNull(model);
        Map<PropertyKey, Number> properties = new HashMap<>();
        for (ModelAnalysis analysis : analyses) {
            properties.putAll(analysis.analyze(model));
        }
        return new ModelProperties(model.getIdentifier(), properties);
    }

}
