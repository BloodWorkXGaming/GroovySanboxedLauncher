package de.bloodworkxgaming.groovysandboxedlauncher.Sandbox;

import de.bloodworkxgaming.groovysandboxedlauncher.annotations.GSLWhitelistClass;
import de.bloodworkxgaming.groovysandboxedlauncher.annotations.GSLWhitelistMember;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class AnnotationManager {
    public static boolean checkHasClassAnnotation(Class<?> clazz){
        return clazz.isAnnotationPresent(GSLWhitelistClass.class);
    }

    public static boolean checkHasConstructorAnnotation(Class<?> clazz){
        return clazz != null && clazz.isAnnotationPresent(GSLWhitelistClass.class);
    }

    public static boolean checkHasMethodAnnotation(Class<?> clazz, String methodName){
        for (Method method : clazz.getMethods()) {
            if (method.getName().equals(methodName)){
                return method.isAnnotationPresent(GSLWhitelistMember.class);
            }
        }

        return false;
    }

    public static boolean checkHasFieldAnnotation(Class<?> clazz, String fieldName){
        try {
            Field field = clazz.getDeclaredField(fieldName);
            return field.isAnnotationPresent(GSLWhitelistMember.class);
        } catch (NoSuchFieldException e) {
            return false;
        }
    }
}
