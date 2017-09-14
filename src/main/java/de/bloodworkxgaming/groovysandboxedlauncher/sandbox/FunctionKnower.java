package de.bloodworkxgaming.groovysandboxedlauncher.sandbox;

import de.bloodworkxgaming.groovysandboxedlauncher.data.GSLScriptFile;
import groovy.lang.MetaMethod;
import groovy.lang.Script;

import java.util.*;

public class FunctionKnower {
    public static final List<GSLScriptFile> EMPTY_LIST = Collections.emptyList();
    private Map<ExtractedMethod, List<GSLScriptFile>> functionKnower = new HashMap<>();

    /**
     * Adds every method to the the knower, He knows what script has which method
     * to not call each script each time without the method existing
     *
     * @param scripts List of scripts which should be scanned
     */
    public void extractMethods(List<GSLScriptFile> scripts) {
        functionKnower.clear();

        for (GSLScriptFile gslScriptFile : scripts) {
            Script script = gslScriptFile.getScript();
            if (script == null) continue;

            List<MetaMethod> metaMethods = script.getMetaClass().getMethods();

            // adds the method names to the knower
            for (MetaMethod metaMethod : metaMethods) {
                ExtractedMethod extractedMethod = new ExtractedMethod(metaMethod.getName(), metaMethod.getParameterTypes().length);
                List<GSLScriptFile> list = functionKnower.getOrDefault(extractedMethod, new ArrayList<>());
                list.add(gslScriptFile);
                functionKnower.put(extractedMethod, list);
            }
        }
    }


    public List<GSLScriptFile> getImplementingScripts(String name, int argumentCount) {
        ExtractedMethod extractedMethod = new ExtractedMethod(name, argumentCount);

        return functionKnower.getOrDefault(extractedMethod, EMPTY_LIST);
    }

    public boolean isFunctionImplemented(String name, int argumentCount) {
        ExtractedMethod extractedMethod = new ExtractedMethod(name, argumentCount);
        return functionKnower.containsKey(extractedMethod);
    }

    /**
     * Inner class to store the extracted methods
     */
    public static class ExtractedMethod {

        String methodName;
        int argumentCount;

        ExtractedMethod(String methodName, int argumentCount) {
            this.argumentCount = argumentCount;
            this.methodName = methodName;
        }

        @Override
        public String toString() {
            return methodName + "<" + argumentCount + ">";
        }

        @Override
        public int hashCode() {
            return Objects.hash(methodName, argumentCount);
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof ExtractedMethod && (((ExtractedMethod) obj).argumentCount == argumentCount && Objects.equals(((ExtractedMethod) obj).methodName, methodName));
        }
    }


}
