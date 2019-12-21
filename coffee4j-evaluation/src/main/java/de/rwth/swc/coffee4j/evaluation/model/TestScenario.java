package de.rwth.swc.coffee4j.evaluation.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * An immutable class that specifies evaluation scenarios on a {@link TestModel}.
 * <p>
 * Each scenario contains:
 * <ul>
 *     <li> a name (which should be unique)
 *     <li> a testing strength
 *     <li> a list of constraint identifiers which should be treated as faults that should be characterized
 *     <li> a list of constraint identifiers which should be treated as previously known constraints of the model
 * </ul>
 * <p>
 * Each scenario uniquely belongs to a model an can only be constructed through an instance of a {@link TestModel.Builder}.
 * This makes it impossible to create model-less scenarios. The constraint modifiers must be present in the model,
 * otherwise accessing it during a trace will throw an exception.
 */
public final class TestScenario {

    private final ScenarioIdentifier identifier;
    private final int strength;
    private final List<String> faults;
    private final List<String> constraints;

    private TestScenario(Builder builder) {
        this.identifier = Objects.requireNonNull(builder.identifier);
        this.strength = builder.strength;
        this.faults = Objects.requireNonNull(builder.faults);
        this.constraints = Objects.requireNonNull(builder.constraints);
    }

    /**
     * Gets the identifier of the scenario.
     *
     * @return the scenario identifier
     */
    public ScenarioIdentifier getIdentifier() {
        return identifier;
    }

    /**
     * Gets the testing strength of the scenario.
     *
     * @return the testing strength
     */
    public int getStrength() {
        return strength;
    }

    /**
     * Gets an immutable copy of the fault identifiers.
     *
     * @return the fault identifiers
     */
    public List<String> getFaults() {
        return Collections.unmodifiableList(faults);
    }

    /**
     * Gets an immutable copy of the constraint identifiers.
     *
     * @return the constraint identifiers
     */
    public List<String> getConstraints() {
        return Collections.unmodifiableList(constraints);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof TestScenario) {
            TestScenario other = (TestScenario) obj;
            return this.identifier.equals(other.identifier) &&
                    this.strength == other.strength &&
                    this.faults.equals(other.faults) &&
                    this.constraints.equals(other.constraints);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return identifier.hashCode();
    }

    /**
     * Builder class for {@link TestScenario} instances.
     * <p>
     * It should only be accessed using {@link TestModel.Builder#scenario(String)}. This guarantees that each scenario
     * belongs to a model.
     */
    public static class Builder {

        private final TestModel.Builder parent;
        private final ScenarioIdentifier identifier;
        private final List<String> faults = new ArrayList<>();
        private final List<String> constraints = new ArrayList<>();
        private int strength;

        /**
         * Constructor.
         *
         * @param parent builder of the parent model
         * @param name   the scenario name
         */
        Builder(TestModel.Builder parent, ModelIdentifier parentIdentifier, String name) {
            this.parent = parent;
            this.identifier = new ScenarioIdentifier(parentIdentifier, name);
        }


        /**
         * Sets the testing strength of the scenario.
         *
         * @param strength the testing strength
         * @return the builder for method chaining
         */
        public Builder withStrength(int strength) {
            if (strength < 0) {
                throw new IllegalArgumentException("Strength must be at least 0.");
            }
            this.strength = strength;
            return this;
        }

        /**
         * Adds a fault to the scenario.
         * <p>
         * The given identifier must be contained in the parent model.
         *
         * @param faultName the identifier of the fault
         * @return the builder for method chaining
         */
        public Builder withFault(String faultName) {
            this.faults.add(faultName);
            return this;
        }

        /**
         * Adds multiple faults to the scenario.
         * <p>
         * The given identifiers must be contained in the parent model.
         *
         * @param faultNames the identifiers of the faults
         * @return the builder for method chaining
         */
        public Builder withFaults(String... faultNames) {
            Collections.addAll(faults, faultNames);
            return this;
        }

        /**
         * Adds multiple faults to the scenario.
         * <p>
         * The given identifiers must be contained in the parent model.
         *
         * @param faultNames the identifiers of the faults
         * @return the builder for method chaining
         */
        public Builder withFaults(List<String> faultNames) {
            this.faults.addAll(faultNames);
            return this;
        }

        /**
         * Adds a constraint to the scenario.
         * <p>
         * The given identifier must be contained in the parent model.
         *
         * @param constraintName the identifier of the constraint
         * @return the builder for method chaining
         */
        public Builder withConstraint(String constraintName) {
            this.constraints.add(constraintName);
            return this;
        }

        /**
         * Adds multiple constraints to the scenario.
         * <p>
         * The given identifiers must be contained in the parent model.
         *
         * @param constraintNames the identifier of the constraints
         * @return the builder for method chaining
         */
        public Builder withConstraints(String... constraintNames) {
            Collections.addAll(constraints, constraintNames);
            return this;
        }

        /**
         * Adds multiple constraints to the scenario.
         * <p>
         * The given identifiers must be contained in the parent model.
         *
         * @param constraintNames the identifier of the constraints
         * @return the builder for method chaining
         */
        public Builder withConstraints(List<String> constraintNames) {
            this.constraints.addAll(constraintNames);
            return this;
        }

        /**
         * Returns the builder for the parent model after completing scenario construction.
         *
         * @return the parent builder for method chaining
         */
        public TestModel.Builder buildScenario() {
            return parent;
        }

        /**
         * Gets the constructed scenario.
         * <p>
         * This is used to access the scenario by the parent builder after construction.
         *
         * @return the scenario
         */
        TestScenario getValue() {
            return new TestScenario(this);
        }

    }
}
