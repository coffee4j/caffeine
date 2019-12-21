package de.rwth.swc.coffee4j.evaluation.persistence.trace;

/**
 * Class that contains some information about stored traces.
 * <p>
 * Currently that only includes the number of them. This class is only meant for bundling the results, the computation
 * happens elsewhere.
 */
public final class TraceInfo {

    private final int numberOfTraces;

    /**
     * Constructor.
     *
     * @param numberOfTraces the number of traces currently stored
     */
    public TraceInfo(int numberOfTraces) {
        this.numberOfTraces = numberOfTraces;
    }

    /**
     * Gets the number of traces stored.
     *
     * @return the number of traces
     */
    public int getNumberOfTraces() {
        return numberOfTraces;
    }
}
