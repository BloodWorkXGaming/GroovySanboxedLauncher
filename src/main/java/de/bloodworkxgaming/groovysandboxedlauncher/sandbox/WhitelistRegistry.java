package de.bloodworkxgaming.groovysandboxedlauncher.sandbox;

import java.lang.reflect.Method;
import java.util.*;

public class WhitelistRegistry {
    private boolean invertMethodWhitelist = false;
    private boolean invertFieldWhitelist = false;
    private boolean invertConstructorWhitelist = false;
    private boolean invertObjectWhitelist = false;


    // private Map<String, Set<String>> allowedFunctions = new HashMap<>();
    private Set<WhiteListedMethod> allowedMethods = new HashSet<>();

    private Map<String, Set<String>> allowedFields = new HashMap<>();
    private Set<String> allowedConstructorCalls = new HashSet<>();
    private Set<String> allowedObjectExistence = new HashSet<>();

    //region >>>> Method Whitelist

    /**
     * register the class and the method name
     *
     * @param clazz      Class that should be registered
     * @param methodName method that should be allowed
     */
    public void registerMethod(Class<?> clazz, String methodName) {
        allowedMethods.add(new WhiteListedMethod(clazz, methodName));
    }

    public void registerMethod(Class<?> clazz, String methodName, Class<?>... arguments) {
        allowedMethods.add(new WhiteListedMethod(clazz, methodName, arguments));
    }

    public void registerWildCardMethod(Class<?> clazz) {
        allowedMethods.add(new WhiteListedMethod(clazz, false, null, true, true));
    }

    public void registerWildCardMethodWithoutClass(String functionName, boolean wildcardArguments, Class<?>... arguments) {
        allowedMethods.add(new WhiteListedMethod(null, true, functionName, false, wildcardArguments, arguments));
    }


    /**
     * Register methods as string (if it is not present in the current classpath)
     * Skips any wrongly formatted entries
     */
    public void registerMethod(String className, boolean isWildcardClass, String methodName, boolean isWildcardMethod, boolean isWildcardArguments, String... arguments) {
        try {
            Class<?>[] classes = new Class<?>[arguments.length];
            for (int i = 0; i < arguments.length; i++) {
                classes[i] = Class.forName(arguments[i]);
            }

            allowedMethods.add(new WhiteListedMethod(Class.forName(className), isWildcardClass, methodName, isWildcardMethod, isWildcardArguments, classes));
        } catch (ClassNotFoundException e) {
            System.out.println("Couldn't find a Class while trying to register Method to whitelist:\n" + e.getMessage());
        }
    }

    /**
     * Checks whether the given class and method is registered
     */
    public boolean isMethodWhitelisted(Class<?> clazz, String methodName, Class<?>... args) {
        if (clazz == null || methodName == null) return invertMethodWhitelist; // false

        WhiteListedMethod method = new WhiteListedMethod(clazz, false, methodName, false, false, args);
        if (allowedMethods.contains(method)) return !invertMethodWhitelist; // true


        //if (!implementsFunction(clazz, methodName, args)) {
        if (isMethodWhitelisted(clazz.getSuperclass(), methodName, args)) return !invertMethodWhitelist; // true

        for (Class interfaceClass : clazz.getInterfaces()) {
            if (isMethodWhitelisted(interfaceClass, methodName, args)) {
                return !invertMethodWhitelist; // true
            }
        }
        // }

        return invertMethodWhitelist; // false
    }

