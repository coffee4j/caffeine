package de.rwth.swc.coffee4j.evaluation.persistence.model;

import de.rwth.swc.coffee4j.evaluation.TestData;
import de.rwth.swc.coffee4j.evaluation.model.InvalidModelException;
import de.rwth.swc.coffee4j.evaluation.model.JsonModelAdapter;
import okio.Buffer;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JsonModelAdapterTest {

    private static final String MODEL_TEMPLATE =
            "{" +
                    "  \"identifier\": %s," +
                    "  \"parameters\": %s," +
                    "  \"constraints\": {}," +
                    "  \"scenarios\": {}" +
                    "}";

    @Test
    void shouldReimportExportedModels() throws InvalidModelException, IOException {
        Buffer model1 = new Buffer();
        Buffer model2 = new Buffer();
        Buffer model3 = new Buffer();
        Buffer model4 = new Buffer();
        JsonModelAdapter adapter = new JsonModelAdapter();
        adapter.export(model1, TestData.MODEL_1);
        adapter.export(model2, TestData.MODEL_2);
        adapter.export(model3, TestData.MODEL_EMPTY);
        adapter.export(model4, TestData.MODEL_EMPTY_SCENARIO);
        assertEquals(TestData.MODEL_1, adapter.importModel(model1));
        assertEquals(TestData.MODEL_2, adapter.importModel(model2));
        assertEquals(TestData.MODEL_EMPTY, adapter.importModel(model3));
        assertEquals(TestData.MODEL_EMPTY_SCENARIO, adapter.importModel(model4));
    }

    @Test
    void shouldThrowForInvalidModel() {
        JsonModelAdapter adapter = new JsonModelAdapter();
        Buffer buffer = new Buffer();
        buffer.writeUtf8(String.format(MODEL_TEMPLATE, "\"Invalid\"", "2"));
        assertThrows(InvalidModelException.class, () -> adapter.importModel(buffer));
    }

}