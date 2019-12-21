package de.rwth.swc.coffee4j.evaluation.adapter.command;

import picocli.CommandLine;

/**
 * {@link CommandLine.IVersionProvider} for all commands.
 */
final class VersionProvider implements CommandLine.IVersionProvider {

    private static final String VERSION = "1.0.6-SNAPSHOT";

    @Override
    public String[] getVersion() {
        return new String[]{VERSION};
    }
}
