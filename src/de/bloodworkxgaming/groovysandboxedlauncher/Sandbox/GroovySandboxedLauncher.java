package de.bloodworkxgaming.groovysandboxedlauncher.Sandbox;

import de.bloodworkxgaming.groovysandboxedlauncher.data.ScriptPathConfig;
import de.bloodworkxgaming.groovysandboxedlauncher.utils.FileUtils;
import groovy.lang.Binding;
import groovy.lang.MetaMethod;
import groovy.lang.Script;
import groovy.util.GroovyScriptEngine;
import groovy.util.ResourceException;
import groovy.util.ScriptException;
import org.codehaus.groovy.GroovyBugError;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.MultipleCompilationErrorsException;
import org.kohsuke.groovy.sandbox.SandboxTransformer;
import org.kohsuke.groovy.sandbox.impl.Checker;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * Created by Jonas on 26.05.2017.
 */

public class GroovySandboxedLauncher {
    public static boolean DEBUG = false;

    private HashMap<ExtractedMethod, List<Script>> functionKnower = new HashMap<>();

    public WhitelistRegistry whitelistRegistry = new WhitelistRegistry();
    public ScriptPathConfig scriptPathConfig = new ScriptPathConfig();
    public ImportModifier importModifier = new ImportModifier();
    public LaunchWrapper launchWrapper = new LaunchWrapper();


    private ArrayList<Script> scripts = new ArrayList<>();
    private GroovyScriptEngine scriptEngine;
    private Binding binding;

    public void initSandbox() {
        if (!whitelistRegistry.isInvertedMethodWhitelist()) whitelistRegistry.registerMethod(Checker.class, "*");

        binding = new Binding();

        try {
            scriptEngine = new GroovyScriptEngine(scriptPathConfig.getScriptPathRootStrings());
            scriptEngine.setConfig(new CompilerConfiguration().addCompilationCustomizers(new SandboxTransformer(), importModifier.getImportCustomizer()));

            new CustomValueFilter(whitelistRegistry).register();

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
                            } catch (MultipleCompilationErrorsException e) {
                                System.out.println(e.getMessage());
                            } catch (NullPointerException e) {
                                System.out.println("Why is there a nullpointer");
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        } catch (Exception e){
            System.out.println("Error while loading scripts");
            e.printStackTrace();
        }

        extractMethods(scripts);
    }

    /**
     * Adds every method to the the knower, He knows what script has which method
     * to not call each script each time without the method existing
     * @param scripts List of scripts which should be scanned
     */
    private void extractMethods(List<Script> scripts) {
        functionKnower.clear();

        for (Script script : scripts) {
            List<MetaMethod> metaMethods = script.getMetaClass().getMethods();

            // adds the method names to the knower
            for (MetaMethod metaMethod : metaMethods) {
                ExtractedMethod extractedMethod = new ExtractedMethod(metaMethod.getName(), metaMethod.getParameterTypes().length);

                if (functionKnower.containsKey(extractedMethod)) {
                    functionKnower.get(extractedMethod).add(script);
                } else {// adds a new list when the key doesn't exist yet
                    List<Script> scriptList = new ArrayList<>();
                    scriptList.add(script);
                    functionKnower.put(extractedMethod, scriptList);
                }
            }
        }
    }

    //TODO: add event to each stage
    public void resetAllToDefault(){
        // OredictUtils.resetToDefault();
        // Pony.plugins.forEach(IPonyPlugin::resetToDefault);
    }

    /**
     * Runs all scripts
     * here the lowest scripts stuff is getting run
     */
    public void runAllScripts() {
        resetAllToDefault();
        ExtractedMethod extractedMethod = new ExtractedMethod("main", 1);

        if (functionKnower.containsKey(extractedMethod)) {
            for (Script script : functionKnower.get(extractedMethod)) {

                try {
                    launchWrapper.run(script);

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

    }

    public void runFunctionAll(String name, Object... args) {
        ExtractedMethod extractedMethod = new ExtractedMethod(name, args.length);

        if (functionKnower.containsKey(extractedMethod)) {

            for (Script script : functionKnower.get(extractedMethod)) {
                try {
                    launchWrapper.invokeMethod(script, name, args);

                } catch (GroovyBugError e) {
                    System.out.println(script.toString() + " couldn't run function [" + name + "] due to \n" + e.getMessage());
                } catch (Exception e) {
                    System.out.println(script.toString() + " couldn't run function [" + name + "] due to \n" + e.getMessage());
                    e.printStackTrace();
                }
            }
        } else {
            System.out.println("No script has the function [" + name + "]");
        }
    }

    private static class ExtractedMethod {
        String methodName;
        int argumentCount;

        ExtractedMethod(String methodName, int argumentCount) {
            this.argumentCount = argumentCount;
            this.methodName = methodName;
        }

        @Override
        public String toString() { return methodName + "<" + argumentCount + ">"; }

        @Override
        public int hashCode() { return Objects.hash(methodName, argumentCount); }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof ExtractedMethod && (((ExtractedMethod) obj).argumentCount == argumentCount && Objects.equals(((ExtractedMethod) obj).methodName, methodName));
        }
    }
}