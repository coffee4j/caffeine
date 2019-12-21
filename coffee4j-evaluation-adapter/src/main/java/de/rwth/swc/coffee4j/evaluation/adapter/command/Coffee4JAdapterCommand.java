package de.rwth.swc.coffee4j.evaluation.adapter.command;

import de.rwth.swc.coffee4j.evaluation.adapter.*;
import picocli.CommandLine;

import java.io.IOException;
import java.util.InputMismatchException;
import java.util.NoSuchElementException;
import java.util.concurrent.Callable;

/**
 * Command which initializes and executes a fault characterization algorithm that communicates over the command line.
 * <p>
 * Two command line options are provided: -a for the fault characterization algorithm, and -g for the covering array
 * generation algorithm. Usually both fields are required. In the case of an interleaved fault characterization algorithm
 * the covering array generation option may be left unset.
 */
@CommandLine.Command(
        description = "Executes combinatorial testing and fault characterization algorithm. Initialization" +
                " and communication with a test oracle happens over the command line." +
                " See coffee4j-evaluation for more information.",
        mixinStandardHelpOptions = true,
        versionProvider = VersionProvider.class
)
class Coffee4JAdapterCommand implements Callable<Integer> {

    private final static int ERROR_RESPONSE_CODE = 1;

    @CommandLine.Option(names = {"-a", "--algorithm"},
            description = "The fault characterization algorithm that should be used.%nAlgorithms: ${COMPLETION-CANDIDATES}",
            required = true)
    private FaultCharacterizationAlgorithmOption faultCharacterizationAlgorithm;

    @CommandLine.Option(names = {"-g", "--generation"},
            description = "The covering array generation algorithm that should be used. This may be left empty for" +
                    " interleaved fault characterization, otherwise it is required.%nAlgorithms: ${COMPLETION-CANDIDATES}")
    private GenerationAlgorithmOption generationAlgorithm;


    @Override
    public Integer call() {

        FaultCharacterizationAlgorithmProvider provider = faultCharacterizationAlgorithm.getProvider();

        if (provider.getType() != FaultCharacterizationAlgorithmProvider.Type.INTERLEAVED
                && generationAlgorithm == null) {
            System.err.println("Static and sequential fault characterization algorithms need to specify a generation algorithm.");
            return ERROR_RESPONSE_CODE;
        }

        try (IOAdapter io = new IOAdapter(System.in, System.out)) {
            return new Coffee4JAdapter(generationAlgorithm != null ? generationAlgorithm.get() : null,
                    provider, io).call();
        } catch (IOException | InputMismatchException e) {
            System.err.println(e.getMessage());
            return ERROR_RESPONSE_CODE;
        } catch (NoSuchElementException e) {
            // If the process is closed, because of a timeout, this exception may be triggered
            return ERROR_RESPONSE_CODE;
        }
    }

}
