package de.bloodworkxgaming.groovysandboxedlauncher.data;

import de.bloodworkxgaming.groovysandboxedlauncher.sandbox.GroovySandboxedLauncher;
import groovy.lang.Script;

public abstract class GSLBaseScript extends Script {
    @Override
    public void println() {
        GroovySandboxedLauncher.LOGGER.logInfo("");
    }

    @Override
    public void print(Object value) {
        GroovySandboxedLauncher.LOGGER.logInfo(String.valueOf(value));
    }

    @Override
    public void println(Object value) {
        GroovySandboxedLauncher.LOGGER.logInfo(String.valueOf(value));
    }
}
