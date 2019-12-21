package de.rwth.swc.coffee4j.evaluation.adapter;

import de.rwth.swc.coffee4j.engine.characterization.FaultCharacterizationAlgorithmFactory;
import de.rwth.swc.coffee4j.engine.characterization.NoOp;
import de.rwth.swc.coffee4j.engine.characterization.aifl.Aifl;
import de.rwth.swc.coffee4j.engine.characterization.aifl.IterationBasedIterAifl;
import de.rwth.swc.coffee4j.engine.characterization.ben.Ben;
import de.rwth.swc.coffee4j.engine.characterization.csp.Csp;
import de.rwth.swc.coffee4j.engine.characterization.delta.ImprovedDeltaDebugging;
import de.rwth.swc.coffee4j.engine.characterization.fic.Fic;
import de.rwth.swc.coffee4j.engine.characterization.fic.FicBS;
import de.rwth.swc.coffee4j.engine.characterization.ict.Ict;
import de.rwth.swc.coffee4j.engine.characterization.locating.AdaptiveLocatingArray;


/**
 * Values for the fault characterization algorithm command line options.
 */
public enum FaultCharacterizationAlgorithmOption {
    /**
     * The Aifl algorithm
     */
    Aifl("Aifl", FaultCharacterizationAlgorithmProvider.Type.ADAPTIVE, Aifl::new),
    /**
     * The IterAifl algorithm
     */
    IterAifl("IterAifl", FaultCharacterizationAlgorithmProvider.Type.ADAPTIVE, IterationBasedIterAifl::new),
    /**
     * The Ben algorithm
     */
    Ben("Ben", FaultCharacterizationAlgorithmProvider.Type.ADAPTIVE, Ben::new),
    /**
     * The Csp algorithm
     */
    Csp("Csp", FaultCharacterizationAlgorithmProvider.Type.STATIC, Csp::new),
    /**
     * The Improved Delta Debugging algorithm
     */
    IDD("IDD", FaultCharacterizationAlgorithmProvider.Type.ADAPTIVE, ImprovedDeltaDebugging::new),
    /**
     * The FIC algorithm
     */
    FIC("FIC", FaultCharacterizationAlgorithmProvider.Type.ADAPTIVE, Fic::new),
    /**
     * The FIC-BS algorithm
     */
    FIC_BS("FIC-BS", FaultCharacterizationAlgorithmProvider.Type.ADAPTIVE, FicBS::new),
    /**
     * The Ict algorithm
     */
    Ict("Ict", FaultCharacterizationAlgorithmProvider.Type.INTERLEAVED, Ict::new),
    /**
     * The Adaptive Locating Array algorithm
     */
    Ala("Ala", FaultCharacterizationAlgorithmProvider.Type.ADAPTIVE, AdaptiveLocatingArray::new),
    /**
     * The NoOp algorithm
     */
    NoOp("NoOp", FaultCharacterizationAlgorithmProvider.Type.ADAPTIVE, NoOp::new);

    private final FaultCharacterizationAlgorithmProvider provider;

    FaultCharacterizationAlgorithmOption(String displayName,
                                         FaultCharacterizationAlgorithmProvider.Type type,
                                         FaultCharacterizationAlgorithmFactory factory) {
        this.provider = new FaultCharacterizationAlgorithmProvider(displayName, type, factory);
    }

    /**
     * Gets the internal provider.
     *
     * @return The provider
     */
    public FaultCharacterizationAlgorithmProvider getProvider() {
        return provider;
    }
}
