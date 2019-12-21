package de.rwth.swc.coffee4j.engine.characterization.ict;

import de.rwth.swc.coffee4j.engine.characterization.FaultCharacterizationAlgorithm;
import de.rwth.swc.coffee4j.engine.characterization.FaultCharacterizationAlgorithmTest;
import de.rwth.swc.coffee4j.engine.characterization.FaultCharacterizationConfiguration;

class IctTest implements FaultCharacterizationAlgorithmTest {

    @Override
    public FaultCharacterizationAlgorithm provideAlgorithm(FaultCharacterizationConfiguration configuration) {
        return new Ict(configuration);
    }
}