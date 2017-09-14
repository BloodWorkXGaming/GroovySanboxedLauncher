package de.bloodworkxgaming.groovysandboxedlauncher.sandbox;

import de.bloodworkxgaming.groovysandboxedlauncher.compilercustomizer.ClassFunctionWhitelistAnnotatorTransformer;
import de.bloodworkxgaming.groovysandboxedlauncher.data.GSLBaseScript;
import de.bloodworkxgaming.groovysandboxedlauncher.data.GSLScriptFile;
import de.bloodworkxgaming.groovysandboxedlauncher.data.ScriptPathConfig;
import de.bloodworkxgaming.groovysandboxedlauncher.preprocessor.PreprocessorManager;
import groovy.lang.Binding;
import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyRuntimeException;
import groovy.lang.Script;
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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GroovySandboxedLauncher {
    /**
     * Setting this option to true enables additional debug info
     */
    public static boolean DEBUG = false;

    public WhitelistRegistry whitelistRegistry = new WhitelistRegistry();
    public ScriptPathConfig scriptPathConfig = new ScriptPathConfig();
    public ImportModifier importModifier = new ImportModifier();
    public LaunchWrapper launchWrapper = new LaunchWrapper();

    private FunctionKnower functionKnower = new FunctionKnower();
    private PreprocessorManager preprocessorManager = new PreprocessorManager();
    private List<GSLScriptFile> gslScriptFiles = new ArrayList<>();
    private GroovyScriptEngine scriptEngine;
    private Binding binding = new Binding();
    private GroovyClassLoader classLoader = new GroovyClassLoader();

    private List<CompilationCustomizer> compilationCustomizers = new ArrayList<>();

    public void initSandbox() {
        if (!whitelistRegistry.isInvertedMethodWhitelist()) whitelistRegistry.registerMethod(Checker.class, "*");

        compilationCustomizers.add(new SandboxTransformer());
        compilationCustomizers.add(importModifier.getImportCustomizer());
        compilationCustomizers.add(new ClassFunctionWhitelistAnnotatorTransformer());

        PreprocessorManager.registerOwnPreprocessors(preprocessorManager);


        CompilerConfiguration conf = new CompilerConfiguration();
        conf.addCompilationCustomizers(compilationCustomizers.toArray(new CompilationCustomizer[compilationCustomizers.size()]));
        conf.setScriptBaseClass(GSLBaseScript.class.getName());

        try {
            scriptEngine = new GroovyScriptEngine(scriptPathConfig.getScriptPathRootStrings(), classLoader);
            scriptEngine.setConfig(conf);

            new CustomValueFilter(whitelistRegistry, functionKnower).register();

            launchWrapper.init();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Loads all scripts in the given paths
     */
    public void loadScripts() {
        collectScripts();
        preprocessorManager.checkFilesForPreprocessors(gslScriptFiles);

        gslScriptFiles.sort(PreprocessorManager.SCRIPT_FILE_COMPARATOR);

        for (GSLScriptFile gslScriptFile : gslScriptFiles) {
            if (gslScriptFile.isValidGroovyFile()) {
                if (gslScriptFile.isScriptCreationBlocked()) {
                    System.out.println("Skipping script " + gslScriptFile + " due to preprocessors");
                    continue;
                }
                System.out.println("Handling groovy script " + gslScriptFile);

                try {
                    Script script = scriptEngine.createScript(gslScriptFile.getName(), binding);
                    gslScriptFile.setScript(script);

                } catch (ResourceException e) {
                    System.out.println("Error while reading the file");
                    e.printStackTrace();
                } catch (ScriptException e) {
                    e.printStackTrace();
                } catch (GroovyBugError e) {
                    System.out.println(gslScriptFile.getName() + " couldn't get compiled because some function is not allowed: " + e.getMessage());
                    e.printStackTrace();
                } catch (GroovyRuntimeException e) {
                    if (e.getMessage().contains("Reason: java.lang.InstantiationException")) {
                        System.out.println("Error while loading script [" + gslScriptFile.getName() + "]: ScriptFile that is only a class also needs a default constructor");
                    }
                    e.printStackTrace();
                } catch (Exception e) {
                    System.out.println("Error while loading script [" + gslScriptFile.getName() + "]");
                    e.printStackTrace();
                }
            }
        }

        functionKnower.extractMethods(gslScriptFiles);
    }

    // Don't use this unless there is a specific reason you need all of them
    // Will cause the scriptengine to crash as it doesn't know about the subfolders
    @Deprecated
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
                        gslScriptFiles.add(new GSLScriptFile(file, file.getName()));
                    }
                }
            }
        }
    }

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

    //TODO: add event to each stage
    public void resetAllToDefault() {
        // OredictUtils.resetToDefault();
        // Pony.plugins.forEach(IPonyPlugin::resetToDefault);
    }

    /**
     * Runs all scripts
     * here the lowest scripts stuff is getting run
     */
    public void runAllScripts() {
        resetAllToDefault();

        for (GSLScriptFile gslScriptFile : functionKnower.getImplementingScripts("main", 1)) {
            if (gslScriptFile.isExecutionBlocked()) continue;
            try {
                System.out.println("launching " + gslScriptFile);
                LaunchWrapper.run(gslScriptFile.getScript());

            } catch (Exception e) {
                System.out.println(gslScriptFile.toString() + " couldn't run lowest Script in File: " + e.getMessage());
                e.printStackTrace();
            }
        }


    }

    public void runFunctionAll(String name, Object... args) {

        for (GSLScriptFile script : functionKnower.getImplementingScripts(name, args.length)) {
            if (script.isExecutionBlocked()) continue;

            try {
                LaunchWrapper.invokeMethod(script.getScript(), name, args);

            } catch (GroovyBugError e) {
                System.out.println(script.toString() + " couldn't run function [" + name + "]: \n" + e.getMessage());
            } catch (Exception e) {
                System.out.println(script.toString() + " couldn't run function [" + name + "]: \n" + e.getMessage());
                e.printStackTrace();
            }
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