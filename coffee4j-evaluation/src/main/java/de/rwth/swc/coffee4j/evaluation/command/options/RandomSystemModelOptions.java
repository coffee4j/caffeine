package de.rwth.swc.coffee4j.evaluation.command.options;

import de.rwth.swc.coffee4j.evaluation.model.RandomModelConfiguration;
import picocli.CommandLine;

/**
 * Command line options for the {@link de.rwth.swc.coffee4j.evaluation.command.model.ModelGenerateCommand}.
 * <p>
 * It provides ranges for all important random variables and constructs a {@link RandomModelConfiguration} which can be
 * used in a {@link de.rwth.swc.coffee4j.evaluation.model.RandomModelGenerator} to generate random models.
 */
public class RandomSystemModelOptions {

    @CommandLine.Option(names = {"--number"}, description = "Number of models to generate.", defaultValue = "1")
    private int numberOfSystemModels;

    @CommandLine.Option(names = "--minParameters", description = "Minimum number of parameters.", required = true)
    private int minNumberOfParameters;

    @CommandLine.Option(names = "--maxParameters", description = "Maximum number of parameters (inclusive).", required = true)
    private int maxNumberOfParameters;

    @CommandLine.Option(names = "--minValues", description = "Minimum number of values per parameter.", required = true)
    private int minNumberOfValues;

    @CommandLine.Option(names = "--maxValues", description = "Maximum number of values per parameter (inclusive).", required = true)
    private int maxNumberOfValues;

    @CommandLine.Option(names = "--faults", description = "Number of faults.", required = true)
    private int noOfFaults;

    @CommandLine.Option(names = "--minFaultParameters", description = "Minimum number of parameters in a fault.", required = true)
    private int minNumberOfParametersPerFault;

    @CommandLine.Option(names = "--maxFaultParameters", description = "Maximum number of parameters in a fault (inclusive).", required = true)
    private int maxNumberOfParametersPerFault;

    @CommandLine.Option(names = "--scenarios", description = "Number of scenarios.", required = true)
    private int noOfScenarios;

    @CommandLine.Option(names = "--minScenarioFaults", description = "Minimum number of faults in a scenario.", required = true)
    private int minNumberOfFaultsPerScenario;

    @CommandLine.Option(names = "--maxScenarioFaults", description = "Maximum number of faults in a scenario (inclusive).", required = true)
    private int maxNumberOfFaultsPerScenario;

    /**
     * Gets a configuration class with all random variable ranges.
     *
     * @return the random model configuration
     */
    public RandomModelConfiguration getConfiguration() {
        return new RandomModelConfiguration.Builder()
                .withNumberOfScenarios(noOfScenarios)
                .withNumberOfParameters(minNumberOfParameters, maxNumberOfParameters)
                .withNumberOfParametersValues(minNumberOfValues, maxNumberOfValues)
                .withNumberOfParametersInForbiddenCombination(minNumberOfParametersPerFault, maxNumberOfParametersPerFault)
                .withNumberOfForbiddenCombinationsInScenario(minNumberOfFaultsPerScenario, maxNumberOfFaultsPerScenario)
                .withNumberOfForbiddenCombinations(noOfFaults)
                .createRandomSystemModelConfiguration();
    }

    /**
     * Gets the number of models that should be generated.
     *
     * @return the number of models
     */
    public int getNumberOfSystemModels() {
        return numberOfSystemModels;
    }
}
