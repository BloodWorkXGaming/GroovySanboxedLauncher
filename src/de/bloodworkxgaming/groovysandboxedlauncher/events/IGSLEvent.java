package de.bloodworkxgaming.groovysandboxedlauncher.events;

public interface IGSLEvent<R> {
    void handleEvent(R eventObject);
}
