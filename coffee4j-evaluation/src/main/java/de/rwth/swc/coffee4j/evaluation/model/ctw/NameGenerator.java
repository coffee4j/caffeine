package de.rwth.swc.coffee4j.evaluation.model.ctw;

import org.antlr.v4.runtime.Token;

import java.util.HashSet;
import java.util.InputMismatchException;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.IntStream;

class NameGenerator {

    private final Iterator<String> generator;
    private final Set<String> usedNames = new HashSet<>();

    NameGenerator(String namePrefix) {
        this.generator = IntStream.iterate(0, i -> i + 1).mapToObj(i -> namePrefix + i).iterator();
    }

    String getOrGenerate(Token node) {
        if (node == null) {
            return getNextName();
        } else {
            String name = node.getText();
            useName(name);
            return name;
        }
    }

    String getNextName() {
        String name = generator.next();
        while (usedNames.contains(name)) {
            name = generator.next();
        }
        useName(name);
        return name;
    }

    private void useName(String name) {
        if (!usedNames.add(name)) {
            throw new InputMismatchException("Name " + name + "already bound.");
        }
    }

}
