package de.bloodworkxgaming.groovysandboxedlauncher.logger;

public interface ILogger {

    void logCommand(String message);

    void logInfo(String message);

    default void logInfo(Object message){
        logInfo(String.valueOf(message));
    }

    void logWarning(String message);

    void logError(String message);

    void logError(String message, Throwable exception);
}
