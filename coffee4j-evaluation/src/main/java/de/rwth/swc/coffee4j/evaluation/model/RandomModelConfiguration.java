package de.rwth.swc.coffee4j.evaluation.model;


/**
 * Configuration for a {@link RandomModelGenerator}.
 * <p>
 * It contains ranges for all random variables.
 */
public class RandomModelConfiguration {

    private final int minNumberOfParameters;
    private final int maxNumberOfParameters;

    private final int minNumberOfParameterValues;
    private final int maxNumberOfParameterValues;

    private final int numberOfForbiddenCombinations;
    private final int minNumberOfParametersInForbiddenCombination;
    private final int maxNumberOfParametersInForbiddenCombination;

    private final int numberOfScenarios;
    private final int minNumberOfForbiddenCombinationsInScenario;
    private final int maxNumberOfForbiddenCombinationsInScenario;

    private RandomModelConfiguration(Builder builder) {
        this.minNumberOfParameters = builder.minNumberOfParameters;
        this.maxNumberOfParameters = builder.maxNumberOfParameters;
        this.minNumberOfParameterValues = builder.minNumberOfParameterValues;
        this.maxNumberOfParameterValues = builder.maxNumberOfParameterValues;
        this.numberOfForbiddenCombinations = builder.numberOfForbiddenCombinations;
        this.minNumberOfParametersInForbiddenCombination = builder.minNumberOfParametersInForbiddenCombination;
        this.maxNumberOfParametersInForbiddenCombination = builder.maxNumberOfParametersInForbiddenCombination;
        this.numberOfScenarios = builder.numberOfScenarios;
        this.minNumberOfForbiddenCombinationsInScenario = builder.minNumberOfForbiddenCombinationsInScenario;
        this.maxNumberOfForbiddenCombinationsInScenario = builder.maxNumberOfForbiddenCombinationsInScenario;
    }

    int getMinNumberOfParameters() {
        return minNumberOfParameters;
    }

    int getMaxNumberOfParameters() {
        return maxNumberOfParameters;
    }

    int getMinNumberOfParameterValues() {
        return minNumberOfParameterValues;
    }

    int getMaxNumberOfParameterValues() {
        return maxNumberOfParameterValues;
    }

    int getNumberOfForbiddenCombinations() {
        return numberOfForbiddenCombinations;
    }

    int getMinNumberOfParametersInForbiddenCombination() {
        return minNumberOfParametersInForbiddenCombination;
    }

    int getMaxNumberOfParametersInForbiddenCombination() {
        return maxNumberOfParametersInForbiddenCombination;
    }

    int getNumberOfScenarios() {
        return numberOfScenarios;
    }

    int getMinNumberOfForbiddenCombinationsInScenario() {
        return minNumberOfForbiddenCombinationsInScenario;
    }

    int getMaxNumberOfForbiddenCombinationsInScenario() {
        return maxNumberOfForbiddenCombinationsInScenario;
    }

    /**
     * Fluent builder for {@link RandomModelConfiguration}.
     */
    public static class Builder {

        private int minNumberOfParameters = 1;
        private int maxNumberOfParameters = 1;
        private int minNumberOfParameterValues = 1;
        private int maxNumberOfParameterValues = 1;
        private int numberOfForbiddenCombinations = 1;
        private int minNumberOfParametersInForbiddenCombination = 1;
        private int maxNumberOfParametersInForbiddenCombination = 1;
        private int numberOfScenarios = 1;
        private int minNumberOfForbiddenCombinationsInScenario = 1;
        private int maxNumberOfForbiddenCombinationsInScenario = 1;

        /**
         * Sets the range for the number of parameters.
         * <p>
         * The range has to satisfy the constraint {@literal 0 < min <= max}.
         *
         * @param minimumInclusive the minimum number of parameters.
         * @param maximumInclusive the maximum number of parameters.
         * @return the builder for method chaining
         */
        public Builder withNumberOfParameters(int minimumInclusive, int maximumInclusive) {
            checkRange(minimumInclusive, maximumInclusive);
            this.minNumberOfParameters = minimumInclusive;
            this.maxNumberOfParameters = maximumInclusive + 1;
            return this;
        }

