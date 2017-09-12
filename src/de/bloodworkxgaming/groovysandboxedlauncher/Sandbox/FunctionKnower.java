package de.bloodworkxgaming.groovysandboxedlauncher.Sandbox;

import groovy.lang.MetaMethod;
import groovy.lang.Script;

import java.util.*;

public class FunctionKnower {
    private Map<ExtractedMethod, List<Script>> functionKnower = new HashMap<>();

    /**
     * Adds every method to the the knower, He knows what script has which method
     * to not call each script each time without the method existing
     *
     * @param scripts List of scripts which should be scanned
     */
    public void extractMethods(List<Script> scripts) {
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

    @SuppressWarnings("unchecked")
    public List<Script> getImplementingScripts(String name, int argumentCount) {
        ExtractedMethod extractedMethod = new ExtractedMethod(name, argumentCount);

        return functionKnower.getOrDefault(extractedMethod, Collections.EMPTY_LIST);
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
