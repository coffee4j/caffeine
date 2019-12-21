package de.rwth.swc.coffee4j.evaluation.command.model;

import de.rwth.swc.coffee4j.evaluation.TestData;
import de.rwth.swc.coffee4j.evaluation.command.Main;
import de.rwth.swc.coffee4j.evaluation.command.SimpleCommandFactory;
import de.rwth.swc.coffee4j.evaluation.persistence.SimpleDatabaseAdapter;
import de.rwth.swc.coffee4j.evaluation.utils.ExitCode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ModelInfoCommandTest {

    @Test
    void modelInfoShouldNotChangeDatabaseWhenCalledWithoutArgument() {
        SimpleDatabaseAdapter db = new SimpleDatabaseAdapter();
        db.getModelRepository().write(TestData.MODEL_1);
        db.getModelRepository().write(TestData.MODEL_2);

        Main main = new Main(new SimpleCommandFactory(db));
        int exitCode = main.run("model", "info");
        assertEquals(TestData.MODEL_1, db.getModelRepository().get(TestData.MODEL_1.getIdentifier()));
        assertEquals(TestData.MODEL_2, db.getModelRepository().get(TestData.MODEL_2.getIdentifier()));
        assertEquals(ExitCode.SUCCESS.exitCode(), exitCode);
    }

    @Test
    void modelInfoShouldNotChangeDatabaseWithArgument() {
        SimpleDatabaseAdapter db = new SimpleDatabaseAdapter();
        db.getModelRepository().write(TestData.MODEL_1);
        db.getModelRepository().write(TestData.MODEL_2);

        Main main = new Main(new SimpleCommandFactory(db));
        int exitCode = main.run("model", "info", TestData.MODEL_1.getIdentifier().toString());
        assertEquals(TestData.MODEL_1, db.getModelRepository().get(TestData.MODEL_1.getIdentifier()));
        assertEquals(TestData.MODEL_2, db.getModelRepository().get(TestData.MODEL_2.getIdentifier()));
        assertEquals(ExitCode.SUCCESS.exitCode(), exitCode);
    }

    @Test
    void modelInfoShouldErrorIfModelNotInDatabase() {
        SimpleDatabaseAdapter db = new SimpleDatabaseAdapter();
        db.getModelRepository().write(TestData.MODEL_1);
        db.getModelRepository().write(TestData.MODEL_2);

        Main main = new Main(new SimpleCommandFactory(db));
        int exitCode = main.run("model", "info", "Test");
        assertEquals(TestData.MODEL_1, db.getModelRepository().get(TestData.MODEL_1.getIdentifier()));
        assertEquals(TestData.MODEL_2, db.getModelRepository().get(TestData.MODEL_2.getIdentifier()));
        assertEquals(ExitCode.ERROR.exitCode(), exitCode);
    }

}