package de.rwth.swc.coffee4j.evaluation.adapter;

import de.rwth.swc.coffee4j.engine.generator.TestInputGroupGenerator;
import de.rwth.swc.coffee4j.engine.generator.aetg.AetgSat;
import de.rwth.swc.coffee4j.engine.generator.ipog.Ipog;

import java.util.function.Supplier;

/**
 * Values for the generation algorithm command line options.
 */
public enum GenerationAlgorithmOption {
    /**
     * The IPOG algorithm
     */
    IPOG(Ipog::new),
    /**
     * The AETGSat algorithm
     */
    AETG(AetgSat::new);

    private final Supplier<TestInputGroupGenerator> provider;

    GenerationAlgorithmOption(Supplier<TestInputGroupGenerator> provider) {
        this.provider = provider;
    }

    /**
     * Gets the generator.
     *
     * @return the generator
     */
    public TestInputGroupGenerator get() {
        return provider.get();
    }
}
