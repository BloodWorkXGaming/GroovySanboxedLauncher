package de.bloodworkxgaming.groovysandboxedlauncher.sandbox;

import de.bloodworkxgaming.groovysandboxedlauncher.compilercustomizer.ClassFunctionWhitelistAnnotatorTransformer;
import de.bloodworkxgaming.groovysandboxedlauncher.data.GSLBaseScript;
import de.bloodworkxgaming.groovysandboxedlauncher.data.GSLScriptFile;
import de.bloodworkxgaming.groovysandboxedlauncher.data.ScriptPathConfig;
import de.bloodworkxgaming.groovysandboxedlauncher.events.EventList;
import de.bloodworkxgaming.groovysandboxedlauncher.events.GSLResetEvent;
import de.bloodworkxgaming.groovysandboxedlauncher.events.IGSLEvent;
import de.bloodworkxgaming.groovysandboxedlauncher.logger.CombineLogger;
import de.bloodworkxgaming.groovysandboxedlauncher.logger.ILogger;
import de.bloodworkxgaming.groovysandboxedlauncher.logger.LogManager;
import de.bloodworkxgaming.groovysandboxedlauncher.preprocessor.PreprocessorManager;
import groovy.lang.*;
import groovy.util.GroovyScriptEngine;
import groovy.util.ResourceException;
import groovy.util.ScriptException;
import org.codehaus.groovy.GroovyBugError;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.CompilationCustomizer;
import org.kohsuke.groovy.sandbox.SandboxTransformer;
import org.kohsuke.groovy.sandbox.impl.Checker;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

public class GroovySandboxedLauncher {
    /**
     * Setting this option to true enables additional debug info
     */
    public static boolean DEBUG = false;
    public static final LogManager LOG_MANAGER = new LogManager();
    public static final ILogger LOGGER = new CombineLogger(LOG_MANAGER);
    public static final DecimalFormat DF = new DecimalFormat("#.##");
    static {
        LOG_MANAGER.registerLogger(LogManager.logger);
    }

    public WhitelistRegistry whitelistRegistry = new WhitelistRegistry();
    public ScriptPathConfig scriptPathConfig = new ScriptPathConfig();
    public ImportModifier importModifier = new ImportModifier();
    public LaunchWrapper launchWrapper = new LaunchWrapper();

    private FunctionKnower functionKnower = new FunctionKnower();
    private PreprocessorManager preprocessorManager = new PreprocessorManager();
    private List<GSLScriptFile> gslScriptFiles = new ArrayList<>();
    private GroovyScriptEngine scriptEngine;
    private Binding binding = new Binding();
    private GroovyClassLoader classLoader = null;

    private List<CompilationCustomizer> compilationCustomizers = new ArrayList<>();
    private EventList<GSLResetEvent> resetEventEventList = new EventList<>();

    private CustomValueFilter customValueFilter = new CustomValueFilter(whitelistRegistry, functionKnower);


