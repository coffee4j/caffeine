package de.rwth.swc.coffee4j.evaluation.command.model;

import de.rwth.swc.coffee4j.evaluation.TestData;
import de.rwth.swc.coffee4j.evaluation.command.Main;
import de.rwth.swc.coffee4j.evaluation.command.SimpleCommandFactory;
import de.rwth.swc.coffee4j.evaluation.persistence.SimpleDatabaseAdapter;
import de.rwth.swc.coffee4j.evaluation.utils.ExitCode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ModelClearCommandTest {

    @Test
    void modelClearShouldDoNothingOnEmptyDatabase() {
        SimpleDatabaseAdapter db = new SimpleDatabaseAdapter();

        Main main = new Main(new SimpleCommandFactory(db));
        int exitCode = main.run("model", "clear");
        assertEquals(0, db.getModelRepository().info().getNumberOfModels());
        assertEquals(ExitCode.SUCCESS.exitCode(), exitCode);
    }

    @Test
    void modelClearShouldClearAllStoredModels() {
        SimpleDatabaseAdapter db = new SimpleDatabaseAdapter();
        db.getModelRepository().write(TestData.MODEL_1);
        db.getModelRepository().write(TestData.MODEL_2);

        Main main = new Main(new SimpleCommandFactory(db));
        int exitCode = main.run("model", "clear");
        assertEquals(0, db.getModelRepository().info().getNumberOfModels());
        assertEquals(ExitCode.SUCCESS.exitCode(), exitCode);
    }

}