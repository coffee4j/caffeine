package de.rwth.swc.coffee4j.evaluation.command.model;

import de.rwth.swc.coffee4j.evaluation.TestData;
import de.rwth.swc.coffee4j.evaluation.command.Main;
import de.rwth.swc.coffee4j.evaluation.command.SimpleCommandFactory;
import de.rwth.swc.coffee4j.evaluation.persistence.SimpleDatabaseAdapter;
import de.rwth.swc.coffee4j.evaluation.utils.ExitCode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ModelExportCommandTest {

    @Test
    void shouldDoNothingIfNoModelsStored(@TempDir Path path) throws IOException {
        SimpleDatabaseAdapter db = new SimpleDatabaseAdapter();

        Main main = new Main(new SimpleCommandFactory(db));
        int exitCode = main.run("model", "export", "--path", path.toString());
        assertEquals(ExitCode.SUCCESS.exitCode(), exitCode);
        assertEquals(0, Files.list(path).count());
    }

    @Test
    void shouldExportAllModels(@TempDir Path path) throws IOException {
        SimpleDatabaseAdapter db = new SimpleDatabaseAdapter();
        db.getModelRepository().write(TestData.MODEL_1);
        db.getModelRepository().write(TestData.MODEL_2);

        Main main = new Main(new SimpleCommandFactory(db));
        int exitCode = main.run("model", "export", "--path", path.toString());
        assertEquals(ExitCode.SUCCESS.exitCode(), exitCode);
        assertEquals(2, Files.list(path).count());
    }

}