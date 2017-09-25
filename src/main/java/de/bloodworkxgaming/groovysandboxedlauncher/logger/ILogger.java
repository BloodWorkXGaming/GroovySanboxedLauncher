package de.bloodworkxgaming.groovysandboxedlauncher.logger;

public interface ILogger {

    void logCommand(String message);

    void logInfo(String message);

    default void logInfo(String className, Object message){
        logScript(className, String.valueOf(message));
    }

    void logScript(String className, String message);

    default void logScript(Object message){
        logInfo(String.valueOf(message));
    }

    void logWarning(String message);

    void logError(String message);

    void logError(String message, Throwable exception);
}
