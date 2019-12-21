package de.rwth.swc.coffee4j.evaluation.adapter;

import de.rwth.swc.coffee4j.engine.characterization.FaultCharacterizationAlgorithm;
import de.rwth.swc.coffee4j.engine.characterization.FaultCharacterizationAlgorithmFactory;
import de.rwth.swc.coffee4j.engine.characterization.FaultCharacterizationConfiguration;

import java.util.Objects;

/**
 * Provider class for a {@link FaultCharacterizationAlgorithm}.
 * <p>
 * It provides additional functionality to a {@link FaultCharacterizationAlgorithmFactory}
 * like the fault characterization approach and a display name for logging.
 */
public class FaultCharacterizationAlgorithmProvider {

    private final String displayName;
    private final Type type;
    private final FaultCharacterizationAlgorithmFactory factory;

    /**
     * Constructor.
     *
     * @param displayName the name to be displayed in logging messages. May not be {@code null}.
     * @param type        the fault characterization approach of the contained algorithm. May not be {@code null}.
     * @param factory     the factory to produce the algorithm. May not be {@code null}.
     */
    public FaultCharacterizationAlgorithmProvider(String displayName,
                                                  Type type,
                                                  FaultCharacterizationAlgorithmFactory factory) {
        this.displayName = Objects.requireNonNull(displayName);
        this.type = Objects.requireNonNull(type);
        this.factory = Objects.requireNonNull(factory);
    }

    /**
     * Gets the fault characterization approach.
     *
     * @return the fault characterization approach
     */
    public Type getType() {
        return type;
    }

    /**
     * Gets the display name for logging purposes.
     *
     * @return the display name
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Create the contained fault characterization algorithm.
     * <p>
     * Delegates this call to the internal {@link FaultCharacterizationAlgorithmFactory}.
     *
     * @param configuration the configuration for the fault characterization algorithm. May not be {@code null}.
     * @return the fault characterization algorithm
     */
    FaultCharacterizationAlgorithm create(FaultCharacterizationConfiguration configuration) {
        return factory.create(Objects.requireNonNull(configuration));
    }

    /**
     * Enum for the different approaches to fault characterization.
     */
    public enum Type {
        /**
         * Algorithm does not request any additional test cases.
         */
        STATIC,
        /**
         * Algorithm request additional test cases after the covering array has been generated.
         */
        ADAPTIVE,
        /**
         * Algorithm that runs interleaved with the covering array generation.
         */
        INTERLEAVED
    }

}
