package de.rwth.swc.coffee4j.evaluation.trace;

import de.rwth.swc.coffee4j.evaluation.model.ModelIdentifier;
import de.rwth.swc.coffee4j.evaluation.model.ScenarioIdentifier;

import java.util.Objects;

/**
 * Class that uniquely identifies a {@link Trace}.
 * <p>
 * It contains a specific scenario and the identifier of the used algorithm.
 */
public final class TraceIdentifier {

    private final ScenarioIdentifier scenarioIdentifier;
    private final String algorithmName;

    /**
     * Constructor.
     *
     * @param scenarioIdentifier identifier of the traced scenario
     * @param algorithmName      the name of the used algorithm
     */
    public TraceIdentifier(ScenarioIdentifier scenarioIdentifier, String algorithmName) {
        this.scenarioIdentifier = Objects.requireNonNull(scenarioIdentifier);
        this.algorithmName = Objects.requireNonNull(algorithmName);
    }

    /**
     * Constructor.
     * <p>
     * This constructor also creates the necessary model and scenario identifiers.
     *
     * @param modelName     the name of the model
     * @param scenarioName  the name of the scenario
     * @param algorithmName the name of the algorithm
     */
    public TraceIdentifier(String modelName, String scenarioName, String algorithmName) {
        this.scenarioIdentifier = new ScenarioIdentifier(new ModelIdentifier(modelName), scenarioName);
        this.algorithmName = algorithmName;
    }

    /**
     * Gets the identifier for the evaluated model.
     *
     * @return the model identifier
     */
    public ModelIdentifier getModelIdentifier() {
        return scenarioIdentifier.getModelIdentifier();
    }

    /**
     * Gets the identifier for the evaluated scenario.
     *
     * @return the scenario identifier
     */
    public ScenarioIdentifier getScenarioIdentifier() {
        return scenarioIdentifier;
    }

    /**
     * Gets the name of the evaluated algorithm.
     *
     * @return the algorithm name
     */
    public String getAlgorithmName() {
        return algorithmName;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof TraceIdentifier) {
            TraceIdentifier other = (TraceIdentifier) obj;
            return this.scenarioIdentifier.equals(other.scenarioIdentifier) &&
                    this.algorithmName.equals(other.algorithmName);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(scenarioIdentifier, algorithmName);
    }

    @Override
    public String toString() {
        return String.join("_", scenarioIdentifier.toString(), algorithmName);
    }
}
