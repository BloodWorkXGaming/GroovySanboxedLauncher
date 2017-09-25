package de.bloodworkxgaming.groovysandboxedlauncher.data;

import de.bloodworkxgaming.groovysandboxedlauncher.annotations.GSLWhitelistClass;
import de.bloodworkxgaming.groovysandboxedlauncher.annotations.GSLWhitelistConstructor;
import de.bloodworkxgaming.groovysandboxedlauncher.annotations.GSLWhitelistMember;
import de.bloodworkxgaming.groovysandboxedlauncher.sandbox.GroovySandboxedLauncher;
import groovy.lang.Script;

@GSLWhitelistConstructor
@GSLWhitelistClass
public abstract class GSLBaseScript extends Script {
    @Override
    @GSLWhitelistMember
    public void println() {
        GroovySandboxedLauncher.LOGGER.logInfo("");
    }

    @Override
    @GSLWhitelistMember
    public void print(Object value) {
        GroovySandboxedLauncher.LOGGER.logInfo(String.valueOf(value));
    }

    @Override
    @GSLWhitelistMember
    public void println(Object value) {
        GroovySandboxedLauncher.LOGGER.logInfo(String.valueOf(value));
    }
}
