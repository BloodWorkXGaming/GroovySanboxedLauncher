package de.bloodworkxgaming.groovysandboxedlauncher.sandbox;

import de.bloodworkxgaming.groovysandboxedlauncher.compilercustomizer.ClassFunctionWhitelistAnnotatorTransformer;
import de.bloodworkxgaming.groovysandboxedlauncher.data.ScriptPathConfig;
import de.bloodworkxgaming.groovysandboxedlauncher.utils.FileUtils;
import groovy.lang.Binding;
import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyRuntimeException;
import groovy.lang.Script;
import groovy.util.GroovyScriptEngine;
import groovy.util.ResourceException;
import groovy.util.ScriptException;
import org.codehaus.groovy.GroovyBugError;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.MultipleCompilationErrorsException;
import org.codehaus.groovy.control.customizers.CompilationCustomizer;
import org.kohsuke.groovy.sandbox.SandboxTransformer;
import org.kohsuke.groovy.sandbox.impl.Checker;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GroovySandboxedLauncher {
    /**
     * Setting this option to true enables additional debug info
     */
    public static boolean DEBUG = true;

    public WhitelistRegistry whitelistRegistry = new WhitelistRegistry();
    public ScriptPathConfig scriptPathConfig = new ScriptPathConfig();
    public ImportModifier importModifier = new ImportModifier();
    public LaunchWrapper launchWrapper = new LaunchWrapper();

    private FunctionKnower functionKnower = new FunctionKnower();
    private ArrayList<Script> scripts = new ArrayList<>();
    private GroovyScriptEngine scriptEngine;
    private Binding binding = new Binding();
    private GroovyClassLoader classLoader = new GroovyClassLoader();

    private List<CompilationCustomizer> compilationCustomizers = new ArrayList<>();

    public void initSandbox() {
        if (!whitelistRegistry.isInvertedMethodWhitelist()) whitelistRegistry.registerMethod(Checker.class, "*");

        compilationCustomizers.add(new SandboxTransformer());
        compilationCustomizers.add(importModifier.getImportCustomizer());
        compilationCustomizers.add(new ClassFunctionWhitelistAnnotatorTransformer());


        try {
            scriptEngine = new GroovyScriptEngine(scriptPathConfig.getScriptPathRootStrings(), classLoader);
            scriptEngine.setConfig(new CompilerConfiguration().addCompilationCustomizers(compilationCustomizers.toArray(new CompilationCustomizer[compilationCustomizers.size()])));

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
        scripts.clear();

        try {

            for (File path : scriptPathConfig.getScriptPathRoots()) {
                System.out.println("Searching for scripts in: " + path.getAbsolutePath());

                // To prevent empty folders from crashing
                File[] fileList = path.listFiles();
                if (fileList != null) {
                    for (File f : fileList) {
                        System.out.println("Found file: " + f.getName());

                        if (FileUtils.isExtension(f, "groovy")) {
                            System.out.println("matching groovy for file " + f.getName());
                            try {
                                scripts.add(scriptEngine.createScript(f.getName(), binding));
                            } catch (ResourceException e) {
                                System.out.println("Error while reading the file");
                                e.printStackTrace();
                            } catch (ScriptException e) {
                                e.printStackTrace();
                            } catch (GroovyBugError e) {
                                System.out.println(f.getName() + " couldn't get compiled because some function is not allowed: " + e.getMessage());
                                e.printStackTrace();
                            } catch (GroovyRuntimeException e) {
                                if (e.getMessage().contains("Reason: java.lang.InstantiationException")) {
                                    System.out.println("Error while loading script [" + f.getName() + "]: ScriptFile that is only a class also needs a default constructor");
                                }
                                e.printStackTrace();
                            } catch (Exception e) {
                                System.out.println("Error while loading script [" + f.getName() + "]");
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error while loading scripts");
            e.printStackTrace();
        }

        functionKnower.extractMethods(scripts);
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

        for (Script script : functionKnower.getImplementingScripts("main", 1)) {

            try {
                LaunchWrapper.run(script);

            } catch (MultipleCompilationErrorsException e) {
                System.out.println("Successful sandboxing: " + e.getMessage());
            } catch (GroovyBugError e) {
                System.out.println("The function the probably not allowed!" + e.getMessage());
            } catch (Exception e) {
                System.out.println("<" + script.toString() + "> couldn't run lowest script in File due to " + e.getMessage());
                e.printStackTrace();
            }
        }


    }

    public void runFunctionAll(String name, Object... args) {

        for (Script script : functionKnower.getImplementingScripts(name, args.length)) {
            try {
                LaunchWrapper.invokeMethod(script, name, args);

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
}