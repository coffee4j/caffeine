package de.rwth.swc.coffee4j.evaluation.model.ctw;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class EnumParameter implements Parameter {

    private final String name;
    private final Map<String, Integer> values;


    EnumParameter(String name, List<String> values) {
        if (values.size() > new HashSet<>(values).size()) {
            throw new InputMismatchException("Enum parameter contains duplicates.");
        }
        this.name = name;
        this.values = IntStream.range(0, values.size()).boxed()
                .collect(Collectors.toMap(values::get, index -> index));
    }

    @Override
    public int getMappedValue(String string) {
        return Optional.ofNullable(values.get(string)).orElseThrow(() -> new InputMismatchException("Variable "
                + string + " not in parameter " + name));
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getNumberOfValues() {
        return values.size();
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
        if (!(obj instanceof EnumParameter)) {
            return false;
        }
        return this.name.equals(((EnumParameter) obj).name);
    }
}
