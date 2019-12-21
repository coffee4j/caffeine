package de.rwth.swc.coffee4j.evaluation.model.ctw;

import de.rwth.swc.coffee4j.evaluation.model.InvalidModelException;
import de.rwth.swc.coffee4j.evaluation.model.ModelImporter;
import de.rwth.swc.coffee4j.evaluation.model.TestModel;
import de.rwth.swc.coffee4j.evaluation.persistence.antlr.CtwFileLexer;
import de.rwth.swc.coffee4j.evaluation.persistence.antlr.CtwFileParser;
import okio.BufferedSource;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ConsoleErrorListener;

import java.io.IOException;
import java.util.InputMismatchException;

/**
 * {@link ModelImporter} for files in the format ".ctw" or ".citl".
 * <p>
 * These formats are created by the tools CTWedge an its predecessor CITLab. This translation is not perfect, several
 * extensions and omissions from the original grammar have been made to support usage in the benchmarking
 * infrastructure.
 * <p>
 * The original valid constructs that are not supported:
 *
 * <ol>
 *     <li> Arithmetic expressions in constraints
 *     <li> CITLab type definitions
 * </ol>
 * <p>
 * The additionally supported constructs:
 *
 * <ol>
 *     <li> Scenario definition
 *     <li> Named constraints
 * </ol>
 */
public class CtwModelImporter implements ModelImporter {
    @Override
    public TestModel importModel(BufferedSource source) throws InvalidModelException, IOException {
        CtwFileLexer lexer = new CtwFileLexer(CharStreams.fromStream(source.inputStream()));
        CtwFileParser parser = new CtwFileParser(new CommonTokenStream(lexer));
        CustomErrorListener listener = new CustomErrorListener();
        parser.addErrorListener(listener);
        parser.removeErrorListener(ConsoleErrorListener.INSTANCE);
        CtwFileParser.ModelContext model = parser.model();
        if (listener.hasError()) {
            throw new InvalidModelException("Model has syntax errors.");
        }

        try {
            return new CtwModelTransformer(lexer.getVocabulary()).visitModel(model);
        } catch (InputMismatchException e) {
            throw new InvalidModelException(e);
        }
    }
}
