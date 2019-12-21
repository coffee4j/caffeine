package de.rwth.swc.coffee4j.evaluation.model;


import de.rwth.swc.coffee4j.evaluation.utils.CombinationUtil;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * An immutable class representing testing scenarios for the evaluation process.
 * <p>
 * It contains information about the System under Test (SuT):
 * <ul>
 *     <li> a name (which should be unique)
 *     <li> the parameter values
 *     <li> a list of named possible forbidden combinations
 * </ul>
 * <p>
 * Additionally, each model may contain multiple {@link TestScenario} instances which can then be used for tracing.
 * Each scenario selects a subset of the possible forbidden combinations of the modes, assigns them to faults
 * and constraints, and provides a testing strength. This allows us to specify multiple evaluation scenarios
 * on single SuT.
 */
public final class TestModel {

    private final ModelIdentifier identifier;
    private final int[] parameters;
    private final Map<String, int[]> constraints;
    private final Map<String, TestScenario> scenarios;

    private TestModel(Builder builder) {
        this.identifier = Objects.requireNonNull(builder.identifier);
        this.parameters = Objects.requireNonNull(builder.parameters);
        this.constraints = Objects.requireNonNull(builder.constraints);
        this.scenarios = Objects.requireNonNull(builder.scenarios);
        checkConstraints();
        checkScenarioIdentifiers();
    }

    private void checkConstraints() {
        for (Map.Entry<String, int[]> constraint : constraints.entrySet()) {
            Objects.requireNonNull(constraint.getValue());
            if (!CombinationUtil.isValid(constraint.getValue(), parameters)) {
                throw new IllegalArgumentException("Constraint " + constraint.getKey() + "has invalid combination.");
            }

        }
    }

    private void checkScenarioIdentifiers() {
        for (TestScenario scenario : scenarios.values()) {
            for (String fault : scenario.getFaults()) {
                checkIdentifiers(fault);
            }
            for (String constraint : scenario.getConstraints()) {
                checkIdentifiers(constraint);
            }
        }
    }

    private void checkIdentifiers(String fault) {
        if (!constraints.containsKey(fault)) {
            throw new IllegalArgumentException("Constraint identifier" + fault + "not present in model.");
        }
    }

    /**
     * Gets the parameter values of the system under test.
     *
     * @return the parameter values.
     */
    public int[] getParameters() {
        return parameters;
    }

    /**
     * Gets the {@link TestScenario} for a given scenario name.
     *
     * @param name the name of the scenario
     * @return the scenario
     * @throws NoSuchElementException when the model contains no scenario with this name
     */
    public TestScenario getScenario(String name) {
        return Optional.ofNullable(scenarios.get(name))
                .orElseThrow(() -> new NoSuchElementException("No scenario with the given name in model."));
    }

    /**
     * Gets an unmodifiable copy of all scenarios grouped by their names.
     *
     * @return the scenarios
     */
    public Map<String, TestScenario> getScenarios() {
        return Collections.unmodifiableMap(scenarios);
    }

    /**
     * Gets a forbidden combination for a given constraint name.
     *
     * @param name the name of the constraint
     * @return the forbidden combination
     * @throws NoSuchElementException when the model contains no constraint with this name
     */
    public int[] getConstraint(String name) {
        return Optional.ofNullable(constraints.get(name))
                .orElseThrow(() -> new NoSuchElementException("No scenario with the given name in model."));
    }

    /**
     * Gets an unmodifiable copy of all constraints grouped by their names.
     *
     * @return the constraints
     */
    public Map<String, int[]> getConstraints() {
        return Collections.unmodifiableMap(constraints);
    }

    /**
     * Gets the faults for a contained testing scenario.
     *
     * @param scenario the scenario
     * @return the faults for this scenario by name
     */
    public Map<String, int[]> getFaultsForScenario(TestScenario scenario) {
        if (!scenarios.containsKey(scenario.getIdentifier().getScenarioName())) {
            throw new IllegalArgumentException("Scenario must be contained in model.");
        }
        return scenarios.get(scenario.getIdentifier().getScenarioName())
                .getFaults().stream()
                .collect(Collectors.toMap(id -> id, constraints::get));
    }

    /**
     * Gets the forbidden combination constraints for a contained testing scenario.
     *
     * @param scenario the scenario
     * @return the constraints for this scenario by name
     */
    public Map<String, int[]> getConstraintsForScenario(TestScenario scenario) {
        if (!scenarios.containsKey(scenario.getIdentifier().getScenarioName())) {
            throw new IllegalArgumentException("Scenario must be contained in model.");
        }
        return scenarios.get(scenario.getIdentifier().getScenarioName())
                .getConstraints().stream()
                .collect(Collectors.toMap(id -> id, constraints::get));
    }

