package de.bloodworkxgaming.groovysandboxedlauncher.sandbox;

import de.bloodworkxgaming.groovysandboxedlauncher.annotations.GSLOptional;
import de.bloodworkxgaming.groovysandboxedlauncher.annotations.GSLWhitelistClass;
import de.bloodworkxgaming.groovysandboxedlauncher.annotations.GSLWhitelistConstructor;
import de.bloodworkxgaming.groovysandboxedlauncher.annotations.GSLWhitelistMember;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class AnnotationManager {
    public static final List<Class<? extends Annotation>> annotationsOptionalParameter = new ArrayList<>();
    private static List<Class<? extends Annotation>> annotationsMemberWhitelist = new ArrayList<>();
    private static List<Class<? extends Annotation>> annotationsClassWhitelist = new ArrayList<>();
    private static List<Class<? extends Annotation>> annotationsConstructorWhitelist = new ArrayList<>();

    static {
        annotationsClassWhitelist.add(GSLWhitelistClass.class);
        annotationsConstructorWhitelist.add(GSLWhitelistConstructor.class);
        annotationsMemberWhitelist.add(GSLWhitelistMember.class);
        annotationsOptionalParameter.add(GSLOptional.class);
    }

    public static boolean checkHasClassAnnotation(Class<?> clazz) {
        return annotationsClassWhitelist.stream().anyMatch(clazz::isAnnotationPresent);
    }

    public static boolean checkHasConstructorAnnotation(Class<?> clazz) {
        return clazz != null && annotationsConstructorWhitelist.stream().anyMatch(clazz::isAnnotationPresent);
    }

    public static boolean checkHasMethodAnnotation(Class<?> clazz, String methodName) {
        if (clazz == null || methodName == null) return false;

        for (Method method : clazz.getDeclaredMethods()) {
            if (method.getName().equals(methodName)) {
                if (annotationsMemberWhitelist.stream().anyMatch(method::isAnnotationPresent)) return true;
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

    /**
     * Registering a annotation here will make it have the same effect as {@link GSLWhitelistClass}
     */
    public static void registerClassWhitelistingAnnotation(Class<? extends Annotation> annotationClass) {
        annotationsClassWhitelist.add(annotationClass);
    }

    /**
     * Registering a annotation here will make it have the same effect as {@link GSLWhitelistMember}
     */
    public static void registerMemberWhitelistingAnnotation(Class<? extends Annotation> annotationClass) {
        annotationsMemberWhitelist.add(annotationClass);
    }

    /**
     * Registering a annotation here will make it have the same effect as {@link GSLWhitelistConstructor}
     */
    public static void registerConstructorWhitelistingAnnotation(Class<? extends Annotation> annotationClass) {
        annotationsConstructorWhitelist.add(annotationClass);
    }

    /**
     * Registering a annotation here will make it have the same effect as {@link de.bloodworkxgaming.groovysandboxedlauncher.annotations.GSLOptional}
     */
    public static void registerOptionalParameterAnnotation(Class<? extends Annotation> annotationClass) {
        annotationsOptionalParameter.add(annotationClass);
    }
}
