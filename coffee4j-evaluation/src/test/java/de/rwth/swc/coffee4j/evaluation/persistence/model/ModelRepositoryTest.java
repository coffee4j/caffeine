package de.rwth.swc.coffee4j.evaluation.persistence.model;

import de.rwth.swc.coffee4j.evaluation.TestData;
import de.rwth.swc.coffee4j.evaluation.model.ModelIdentifier;
import de.rwth.swc.coffee4j.evaluation.model.TestModel;
import de.rwth.swc.coffee4j.evaluation.persistence.DatabaseAdapter;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

interface ModelRepositoryTest {

    DatabaseAdapter provideDataSource() throws IOException;

    @Test
    default void shouldBeEmptyOnCreation() throws IOException {
        try (DatabaseAdapter db = provideDataSource()) {
            ModelRepository modelRepository = db.getModelRepository();
            assertFalse(modelRepository.exists(new ModelIdentifier("")));
        }
    }

    @Test
    default void shouldReadWrittenModels() throws IOException {
        try (DatabaseAdapter db = provideDataSource()) {
            ModelRepository modelRepository = db.getModelRepository();
            modelRepository.write(TestData.MODEL_1);
            modelRepository.write(TestData.MODEL_2);
            modelRepository.write(TestData.MODEL_EMPTY);
            modelRepository.write(TestData.MODEL_EMPTY_SCENARIO);

            assertTrue(modelRepository.exists(TestData.MODEL_1.getIdentifier()));
            assertTrue(modelRepository.exists(TestData.MODEL_2.getIdentifier()));
            assertTrue(modelRepository.exists(TestData.MODEL_EMPTY.getIdentifier()));
            assertTrue(modelRepository.exists(TestData.MODEL_EMPTY_SCENARIO.getIdentifier()));
            assertFalse(modelRepository.exists(new ModelIdentifier("")));

            assertEquals(TestData.MODEL_1, modelRepository.get(TestData.MODEL_1.getIdentifier()));
            assertEquals(TestData.MODEL_2, modelRepository.get(TestData.MODEL_2.getIdentifier()));
            assertEquals(TestData.MODEL_EMPTY, modelRepository.get(TestData.MODEL_EMPTY.getIdentifier()));
            assertEquals(TestData.MODEL_EMPTY_SCENARIO, modelRepository.get(TestData.MODEL_EMPTY_SCENARIO.getIdentifier()));
        }
    }

    @Test
    default void shouldThrowIfModelNotFound() throws IOException {
        try (DatabaseAdapter db = provideDataSource()) {
            ModelRepository modelRepository = db.getModelRepository();
            assertThrows(NoSuchElementException.class, () -> modelRepository.get(new ModelIdentifier("")));
        }
    }

    @Test
    default void getAllShouldStreamAllModels() throws IOException {
        try (DatabaseAdapter db = provideDataSource()) {
            ModelRepository modelRepository = db.getModelRepository();
            modelRepository.write(TestData.MODEL_1);
            modelRepository.write(TestData.MODEL_2);
            modelRepository.write(TestData.MODEL_EMPTY);
            modelRepository.write(TestData.MODEL_EMPTY_SCENARIO);
            try (Stream<TestModel> stream = modelRepository.getAll()) {
                List<TestModel> read = stream.collect(Collectors.toList());
                assertEquals(4, read.size());
                assertTrue(read.contains(TestData.MODEL_1));
                assertTrue(read.contains(TestData.MODEL_2));
                assertTrue(read.contains(TestData.MODEL_EMPTY));
                assertTrue(read.contains(TestData.MODEL_EMPTY_SCENARIO));
            }
        }
    }

    @Test
    default void infoShouldCountModelsAndScenarios() throws IOException {
        try (DatabaseAdapter db = provideDataSource()) {
            ModelRepository modelRepository = db.getModelRepository();
            ModelInfo info = modelRepository.info();
            assertEquals(0, info.getNumberOfModels());
            assertEquals(0, info.getNumberOfScenarios());
            modelRepository.write(TestData.MODEL_1);
            modelRepository.write(TestData.MODEL_2);
            modelRepository.write(TestData.MODEL_EMPTY);
            modelRepository.write(TestData.MODEL_EMPTY_SCENARIO);
            info = modelRepository.info();
            assertEquals(4, info.getNumberOfModels());
            assertEquals(5, info.getNumberOfScenarios());
        }
    }
}
