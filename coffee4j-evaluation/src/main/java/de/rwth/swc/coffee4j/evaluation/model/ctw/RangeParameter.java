package de.rwth.swc.coffee4j.evaluation.model.ctw;

import java.util.InputMismatchException;

class RangeParameter implements Parameter {
    private final String name;
    private final int start;
    private final int end;

    RangeParameter(String name, int start, int end) {
        if (start > end) {
            throw new InputMismatchException("Range for parameter " + name + "invalid.");
        }
        this.name = name;
        this.start = start;
        this.end = end;
    }

    @Override
    public int getMappedValue(String string) {
        try {
            int value = Integer.parseInt(string);
            int mapped = Math.abs(value - start);
            if (mapped > end - start) {
                throw new InputMismatchException("Value " + value + " not in range for parameter " + name + ".");
            }
            return mapped;
        } catch (NumberFormatException e) {
            throw new InputMismatchException("Trying to assign non-integer value to range parameter " + name + ".");
        }
    }

    @Override
    public int getNumberOfValues() {
        return (end - start) + 1;
    }

    @Override
    public String getName() {
        return name;
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
        if (!(obj instanceof RangeParameter)) {
            return false;
        }
        return this.name.equals(((RangeParameter) obj).name);
    }
}
