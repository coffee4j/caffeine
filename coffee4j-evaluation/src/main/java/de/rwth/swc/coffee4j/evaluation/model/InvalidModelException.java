package de.rwth.swc.coffee4j.evaluation.model;

/**
 * Exception that signifies an error in the external model representation.
 */
public class InvalidModelException extends Exception {

    /**
     * Constructor with a message.
     *
     * @param msg the message
     */
    public InvalidModelException(String msg) {
        super(msg);
    }

    /**
     * Constructor with the causing throwable.
     *
     * @param e the cause
     */
    public InvalidModelException(Throwable e) {
        super(e);
    }
}
