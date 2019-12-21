package de.rwth.swc.coffee4j.evaluation.model.ctw;

import java.util.InputMismatchException;

class BooleanParameter implements Parameter {
    private final String name;

    BooleanParameter(String name) {
        this.name = name;
    }

    @Override
    public int getMappedValue(String string) {
        if (string.equalsIgnoreCase("true")) {
            return 1;
        } else if (string.equalsIgnoreCase("false")) {
            return 0;
        } else {
            throw new InputMismatchException("Trying to assign non-boolean value to boolean parameter " + name + ".");
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getNumberOfValues() {
        return 2;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof BooleanParameter)) {
            return false;
        }
        return this.name.equals(((BooleanParameter) obj).name);
    }
}
