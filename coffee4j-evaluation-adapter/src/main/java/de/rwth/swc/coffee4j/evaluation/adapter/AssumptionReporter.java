package de.rwth.swc.coffee4j.evaluation.adapter;

import de.rwth.swc.coffee4j.engine.report.Report;
import de.rwth.swc.coffee4j.engine.report.ReportLevel;
import de.rwth.swc.coffee4j.engine.report.Reporter;

import java.io.IOException;
import java.util.function.Supplier;

class AssumptionReporter implements Reporter {

    private final IOAdapter io;

    AssumptionReporter(IOAdapter io) {
        this.io = io;
    }

    @Override
    public void report(ReportLevel level, Report report) {
        // Do nothing
    }

    @Override
    public void report(ReportLevel level, Supplier<Report> reportSupplier) {
        // Do nothing
    }

    @Override
    public void reportAssumptionViolation(String assumptionKey) {
        try {
            io.write("# " + assumptionKey + " VIOLATED");
        } catch (IOException e) {
            // Do nothing
        }
    }

    @Override
    public void reportAssumptionSatisfaction(String assumptionKey) {
        try {
            io.write("# " + assumptionKey + " SATISFIED");
        } catch (IOException e) {
            // Do nothing
        }
    }
}
