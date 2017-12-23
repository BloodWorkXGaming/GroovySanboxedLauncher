package de.bloodworkxgaming.groovysandboxedlauncher.utils;

import java.util.ArrayList;

public class StringUtils {
    public static String capitalize(String string) {
        return string.substring(0, 1).toUpperCase() + string.substring(1);
    }

    public static String lowercaseFirstLetter(String string) {
        return string.substring(0, 1).toLowerCase() + string.substring(1);
    }

    public static String[] classArrayToStringArray(Class<?>[] classes) {
        ArrayList<String> strings = new ArrayList<>(classes.length);
        for (Class<?> aClass : classes) {
            if (aClass != null) {
                strings.add(aClass.getName());
            }
        }

        return strings.toArray(new String[0]);
    }
}
