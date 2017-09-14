package de.bloodworkxgaming.groovysandboxedlauncher.sandbox;

import de.bloodworkxgaming.groovysandboxedlauncher.annotations.GSLOptional;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

import static de.bloodworkxgaming.groovysandboxedlauncher.sandbox.GroovySandboxedLauncher.DEBUG;

public class OptionalParasManager {
    private static final Map<Class<?>, Class<?>> PRIMITIVES_TO_WRAPPERS = new HashMap<>();;

    static {
        PRIMITIVES_TO_WRAPPERS.put(boolean.class, Boolean.class);
        PRIMITIVES_TO_WRAPPERS.put(byte.class, Byte.class);
        PRIMITIVES_TO_WRAPPERS.put(char.class, Character.class);
        PRIMITIVES_TO_WRAPPERS.put(double.class, Double.class);
        PRIMITIVES_TO_WRAPPERS.put(float.class, Float.class);
        PRIMITIVES_TO_WRAPPERS.put(int.class, Integer.class);
        PRIMITIVES_TO_WRAPPERS.put(long.class, Long.class);
        PRIMITIVES_TO_WRAPPERS.put(short.class, Short.class);
        PRIMITIVES_TO_WRAPPERS.put(void.class, Void.class);
    }


    public static Object[] checkParas(Class<?> receiver, String methodName, Object... args) {
        // gets all the valid methods
        List<Method> methods = new ArrayList<>();
        for (Method method : receiver.getMethods()) {
            if (method.getName().equals(methodName)) methods.add(method);
        }

        Class<?>[] argumentTypes = CustomValueFilter.objectToClassArray(args);

        // checks whether there is anything that was found
        // also checks whether it is already valid
        boolean canAssignAny = false;
        boolean foundSomething = false;
        for (Method method : methods) {
            if (!method.getName().equals(methodName)) continue;
            foundSomething = true;
            if (canBeAssigned(method.getParameterTypes(), args)) {
                canAssignAny = true;
                break;
            }
        }

        if (!canAssignAny && foundSomething) {
            if (DEBUG) System.out.println("Can't assign to any possible method implementation '" + methodName + "' args: " + Arrays.toString(args));
        } else {
            return args;
        }

        // loops over the possible methods and applies the transformations
        methodLoop:
        for (Method method : methods) {
            if (!method.getName().equals(methodName)) continue;

            Parameter[] paras = method.getParameters();
            if (paras.length > argumentTypes.length) {

                // checks whether the first few parameters are correct
                if (!canBeAssigned(
                        Arrays.copyOfRange(method.getParameterTypes(), 0, argumentTypes.length - 1),
                        Arrays.copyOfRange(args, 0, argumentTypes.length - 1))) continue;

                // list of objects that will be returned as new parameters
                List<Object> objects = new ArrayList<>();
                Collections.addAll(objects, args);

                // whether the rest of the parameters are annotated and if ye, then it adds it to the list of objects
                for (int i = argumentTypes.length; i < paras.length; i++) {
                    Class<?> paraType = paras[i].getType();
                    Annotation[] annotations = paras[i].getAnnotations();
                    GSLOptional optional = null;

                    boolean hasMatch = false;
                    for (Annotation annotation : annotations) {
                        for (Class<? extends Annotation> aClass : AnnotationManager.annotationsOptionalParameter) {
                            if (aClass.equals(annotation.annotationType())) {
                                hasMatch = true;
                                if (annotation instanceof GSLOptional) {
                                    optional = (GSLOptional) annotation;
                                }
                            }
                        }
                    }

                    if (!hasMatch) continue methodLoop;

                    DefaultValueAssigner.assignDefaultValue(optional, objects, paraType);
                }

                return objects.toArray();
            }
        }

        return args;
    }

    private static boolean canBeAssigned(Class<?>[] paras, Object[] args) {
        if (paras == null && args == null) return true;
        if (paras == null || args == null) return false;

        if (paras.length != args.length) return false;

        for (int i = 0; i < paras.length; i++) {
            if (args[i] == null && !paras[i].isPrimitive()) continue;
            if (paras[i].isPrimitive() && isPrimitiveOfSameType(paras[i], args[i].getClass())) continue;
            if (paras[i].isAssignableFrom(args[i].getClass())) continue;
            return false;
        }

        return true;
    }


    private static boolean isPrimitiveOfSameType(Class<?> type1, Class<?> type2){
        if (type1.isPrimitive() && type2.isPrimitive()){
            return type1.equals(type2);
        }
        Class<?> primitive;
        Class<?> other;

        if (type1.isPrimitive()){
            primitive = type1;
            other = type2;
        }else {
            primitive = type2;
            other = type1;
        }

        Class<?> wrapper = PRIMITIVES_TO_WRAPPERS.get(primitive);

        return other.equals(wrapper);
    }
}