        /**
         * Sets the range for the number of values per parameter.
         * <p>
         * The range has to satisfy the constraint {@literal 0 < min <= max}.
         *
         * @param minimumInclusive the minimum number of parameters values.
         * @param maximumInclusive the maximum number of parameters values.
         * @return the builder for method chaining
         */
        public Builder withNumberOfParametersValues(int minimumInclusive, int maximumInclusive) {
            checkRange(minimumInclusive, maximumInclusive);
            this.minNumberOfParameterValues = minimumInclusive;
            this.maxNumberOfParameterValues = maximumInclusive + 1;
            return this;
        }


        /**
         * Sets the number of forbidden combinations.
         * <p>
         * These will be referenced in the construction of scenarios. The higher this number is, the less similar the
         * created scenarios will be. By itself the number has no influence on the model.
         *
         * @param numberOfForbiddenCombinations the number of forbidden combinations
         * @return the builder for method chaining
         */
        public Builder withNumberOfForbiddenCombinations(int numberOfForbiddenCombinations) {
            if (numberOfForbiddenCombinations <= 0) {
                throw new IllegalArgumentException("Must have at least 1 forbidden combination.");
            }
            this.numberOfForbiddenCombinations = numberOfForbiddenCombinations;
            return this;
        }

        /**
         * Sets the range for the number of parameters in a forbidden combination.
         * <p>
         * This essentially restricts the strength of the contained faults. The range has to satisfy the constraint
         * {@literal 0 < min <= max}.
         *
         * @param minimumInclusive the minimum number of parameters in a forbidden combination.
         * @param maximumInclusive the maximum number of parameters in a forbidden combination.
         * @return the builder for method chaining
         */
        public Builder withNumberOfParametersInForbiddenCombination(int minimumInclusive, int maximumInclusive) {
            checkRange(minimumInclusive, maximumInclusive);
            this.minNumberOfParametersInForbiddenCombination = minimumInclusive;
            this.maxNumberOfParametersInForbiddenCombination = maximumInclusive + 1;
            return this;
        }

        private void checkRange(int minimumInclusive, int maximumInclusive) {
            if (minimumInclusive <= 0) {
                throw new IllegalArgumentException("The values need to greater than 0.");
            }
            if (maximumInclusive < minimumInclusive) {
                throw new IllegalArgumentException("Maximum must be larger or equals to the minimum");
            }
        }


        /**
         * Sets the number of scenarios.
         *
         * @param numberOfScenarios the number of scenarios
         * @return the builder for method chaining
         */
        public Builder withNumberOfScenarios(int numberOfScenarios) {
            if (numberOfScenarios <= 0) {
                throw new IllegalArgumentException("Must have at least 1 scenario.");
            }
            this.numberOfScenarios = numberOfScenarios;
            return this;
        }

        /**
         * Sets the range for the number of faults in a scenario.
         * <p>
         * The range has to satisfy the constraint {@literal 0 < min <= max}.
         *
         * @param minimumInclusive the minimum number of parameters values.
         * @param maximumInclusive the maximum number of parameters values.
         * @return the builder for method chaining
         */
        public Builder withNumberOfForbiddenCombinationsInScenario(int minimumInclusive, int maximumInclusive) {
            checkRange(minimumInclusive, maximumInclusive);
            this.minNumberOfForbiddenCombinationsInScenario = minimumInclusive;
            this.maxNumberOfForbiddenCombinationsInScenario = maximumInclusive + 1;
            return this;
        }


        /**
         * Creates the configuration.
         * <p>
         * Should any value have not been set they are assigned the default value of 1.
         *
         * @return the created configuration
         */
        public RandomModelConfiguration createRandomSystemModelConfiguration() {
            return new RandomModelConfiguration(this);
        }
    }
}
