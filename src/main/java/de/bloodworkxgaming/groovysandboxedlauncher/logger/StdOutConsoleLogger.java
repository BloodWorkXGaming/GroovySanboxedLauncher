package de.bloodworkxgaming.groovysandboxedlauncher.logger;

public class StdOutConsoleLogger implements ILogger{

    @Override
    public void logCommand(String message) {
        System.out.println(message);
    }

    @Override
    public void logInfo(String message) {
        System.out.println("[INFO] " + message);
    }

    @Override
    public void logScript(String className, String message) {
        System.out.println("[SCRIPT][" + className + "] " + message);
    }

    @Override
    public void logWarning(String message) {
        System.out.println("[WARN] " + message);
    }

    @Override
    public void logError(String message) {
        logError(message, null);
    }

    @Override
    public void logError(String message, Throwable exception) {
        if (exception == null){
            System.out.println("[ERROR] " + message);
        }else {
            System.out.println("[ERROR] " + message);
            exception.printStackTrace();
        }
    }
}
