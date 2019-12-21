package de.rwth.swc.coffee4j.evaluation.model.ctw;

import de.rwth.swc.coffee4j.evaluation.model.InvalidModelException;
import de.rwth.swc.coffee4j.evaluation.model.ModelImporter;
import de.rwth.swc.coffee4j.evaluation.model.TestModel;
import okio.Buffer;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

class CtwModelImporterTest {

    private static final String MODEL = "\tModel Aircraft\n" +
            "\t\n" +
            "\tParameters:\n" +
            "Enumerative p1 { v1 v2 v3 v4 };" +
            "\tHigh: Boolean;\n" +
            "\tShoulder: Boolean;\n" +
            "\tEngine: { Jet Piston NONE };\n" +
            "\tBoolean Metal;\n" +
            "\tWood: Boolean;\n" +
            "\tCloth: [-3 .. 10];" +
            "Constraints: " +
            "# Wood!=false AND Engine == Engine.Jet #" +
            "Scenarios: # 2 (C0)#";

    @Test
    void shouldParseCorrectModel() throws InvalidModelException, IOException {
        ModelImporter importer = new CtwModelImporter();
        Buffer buffer = new Buffer();
        buffer.writeUtf8(MODEL);
        TestModel model = importer.importModel(buffer);
        assertArrayEquals(new int[]{4, 2, 2, 3, 2, 2, 14}, model.getParameters());

    }

}