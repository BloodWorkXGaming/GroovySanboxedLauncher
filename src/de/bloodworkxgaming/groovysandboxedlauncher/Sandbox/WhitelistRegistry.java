package de.bloodworkxgaming.groovysandboxedlauncher.Sandbox;

import java.util.*;

public class WhitelistRegistry {
    private boolean invertMethodWhitelist = false;
    private boolean invertConstructorWhitelist = false;
    private boolean invertObjectWhitelist = false;

    private Map<String, Set<String>> allowedFunctions = new HashMap<>();
    private Set<String> allowedConstructorCalls = new HashSet<>();
    private Set<String> allowedObjectExistence = new HashSet<>();

    //region >>>> Method Whitelist
    /** register the class and the method name
     *
     * @param clazz Class that should be registered
     * @param methodName method that should be allowed
     *                   To allow any method from that class register it as a '*'
     */
    public void registerMethod(Class clazz, String methodName){
        Set<String> functions = allowedFunctions.getOrDefault(clazz.getName(), new HashSet<>());
        functions.add(methodName);

        allowedFunctions.put(clazz.getName(), functions);
    }

    /**
     * Register methods as string (if it is not present in the current classpath)
     * Skips any wrongly formatted entries
     * @param methods Identifier of the Mathod 'classname#methodname'
     *                Method name can be * for any
     */
    public void registerMethods(String... methods){
        for (String method : methods) {
            String[] split = method.split("#");
            if (split.length == 2){
                Set<String> functions = allowedFunctions.getOrDefault(split[0], new HashSet<>());
                functions.add(split[1]);

                allowedFunctions.put(split[0], functions);
            }
        }
    }

    /**
     * Checks whether the given class and method is registered
     */
    public boolean isMethodWhitelisted(Class clazz, String methodName) {
        if (clazz == null) return invertMethodWhitelist; // false

        Set<String> functions = allowedFunctions.get(clazz.getName());
        if (functions != null && (functions.contains(methodName)|| functions.contains("*"))){
            return !invertMethodWhitelist; // true
        }

        if (isMethodWhitelisted(clazz.getSuperclass(), methodName)) return !invertMethodWhitelist; // true

        for (Class interfaceClass : clazz.getInterfaces()) {
            if (isMethodWhitelisted(interfaceClass, methodName)){
                return !invertMethodWhitelist; // true
            }
        }

        return invertMethodWhitelist; // false
    }
    //endregion

    //region >>>> Constructor Whitelist
    /**
     * Registers the Constructor call for a specific Class
     */
    public void registerConstructorCall(Class... classes){
        for (Class aClass : classes) {
            allowedConstructorCalls.add(aClass.getName());
        }
    }

    /**
     * Registers the Constructor call for a specific Class
     */
    public void registerConstructorCall(String... classes){
        allowedConstructorCalls.addAll(Arrays.asList(classes));
    }

    /**
     * Checks whether the given Class is whitelisted
     */
    public boolean isConstructorWhitelisted(Class clazz){
        return invertConstructorWhitelist != allowedConstructorCalls.contains(clazz.getName());
    }
    //endregion

    //region >>>> Object Existence Whitelist
    /**
     * Allows this Object to exist at all
     */
    public void registerObjectExistence(Class... classes){
        for (Class aClass : classes) {
            allowedObjectExistence.add(aClass.getName());
        }
    }

    /**
     * Allows this Object to exist at all
     */
    public void registerObjectExistence(String... classes){
        allowedObjectExistence.addAll(Arrays.asList(classes));
    }

    /**
     * Checks whether the given Class is whitelisted
     */
    public boolean isObjectExistenceWhitelisted(Class clazz){
        return (invertObjectWhitelist != (clazz != null && allowedObjectExistence.contains(clazz.getName())));
    }
    //endregion

    public void invertMethodWhitelist() { this.invertMethodWhitelist = true; }

    public void invertConstructorWhitelist() { this.invertConstructorWhitelist = true; }

    public void invertObjectWhitelist() { this.invertObjectWhitelist = true; }
}