    /**
     * Gets the model name.
     *
     * @return the name of the model
     */
    public ModelIdentifier getIdentifier() {
        return this.identifier;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof TestModel) {
            TestModel other = (TestModel) obj;
            return this.identifier.equals(other.identifier) &&
                    Arrays.equals(this.parameters, other.parameters) &&
                    CombinationUtil.equals(this.constraints, other.constraints) &&
                    this.scenarios.equals(other.scenarios);

        }
        return false;
    }

    @Override
    public int hashCode() {
        return identifier.hashCode();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("================================================================================\n");
        sb.append(identifier).append("\n")
                .append("Parameters: ").append(Arrays.toString(parameters)).append("\n")
                .append("Potentially forbidden combinations:\n");

        for (Map.Entry<String, int[]> constraint : constraints.entrySet()) {
            sb.append("\t").append(constraint.getKey()).append(":\t")
                    .append(Arrays.toString(constraint.getValue()).replaceAll("-1", "-")).append("\n");
        }
        sb.append("Scenarios:\n");
        for (TestScenario scenario : scenarios.values()) {
            sb.append("\t").append(scenario.getIdentifier().getScenarioName())
                    .append(": Strength=").append(scenario.getStrength())
                    .append("\n\t\tFaults=").append(scenario.getFaults())
                    .append("\n\t\tConstraints=").append(scenario.getConstraints())
                    .append("\n");
        }
        sb.append("================================================================================");
        return sb.toString();
    }

    /**
     * Builder class for {@link TestModel} instances.
     * <p>
     * It provides nested access to {@link TestScenario.Builder} for constructing contained scenarios. This is the only
     * way to create instances of {@link TestScenario} and guarantees that every scenario uniquely belongs to a model.
     */
    public static class Builder {

        private final ModelIdentifier identifier;
        private final Map<String, int[]> constraints = new HashMap<>();
        private final Map<String, TestScenario> scenarios = new HashMap<>();
        private int[] parameters;
        private TestScenario.Builder scenarioBuilder;

        /**
         * Constructor.
         * <p>
         * The given name should be unique, it is used in the evaluation process as a way to identify models.
         *
         * @param name the model of the name
         */
        public Builder(String name) {
            this.identifier = new ModelIdentifier(name);
        }

        /**
         * Adds parameter values to the model.
         *
         * @param parameters the parameter values
         * @return the builder for method chaining
         */
        public Builder withParameters(int... parameters) {
            if (parameters.length < 1) {
                throw new IllegalArgumentException("Model must have at least one parameter.");
            }
            if (IntStream.of(parameters).anyMatch(i -> i < 0)) {
                throw new IllegalArgumentException("All parameter values must be positive.");
            }
            this.parameters = parameters;
            return this;
        }

        /**
         * Adds a constraint to the model.
         * <p>
         * This constraint can then be referenced by the scenarios of this model. The given identifier should be unique
         * within the model.
         *
         * @param identifier           the identifier
         * @param forbiddenCombination the forbidden combination
         * @return the builder for method chaining
         */
        public Builder withConstraint(String identifier, int... forbiddenCombination) {
            this.constraints.put(identifier, forbiddenCombination);
            return this;
        }

        /**
         * Adds constraints to the model.
         *
         * @param constraints a map of constraints with entries (identifier, forbidden combination)
         * @return the builder for method chaining
         */
        public Builder withConstraints(Map<String, int[]> constraints) {
            this.constraints.putAll(constraints);
            return this;
        }

        /**
         * Creates a new scenario with a given name.
         * <p>
         * This scenario can then be further specified by the returned scenario builder. Calling {@link
         * TestScenario.Builder#buildScenario()} will return this builder for further configuration of the model. The
         * given identifier should be unique within the model.
         *
         * @param name the scenario name
         * @return a builder for the scenario
         */
        public TestScenario.Builder scenario(String name) {
            if (scenarioBuilder != null) {
                TestScenario scenario = scenarioBuilder.getValue();
                this.scenarios.put(scenario.getIdentifier().getScenarioName(), scenario);
            }
            this.scenarioBuilder = new TestScenario.Builder(this, identifier, name);
            return scenarioBuilder;
        }

        /**
         * Create the model.
         *
         * @return the created model
         */
        public TestModel buildModel() {
            if (scenarioBuilder != null) {
                TestScenario scenario = scenarioBuilder.getValue();
                this.scenarios.put(scenario.getIdentifier().getScenarioName(), scenario);
            }
            return new TestModel(this);
        }
    }

}
