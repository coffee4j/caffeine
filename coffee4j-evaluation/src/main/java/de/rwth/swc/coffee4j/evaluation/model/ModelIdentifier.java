package de.rwth.swc.coffee4j.evaluation.model;

import java.util.Objects;

/**
 * Class that uniquely identifies a {@link TestModel}.
 */
public final class ModelIdentifier {

    private final String modelName;

    /**
     * Constructor.
     *
     * @param modelName the name of the model.
     */
    public ModelIdentifier(String modelName) {
        this.modelName = Objects.requireNonNull(modelName);
    }

    /**
     * Gets the name of the model.
     *
     * @return the model name
     */
    public String getModelName() {
        return modelName;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ModelIdentifier) {
            return this.modelName.equals(((ModelIdentifier) obj).modelName);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(modelName);
    }

    @Override
    public String toString() {
        return modelName;
    }
}
