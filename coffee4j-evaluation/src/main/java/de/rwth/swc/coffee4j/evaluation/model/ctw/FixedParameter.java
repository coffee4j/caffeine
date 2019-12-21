package de.rwth.swc.coffee4j.evaluation.model.ctw;

/**
 * Parameter with a fixed value.
 */
class FixedParameter implements Parameter {

    private final int value;

    FixedParameter(int value) {
        this.value = value;
    }

    @Override
    public String getName() {
        return Integer.toString(value);
    }

    @Override
    public int getMappedValue(String string) {
        return value;
    }

    @Override
    public int getNumberOfValues() {
        return 1;
    }

    @Override
    public int hashCode() {
        return value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof FixedParameter)) {
            return false;
        }
        return this.value == ((FixedParameter) obj).value;
    }
}
