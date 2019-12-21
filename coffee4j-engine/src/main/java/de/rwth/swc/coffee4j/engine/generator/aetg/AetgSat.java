package de.rwth.swc.coffee4j.engine.generator.aetg;

import de.rwth.swc.coffee4j.engine.TestModel;
import de.rwth.swc.coffee4j.engine.characterization.FaultCharacterizationConfiguration;
import de.rwth.swc.coffee4j.engine.generator.TestInputGroup;
import de.rwth.swc.coffee4j.engine.generator.TestInputGroupGenerator;
import de.rwth.swc.coffee4j.engine.report.Reporter;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

/**
 * Generator for one test group containing the test inputs generated with the {@link AetgSatAlgorithm} algorithm with
 * the strength given by the {@link TestModel}.
 */
public class AetgSat implements TestInputGroupGenerator {

    private static final String DISPLAY_NAME = "Positive AetgSatAlgorithm Tests";

    @Override
    public Set<Supplier<TestInputGroup>> generate(TestModel model, Reporter reporter) {
        if (model.getStrength() == 0) {
            return Collections.emptySet();
        }

        return Collections.singleton(() -> {
            final List<int[]> testInputs = new AetgSatAlgorithm(AetgSatConfiguration.aetgSatConfiguration().model(model).build()).generate();
            final FaultCharacterizationConfiguration faultCharacterizationConfiguration = new FaultCharacterizationConfiguration(model, reporter);
            return new TestInputGroup(DISPLAY_NAME, testInputs, faultCharacterizationConfiguration);
        });
    }
}
