package de.rwth.swc.coffee4j.evaluation.command.model;

import de.rwth.swc.coffee4j.evaluation.command.Main;
import de.rwth.swc.coffee4j.evaluation.command.SimpleCommandFactory;
import de.rwth.swc.coffee4j.evaluation.persistence.SimpleDatabaseAdapter;
import de.rwth.swc.coffee4j.evaluation.utils.ExitCode;
import org.junit.jupiter.api.Test;

import java.io.OutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ModelCommandTest {

    @Test
    void modelClearShouldDoNothingOnEmptyDatabase() {
        SimpleDatabaseAdapter db = new SimpleDatabaseAdapter();
        // Silence the usage output
        System.setErr(new PrintStream(OutputStream.nullOutputStream()));

        Main main = new Main(new SimpleCommandFactory(db));
        int exitCode = main.run("model");
        assertEquals(ExitCode.INVALID_INPUT.exitCode(), exitCode);
    }

}