    private boolean implementsFunction(Class<?> clazz, String methodName, Class<?>... params) {

        outerLoop:
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.getName().equals(methodName)) {
                Class<?>[] paraTypes = method.getParameterTypes();
                if (paraTypes.length != params.length) continue;

                for (int i = 0; i < paraTypes.length; i++) {
                    if (!(params[i] == null && !paraTypes[i].isPrimitive()) || !paraTypes[i].isAssignableFrom(params[i]))
                        continue outerLoop;
                }

                return true;
            }
        }

        return false;
    }
    //endregion

    //region >>>> Constructor Whitelist

    /**
     * Registers the Constructor call for a specific Class
     */
    public void registerConstructorCall(Class<?>... classes) {
        for (Class aClass : classes) {
            allowedConstructorCalls.add(aClass.getName());
        }
    }

    /**
     * Registers the Constructor call for a specific Class
     */
    public void registerConstructorCall(String... classes) {
        allowedConstructorCalls.addAll(Arrays.asList(classes));
    }

    /**
     * Checks whether the given Class is whitelisted
     */
    public boolean isConstructorWhitelisted(Class<?> clazz) {
        return invertConstructorWhitelist != allowedConstructorCalls.contains(clazz.getName());
    }
    //endregion

    //region >>>> Object Existence Whitelist

    /**
     * Allows this Object to exist at all
     */
    public void registerObjectExistence(Class<?>... classes) {
        for (Class aClass : classes) {
            allowedObjectExistence.add(aClass.getName());
        }
    }

    /**
     * Allows this Object to exist at all
     */
    public void registerObjectExistence(String... classes) {
        allowedObjectExistence.addAll(Arrays.asList(classes));
    }

    /**
     * Checks whether the given Class is whitelisted
     */
    public boolean isObjectExistenceWhitelisted(Class<?> clazz) {
        return (invertObjectWhitelist != (clazz != null && allowedObjectExistence.contains(clazz.getName())));
    }
    //endregion

    //region >>>> Field Whitelist

    /**
     * register the class and the field name
     *
     * @param clazz     Class that should be registered
     * @param fieldName field that should be allowed
     *                  To allow any field from that class register it as a '*'
     */
    public void registerField(Class<?> clazz, String fieldName) {
        Set<String> fields = allowedFields.getOrDefault(clazz.getName(), new HashSet<>());
        fields.add(fieldName);

        allowedFields.put(clazz.getName(), fields);
    }

    /**
     * Register fields as string (if it is not present in the current classpath)
     * Skips any wrongly formatted entries
     *
     * @param fields Identifier of the Mathod 'classname#fieldName'
     *               FieldName can be '*' for any
     */
    public void registerFields(String... fields) {
        for (String field : fields) {
            String[] split = field.split("#");
            if (split.length == 2) {
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
        if (fields != null && (fields.contains(fieldName) || fields.contains("*"))) {
            return !invertFieldWhitelist; // true
        }

        if (isFieldWhitelisted(clazz.getSuperclass(), fieldName)) return !invertFieldWhitelist; // true

        for (Class<?> interfaceClass : clazz.getInterfaces()) {
            if (isFieldWhitelisted(interfaceClass, fieldName)) {
                return !invertFieldWhitelist; // true
            }
        }

        return invertFieldWhitelist; // false
    }
    //endregion

    //region >>>> Whitelist -> Blacklist Inverter
    public void invertMethodWhitelist() {
        this.invertMethodWhitelist = true;
    }

    public void invertConstructorWhitelist() {
        this.invertConstructorWhitelist = true;
    }

    public void invertObjectWhitelist() {
        this.invertObjectWhitelist = true;
    }

    public void invertFieldWhitelist() {
        this.invertFieldWhitelist = true;
    }

    public boolean isInvertedMethodWhitelist() {
        return invertMethodWhitelist;
    }

    public boolean isInvertedConstructorWhitelist() {
        return invertConstructorWhitelist;
    }

    public boolean isInvertedObjectWhitelist() {
        return invertObjectWhitelist;
    }

    public boolean isInvertedFieldWhitelist() {
        return invertFieldWhitelist;
    }
    //endregion

    public void registerAllMethodsAndFields(Class clazz) {
        registerWildCardMethod(clazz);
        registerField(clazz, "*");
    }


    static class WhiteListedMethod {
        Class<?> clazz;
        boolean isWildcardClass;

        String methodName;
        boolean isWildcardMethod;

        boolean isWildcardArguments;
        Class<?>[] arguments;


        public WhiteListedMethod(Class<?> clazz, boolean isWildcardClass, String methodName, boolean isWildcardMethod, boolean isWildcardArguments, Class<?>... arguments) {
            this.clazz = clazz;
            this.isWildcardClass = isWildcardClass;
            this.methodName = methodName;
            this.isWildcardMethod = isWildcardMethod;
            this.isWildcardArguments = isWildcardArguments;
            this.arguments = arguments;
        }

        public WhiteListedMethod(Class<?> clazz, String methodName) {
            this.clazz = clazz;
            isWildcardClass = false;
            this.methodName = methodName;
            isWildcardArguments = true;
        }

        public WhiteListedMethod(Class<?> clazz, String methodName, Class<?>... arguments) {
            this.clazz = clazz;
            isWildcardClass = false;
            this.methodName = methodName;
            this.arguments = arguments;
            isWildcardArguments = false;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            // Whitelisted objects in the map are THAT
            // objects to compare to THIS
            WhiteListedMethod that = (WhiteListedMethod) o;

            boolean areTheSame;

            areTheSame = that.isWildcardClass || this.isWildcardClass || Objects.equals(that.clazz, this.clazz);
            if (!areTheSame) return false;

            areTheSame = that.isWildcardMethod || this.isWildcardMethod || Objects.equals(that.methodName, this.methodName);
            if (!areTheSame) return false;

            areTheSame = that.isWildcardArguments || this.isWildcardArguments || classTypesEquals(that.arguments, this.arguments);
            return areTheSame;
        }

        @Override
        public int hashCode() {
            return clazz != null ? clazz.hashCode() : 0;
        }

        // Class objects in the map are a1
        // Class objects to compare to a2
        private boolean classTypesEquals(Class<?>[] a1, Class<?>[] a2) {
            if (a1 == a2)
                return true;
            if (a1 == null || a2 == null)
                return false;

            int length = a1.length;
            if (a2.length != length)
                return false;

            for (int i = 0; i < length; i++) {
                Class<?> c1 = a1[i];
                Class<?> c2 = a2[i];
                if (c1 != null && c2 != null) {
                    if (!c1.isAssignableFrom(c2)) return false;
                }
            }

            return true;
        }
    }
}