    public void initSandbox() {
        if (!whitelistRegistry.isInvertedMethodWhitelist()) whitelistRegistry.registerMethod(Checker.class, "*");

        compilationCustomizers.add(new SandboxTransformer());
        compilationCustomizers.add(importModifier.getImportCustomizer());
        compilationCustomizers.add(new ClassFunctionWhitelistAnnotatorTransformer(this));

        PreprocessorManager.registerOwnPreprocessors(preprocessorManager);

        CompilerConfiguration conf = new CompilerConfiguration();
        conf.addCompilationCustomizers(compilationCustomizers.toArray(new CompilationCustomizer[compilationCustomizers.size()]));
        conf.setScriptBaseClass(GSLBaseScript.class.getName());

        if (classLoader == null){
            CompilerConfiguration loaderConf = new CompilerConfiguration();
            loaderConf.setScriptBaseClass(GSLBaseScript.class.getName());
            classLoader = new GroovyClassLoader(Thread.currentThread().getContextClassLoader(), loaderConf);
        }

        try {
            scriptEngine = new GroovyScriptEngine(scriptPathConfig.getScriptPathRootStrings(), classLoader);

            scriptEngine.setConfig(conf);
            customValueFilter.register();

            launchWrapper.init();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Loads all scripts in the given paths
     */
    public void loadScripts() {
        long timeStart = System.nanoTime();
        resetAllToDefault();
        collectScriptsRecoursive();
        preprocessorManager.checkFilesForPreprocessors(gslScriptFiles);

        gslScriptFiles.sort(PreprocessorManager.SCRIPT_FILE_COMPARATOR);

        List<GSLScriptFile> rejectedFiles = new ArrayList<>();

        // Loading the scripts normally
        for (GSLScriptFile gslScriptFile : gslScriptFiles) {
            if (gslScriptFile.isValidGroovyFile()) {
                if (gslScriptFile.isScriptCreationBlocked()) {
                    GroovySandboxedLauncher.LOGGER.logInfo("Skipping script " + gslScriptFile + " due to preprocessors");
                    continue;
                }
                GroovySandboxedLauncher.LOGGER.logInfo("Loading groovy script " + gslScriptFile);

                try {
                    try {
                        Script script = scriptEngine.createScript(gslScriptFile.getName(), binding);
                        gslScriptFile.setScript(script);

                    } catch (Exception e){
                        LOGGER.logWarning("Script " + gslScriptFile.toString() + " got rejected due to " + e.getMessage() + ", trying again later.");
                        rejectedFiles.add(gslScriptFile);
                        if(DEBUG) LOGGER.logError("full stack: ", e);
                    }

                } catch (GroovyBugError e) {
                    GroovySandboxedLauncher.LOGGER.logError(gslScriptFile.getName() + " couldn't get compiled because some function is not allowed: " + e.getMessage(), e);
                } catch (GroovyRuntimeException e) {
                    if (e.getMessage().contains("Reason: java.lang.InstantiationException")) {
                        GroovySandboxedLauncher.LOGGER.logError("Error while loading script [" + gslScriptFile.getName() + "]: ScriptFile that is only a class also needs a default constructor", e);
                    }
                } catch (Exception e) {
                    GroovySandboxedLauncher.LOGGER.logError("Error while loading script [" + gslScriptFile.getName() + "]", e);
                }
            }
        }

        // Trying everything again after it failed once
        for (GSLScriptFile rejectedFile : rejectedFiles) {
            try {
                GroovySandboxedLauncher.LOGGER.logInfo("Loading groovy script " + rejectedFile + " for a second attempt.");
                Script script = scriptEngine.createScript(rejectedFile.getName(), binding);
                rejectedFile.setScript(script);
            } catch (Exception e){
                LOGGER.logError("Script " + rejectedFile.toString() + " failed on the second attempt as well (try using the priority preprocessor): ", e);
            }
        }

        functionKnower.extractMethods(gslScriptFiles);
        if (DEBUG) functionKnower.dumpMap();

        LOGGER.logInfo("Finished loading scripts in " + DF.format((System.nanoTime() - timeStart)/1.0e6) + "ms.");
    }

    /**
     * Collects all the scripts in all the subfolders, may cause issues when it is not in the right order, but not sure how to sort for that
     */
    private void collectScriptsRecoursive() {
        gslScriptFiles.clear();
        for (File path : scriptPathConfig.getScriptPathRoots()) {
            List<Path> pathList = null;
            try {
                pathList = Files.find(path.toPath(), Integer.MAX_VALUE, (path1, basicFileAttributes) -> basicFileAttributes.isRegularFile()).collect(Collectors.toList());
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (pathList != null) {
                for (Path scriptPath : pathList) {
                    File file = scriptPath.toFile();
                    if (file.isFile()) {
                        String s = path.toPath().relativize(file.toPath()).toString();
                        gslScriptFiles.add(new GSLScriptFile(file, s));
                    }
                }
            }
        }
    }

    /**
     * Only loads the scripts in the flat roots, no subfolders
     */
    private void collectScripts() {
        gslScriptFiles.clear();
        for (File path : scriptPathConfig.getScriptPathRoots()) {
            if (path == null) continue;
            File[] filelist = path.listFiles();
            if (filelist == null) continue;

            for (File file : filelist) {
                if (!file.isFile()) continue;
                gslScriptFiles.add(new GSLScriptFile(file, file.getName()));
            }
        }
    }

    public void resetAllToDefault() {
        resetEventEventList.callOnEach(new GSLResetEvent());
    }

    public void registerResetEvent(IGSLEvent<GSLResetEvent> event) {
        resetEventEventList.registerEvent(event);
    }

    /**
     * Runs all scripts
     * here the lowest scripts stuff is getting run
     */
    public void runAllScripts() {
        long timeStart = System.nanoTime();
        resetAllToDefault();

        for (GSLScriptFile gslScriptFile : functionKnower.getImplementingScripts("main", 1)) {
            if (gslScriptFile.isExecutionBlocked()) continue;
            try {
                GroovySandboxedLauncher.LOGGER.logInfo("Running script " + gslScriptFile);
                LaunchWrapper.run(gslScriptFile.getScript());

            } catch (Exception e) {
                GroovySandboxedLauncher.LOGGER.logError(gslScriptFile.toString() + " couldn't run lowest Script in File: " + e.getMessage(), e);
            }
        }

        LOGGER.logInfo("Finished executing scripts in " + DF.format((System.nanoTime() - timeStart)/1.0e6) + "ms.");
    }

    public void runFunctionAll(String name, Object... args) {
        for (GSLScriptFile script : functionKnower.getImplementingScripts(name, args.length)) {
            if (script.isExecutionBlocked()) continue;

            try {
                LaunchWrapper.invokeMethod(script.getScript(), name, args);

            } catch (Exception e) {
                GroovySandboxedLauncher.LOGGER.logError(script.toString() + " couldn't run function [" + name + "]: \n" + e.getMessage(), e);
            }
        }

    }

    public void runClosure(Closure closure, Object... args){
        try {
            Checker.checkedCall(closure, false, false, "call", args);
        } catch (Throwable throwable) {
            GroovySandboxedLauncher.LOGGER.logError("There was a problem running the closure " + closure + " with the arguments " + Arrays.toString(args), throwable);
        }
    }

    /**
     * Register a Customizer that will get injected into the ScriptEngine on startup
     */
    public void registerCompilationCustomizer(CompilationCustomizer customizer) {
        compilationCustomizers.add(customizer);
    }

    public Binding getBinding() {
        return binding;
    }

    public void setBinding(Binding binding) {
        this.binding = binding;
    }

    public void setClassLoader(GroovyClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public PreprocessorManager getPreprocessorManager() {
        return preprocessorManager;
    }
}