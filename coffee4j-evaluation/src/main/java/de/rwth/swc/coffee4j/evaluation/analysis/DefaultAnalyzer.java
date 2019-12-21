package de.rwth.swc.coffee4j.evaluation.analysis;

import de.rwth.swc.coffee4j.evaluation.analysis.model.DefaultModelAnalyzer;
import de.rwth.swc.coffee4j.evaluation.analysis.scenario.DefaultScenarioAnalyzer;
import de.rwth.swc.coffee4j.evaluation.analysis.trace.DefaultTraceAnalyzer;

/**
 * Default implementation for {@link Analyzer} that uses the {@link DefaultModelAnalyzer}, {@link
 * DefaultScenarioAnalyzer}, and {@link DefaultTraceAnalyzer}.
 */
public class DefaultAnalyzer extends Analyzer {

    /**
     * Constructor.
     */
    public DefaultAnalyzer() {
        super(new DefaultModelAnalyzer(),
                new DefaultScenarioAnalyzer(),
                new DefaultTraceAnalyzer());
    }

}
