package de.rwth.swc.coffee4j.evaluation.command.options;

import picocli.CommandLine;

/**
 * {@link picocli.CommandLine.IVersionProvider} for all commands.
 */
public final class VersionProvider implements CommandLine.IVersionProvider {

    private static final String VERSION = "1.0.0-SNAPSHOT";

    @Override
    public String[] getVersion() {
        return new String[]{VERSION};
    }
}
