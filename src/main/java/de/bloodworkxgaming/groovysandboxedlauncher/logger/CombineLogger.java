package de.bloodworkxgaming.groovysandboxedlauncher.logger;

import java.util.List;

public class CombineLogger implements ILogger {
    private List<ILogger> loggers;

    public CombineLogger(LogManager logManager) {
        this.loggers = logManager.getLoggers();
    }

    @Override
    public void logCommand(String message) {
        loggers.forEach(logger -> logger.logCommand(message));
    }

    @Override
    public void logInfo(String message) {
        loggers.forEach(logger -> logger.logInfo(message));
    }

    @Override
    public void logScript(String className, String message) {
        loggers.forEach(logger -> logger.logScript(className, message));

    }
    @Override
    public void logWarning(String message) {
        loggers.forEach(logger -> logger.logWarning(message));
    }

    @Override
    public void logError(String message) {
        loggers.forEach(logger -> logger.logError(message));
    }

    @Override
    public void logError(String message, Throwable exception) {
        loggers.forEach(logger -> logger.logError(message, exception));
    }
}
