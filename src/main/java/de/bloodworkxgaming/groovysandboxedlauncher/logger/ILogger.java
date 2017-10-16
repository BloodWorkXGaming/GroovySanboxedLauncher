package de.bloodworkxgaming.groovysandboxedlauncher.logger;

import de.bloodworkxgaming.groovysandboxedlauncher.annotations.GSLWhitelistMember;

public interface ILogger {

    @GSLWhitelistMember
    void logCommand(String message);

    @GSLWhitelistMember
    void logInfo(String message);

    @GSLWhitelistMember
    default void logInfo(String className, Object message){
        logScript(className, String.valueOf(message));
    }

    @GSLWhitelistMember
    void logScript(String className, String message);

    @GSLWhitelistMember
    default void logScript(Object message){
        logInfo(String.valueOf(message));
    }

    @GSLWhitelistMember
    void logWarning(String message);

    @GSLWhitelistMember
    void logError(String message);

    @GSLWhitelistMember
    void logError(String message, Throwable exception);
}
