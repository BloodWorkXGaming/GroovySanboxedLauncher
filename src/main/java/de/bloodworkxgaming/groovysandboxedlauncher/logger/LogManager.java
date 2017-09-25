package de.bloodworkxgaming.groovysandboxedlauncher.logger;

import java.util.ArrayList;
import java.util.List;

public class LogManager {
    public static final StdOutConsoleLogger logger = new StdOutConsoleLogger();

    private List<ILogger> loggers = new ArrayList<>();

    public void registerLogger(ILogger logger){
        loggers.add(logger);
    }

    public void clearLoggers(){
        loggers.clear();
    }

    public void removeLogger(ILogger logger){
        loggers.remove(logger);
    }

    public List<ILogger> getLoggers(){
        return loggers;
    }
}
