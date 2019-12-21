package de.rwth.swc.coffee4j.evaluation.model;

import okio.BufferedSource;

import java.io.IOException;

/**
 * Interface for classes that transform a {@link TestModel} from some external representation to the internal one.
 */
public interface ModelImporter {

    /**
     * Import a model from a data source.
     * <p>
     * The data source should not be closed by the importer, that is handled externally.
     *
     * @param source the data source
     * @return the imported model
     * @throws InvalidModelException if the model representation is not valid
     * @throws IOException           if reading from the source fails
     */
    TestModel importModel(BufferedSource source) throws InvalidModelException, IOException;

}
