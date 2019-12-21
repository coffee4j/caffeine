package de.rwth.swc.coffee4j.evaluation.command.model;

import de.rwth.swc.coffee4j.evaluation.TestData;
import de.rwth.swc.coffee4j.evaluation.command.Main;
import de.rwth.swc.coffee4j.evaluation.command.SimpleCommandFactory;
import de.rwth.swc.coffee4j.evaluation.model.JsonModelAdapter;
import de.rwth.swc.coffee4j.evaluation.persistence.SimpleDatabaseAdapter;
import de.rwth.swc.coffee4j.evaluation.utils.ExitCode;
import okio.BufferedSink;
import okio.Okio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ModelImportCommandTest {

    @Test
    void shouldDoNothingIfNoCorrectFilesAreFound(@TempDir Path path) throws IOException {
        SimpleDatabaseAdapter db = new SimpleDatabaseAdapter();
        JsonModelAdapter adapter = new JsonModelAdapter();
        try (BufferedSink m1Sink = Okio.buffer(Okio.sink(path.resolve("MODEL_1.notJson")));
             BufferedSink m2Sink = Okio.buffer(Okio.sink(path.resolve("MODEL_2.notJson")))) {
            adapter.export(m1Sink, TestData.MODEL_1);
            adapter.export(m2Sink, TestData.MODEL_2);
        }

        Main main = new Main(new SimpleCommandFactory(db));
        int exitCode = main.run("model", "import", "--format", "JSON", "--path", path.toString());
        assertEquals(ExitCode.SUCCESS.exitCode(), exitCode);
        assertEquals(0, db.getModelRepository().info().getNumberOfModels());
    }

    @Test
    void shouldImportModels(@TempDir Path path) throws IOException {
        SimpleDatabaseAdapter db = new SimpleDatabaseAdapter();
        JsonModelAdapter adapter = new JsonModelAdapter();
        try (BufferedSink m1Sink = Okio.buffer(Okio.sink(path.resolve("MODEL_1.json")));
             BufferedSink m2Sink = Okio.buffer(Okio.sink(path.resolve("MODEL_2.json")))) {
            adapter.export(m1Sink, TestData.MODEL_1);
            adapter.export(m2Sink, TestData.MODEL_2);
        }

        Main main = new Main(new SimpleCommandFactory(db));
        int exitCode = main.run("model", "import", "--format", "JSON", "--path", path.toString());
        assertEquals(ExitCode.SUCCESS.exitCode(), exitCode);
        assertEquals(2, db.getModelRepository().info().getNumberOfModels());
    }

}