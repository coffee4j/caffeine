package de.rwth.swc.coffee4j.evaluation.model.ctw;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class CustomErrorListener extends BaseErrorListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomErrorListener.class);
    private boolean hasError = false;

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line,
                            int charPositionInLine, String msg, RecognitionException e) {
        hasError = true;
        LOGGER.error("[Line {}; Column {}] {}.", line, charPositionInLine, msg);
    }

    boolean hasError() {
        return hasError;
    }
}
