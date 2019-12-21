package de.rwth.swc.coffee4j.evaluation.model;

import com.squareup.moshi.*;
import okio.BufferedSink;
import okio.BufferedSource;

import java.io.IOException;

/**
 * {@link ModelImporter} and {@link ModelExporter} implementing transformation into a Json format.
 * <p>
 * This provides an almost 1:1 mapping from internal to external representation. This makes it relatively fast to im-
 * and export, but may increase the size of the external representation in comparison to more compressed models.
 */
public class JsonModelAdapter implements ModelImporter, ModelExporter {

    private final JsonAdapter<TestModel> adapter = new Moshi.Builder()
            .add(new ModelIdentifierAdapter())
            .build()
            .adapter(TestModel.class)
            .indent("  ")
            .nonNull();

    @Override
    public TestModel importModel(BufferedSource source) throws InvalidModelException, IOException {
        try {
            return adapter.fromJson(source);
        } catch (JsonDataException e) {
            throw new InvalidModelException(e);
        }
    }

    @Override
    public void export(BufferedSink sink, TestModel model) throws IOException {
        adapter.toJson(sink, model);
    }

    private static class ModelIdentifierAdapter {
        @ToJson
        String toJson(ModelIdentifier identifier) {
            return identifier.getModelName();
        }

        @FromJson
        ModelIdentifier fromJson(String identifier) {
            return new ModelIdentifier(identifier);
        }
    }
}
