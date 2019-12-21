package de.rwth.swc.coffee4j.evaluation.model;

import java.util.Objects;

/**
 * Class that uniquely identifies a {@link TestScenario}.
 */
public class ScenarioIdentifier {

    private final ModelIdentifier modelIdentifier;
    private final String scenarioName;

    /**
     * Constructor.
     *
     * @param modelIdentifier the identifier of the parent model
     * @param scenarioName    the name of the scenario
     */
    public ScenarioIdentifier(ModelIdentifier modelIdentifier, String scenarioName) {
        this.modelIdentifier = modelIdentifier;
        this.scenarioName = scenarioName;
    }

    /**
     * Gets the identifier of the parent model.
     *
     * @return the model identifier
     */
    public ModelIdentifier getModelIdentifier() {
        return modelIdentifier;
    }

    /**
     * Gets the name of the scenario.
     *
     * @return the scenario name
     */
    public String getScenarioName() {
        return scenarioName;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ScenarioIdentifier) {
            ScenarioIdentifier other = (ScenarioIdentifier) obj;
            return this.modelIdentifier.equals(other.modelIdentifier) && this.scenarioName.equals(other.scenarioName);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(modelIdentifier, scenarioName);
    }

    @Override
    public String toString() {
        return modelIdentifier + "_" + scenarioName;
    }
}
