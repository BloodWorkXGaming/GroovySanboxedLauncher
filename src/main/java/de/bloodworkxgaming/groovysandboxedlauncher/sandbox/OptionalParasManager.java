package de.bloodworkxgaming.groovysandboxedlauncher.sandbox;

import de.bloodworkxgaming.groovysandboxedlauncher.annotations.GSLOptional;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static de.bloodworkxgaming.groovysandboxedlauncher.sandbox.GroovySandboxedLauncher.DEBUG;

public class OptionalParasManager {
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
                for (int i = 0; i < argumentTypes.length; i++) {
                    if (argumentTypes[i] == null || paras[i] != null && paras[i].getType().isAssignableFrom(argumentTypes[i]))
                        continue methodLoop;
                }


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
            if (args[i] != null && !paras[i].isAssignableFrom(args[i].getClass())) return false;
        }

        return true;
    }
}
