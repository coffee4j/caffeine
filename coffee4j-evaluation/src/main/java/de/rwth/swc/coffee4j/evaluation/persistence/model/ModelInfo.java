package de.rwth.swc.coffee4j.evaluation.persistence.model;

/**
 * Class containing some information about stored models.
 * <p>
 * This class is only a container, the actual computation happens elsewhere.
 */
public class ModelInfo {

    private final int numberOfModels;
    private final int numberOfScenarios;

    /**
     * Constructor.
     *
     * @param numberOfModels    the number of models stored
     * @param numberOfScenarios the number of scenarios stored
     */
    public ModelInfo(int numberOfModels, int numberOfScenarios) {
        this.numberOfModels = numberOfModels;
        this.numberOfScenarios = numberOfScenarios;
    }

    /**
     * Gets the number of stored models.
     *
     * @return the number of models
     */
    public int getNumberOfModels() {
        return numberOfModels;
    }

    /**
     * Gets the number of stored scenarios.
     *
     * @return the number of scenarios.
     */
    public int getNumberOfScenarios() {
        return numberOfScenarios;
    }
}
