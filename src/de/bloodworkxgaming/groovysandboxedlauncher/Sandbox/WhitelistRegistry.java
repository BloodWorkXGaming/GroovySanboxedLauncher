package de.bloodworkxgaming.groovysandboxedlauncher.Sandbox;

import de.bloodworkxgaming.groovysandboxedlauncher.annotations.GSLWhitelistMember;
import de.bloodworkxgaming.groovysandboxedlauncher.annotations.GSLWhitelistClass;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

public class WhitelistRegistry {
    private boolean invertMethodWhitelist = false;
    private boolean invertFieldWhitelist = false;
    private boolean invertConstructorWhitelist = false;
    private boolean invertObjectWhitelist = false;

    private Map<String, Set<String>> allowedFunctions = new HashMap<>();
    private Map<String, Set<String>> allowedFields = new HashMap<>();
    private Set<String> allowedConstructorCalls = new HashSet<>();
    private Set<String> allowedObjectExistence = new HashSet<>();

    //region >>>> Method Whitelist
    /** register the class and the method name
     *
     * @param clazz Class that should be registered
     * @param methodName method that should be allowed
     *                   To allow any method from that class register it as a '*'
     */
    public void registerMethod(Class<?> clazz, String methodName){
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
    public boolean isMethodWhitelisted(Class<?> clazz, String methodName) {
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
    public void registerConstructorCall(Class<?>... classes){
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
    public boolean isConstructorWhitelisted(Class<?> clazz){
        return invertConstructorWhitelist != allowedConstructorCalls.contains(clazz.getName());
    }
    //endregion

    //region >>>> Object Existence Whitelist
    /**
     * Allows this Object to exist at all
     */
    public void registerObjectExistence(Class<?>... classes){
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
    public boolean isObjectExistenceWhitelisted(Class<?> clazz){
        return (invertObjectWhitelist != (clazz != null && allowedObjectExistence.contains(clazz.getName())));
    }
    //endregion

    //region >>>> Field Whitelist
    /** register the class and the field name
     *
     * @param clazz Class that should be registered
     * @param fieldName field that should be allowed
     *                  To allow any field from that class register it as a '*'
     */
    public void registerField(Class<?> clazz, String fieldName){
        Set<String> fields = allowedFields.getOrDefault(clazz.getName(), new HashSet<>());
        fields.add(fieldName);

        allowedFields.put(clazz.getName(), fields);
    }

    /**
     * Register fields as string (if it is not present in the current classpath)
     * Skips any wrongly formatted entries
     * @param fields Identifier of the Mathod 'classname#fieldName'
     *               FieldName can be '*' for any
     */
    public void registerFields(String... fields){
        for (String field : fields) {
            String[] split = field.split("#");
            if (split.length == 2){
                Set<String> fieldNames = allowedFields.getOrDefault(split[0], new HashSet<>());
                fieldNames.add(split[1]);

                allowedFields.put(split[0], fieldNames);
            }
        }
    }

    /**
     * Checks whether the given class and Field is registered
     */
    public boolean isFieldWhitelisted(Class<?> clazz, String fieldName) {
        if (clazz == null) return invertFieldWhitelist; // false

        Set<String> fields = allowedFields.get(clazz.getName());
        if (fields != null && (fields.contains(fieldName)|| fields.contains("*"))){
            return !invertFieldWhitelist; // true
        }

        if (isFieldWhitelisted(clazz.getSuperclass(), fieldName)) return !invertFieldWhitelist; // true

        for (Class<?> interfaceClass : clazz.getInterfaces()) {
            if (isFieldWhitelisted(interfaceClass, fieldName)){
                return !invertFieldWhitelist; // true
            }
        }

        return invertFieldWhitelist; // false
    }
    //endregion

    //region >>>> Whitelist -> Blacklist Inverter
    public void invertMethodWhitelist() { this.invertMethodWhitelist = true; }

    public void invertConstructorWhitelist() { this.invertConstructorWhitelist = true; }

    public void invertObjectWhitelist() { this.invertObjectWhitelist = true; }

    public void invertFieldWhitelist() { this.invertFieldWhitelist = true; }

    public boolean isInvertedMethodWhitelist() { return invertMethodWhitelist; }

    public boolean isInvertedConstructorWhitelist() { return invertConstructorWhitelist; }

    public boolean isInvertedObjectWhitelist() { return invertObjectWhitelist; }

    public boolean isInvertedFieldWhitelist() { return invertFieldWhitelist; }
    //endregion

    public void registerAllMethodsAndFields(Class clazz){
        registerMethod(clazz, "*");
        registerField(clazz, "*");
    }
}
