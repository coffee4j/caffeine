package de.rwth.swc.coffee4j.evaluation.adapter;

import java.io.*;
import java.util.Objects;
import java.util.Scanner;

/**
 * Adapter class that wraps one input and one output.
 * <p>
 * It is meant to abstract away the underlying communication mechanism for the coffee4j adapter.
 * This enables easier testing and a cleaner interface.
 */
public class IOAdapter implements Closeable {
    private final Scanner input;
    private final BufferedWriter output;

    /**
     * Constructor.
     *
     * @param input the input stream. May not be {@code null}.
     * @param output the output stream. May not be {@code null}.
     */
    public IOAdapter(InputStream input, OutputStream output) {
        this.input = new Scanner(Objects.requireNonNull(input));
        this.output = new BufferedWriter(new OutputStreamWriter(Objects.requireNonNull(output)));
    }

    /**
     * Write a line to the output.
     *
     * This automatically appends a newline and flushes the stream if necessary.
     *
     * @param line the line to write. It should not contain a closing line break.
     * @throws IOException If writing to the underlying stream fails.
     */
    public void write(String line) throws IOException {
        output.write(line);
        output.newLine();
        output.flush();
    }


    /**
     * Reads the next line from the input stream.
     *
     * This call is blocking and will wait until a complete line has been written or the stream closes.
     *
     * @return the read line
     */
    public String nextLine() {
        return input.nextLine();
    }

    /**
     * Checks whether the input stream contains another line.
     * <p>
     * This call is blocking and will wait until a complete line has been written, EOF has been reached, or the stream closes.
     *
     * @return whether the input stream contains another line
     */
    boolean hasNextLine() {
        return input.hasNextLine();
    }

    @Override
    public void close() throws IOException {
        IOException thrownException = null;
        IOException scannerLastException = input.ioException();
        input.close();
        if (input.ioException() != scannerLastException) {
            thrownException = input.ioException();
        }
        output.close();
        if (thrownException != null) {
            throw thrownException;
        }
    }

}
