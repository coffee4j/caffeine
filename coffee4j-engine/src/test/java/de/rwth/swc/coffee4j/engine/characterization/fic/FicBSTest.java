package de.rwth.swc.coffee4j.engine.characterization.fic;

import de.rwth.swc.coffee4j.engine.characterization.FaultCharacterizationAlgorithm;
import de.rwth.swc.coffee4j.engine.characterization.FaultCharacterizationAlgorithmTest;
import de.rwth.swc.coffee4j.engine.characterization.FaultCharacterizationConfiguration;

class FicBSTest implements FaultCharacterizationAlgorithmTest {

    @Override
    public FaultCharacterizationAlgorithm provideAlgorithm(FaultCharacterizationConfiguration configuration) {
        return new FicBS(configuration);
    }
}