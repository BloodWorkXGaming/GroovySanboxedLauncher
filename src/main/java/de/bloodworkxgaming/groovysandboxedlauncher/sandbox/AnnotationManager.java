package de.bloodworkxgaming.groovysandboxedlauncher.sandbox;

import de.bloodworkxgaming.groovysandboxedlauncher.annotations.GSLWhitelistClass;
import de.bloodworkxgaming.groovysandboxedlauncher.annotations.GSLWhitelistConstructor;
import de.bloodworkxgaming.groovysandboxedlauncher.annotations.GSLWhitelistMember;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class AnnotationManager {
    public static boolean checkHasClassAnnotation(Class<?> clazz) {
        return clazz.isAnnotationPresent(GSLWhitelistClass.class);
    }

    public static boolean checkHasConstructorAnnotation(Class<?> clazz) {
        return clazz != null && clazz.isAnnotationPresent(GSLWhitelistConstructor.class);
    }

    public static boolean checkHasMethodAnnotation(Class<?> clazz, String methodName) {
        if (clazz == null || methodName == null) return false;

        for (Method method : clazz.getDeclaredMethods()) {
            if (method.getName().equals(methodName)) {
                if(method.isAnnotationPresent(GSLWhitelistMember.class)) return true;
            }
        }

        if (checkHasMethodAnnotation(clazz.getSuperclass(), methodName)) return true;

        for (Class<?> interfaceClass : clazz.getInterfaces()) {
            if (checkHasMethodAnnotation(interfaceClass, methodName)) {
                return true;
            }
        }

        return false;
    }

    public static boolean checkHasFieldAnnotation(Class<?> clazz, String fieldName) {
        if (clazz == null || fieldName == null) return false;

        try {
            Field field = clazz.getDeclaredField(fieldName);
            return field.isAnnotationPresent(GSLWhitelistMember.class);
        } catch (NoSuchFieldException e) {
            if (checkHasFieldAnnotation(clazz.getSuperclass(), fieldName)) return true;

            for (Class<?> interfaceClass : clazz.getInterfaces()) {
                if (checkHasFieldAnnotation(interfaceClass, fieldName)) {
                    return true;
                }
            }
        }

        return false;
    }
}
