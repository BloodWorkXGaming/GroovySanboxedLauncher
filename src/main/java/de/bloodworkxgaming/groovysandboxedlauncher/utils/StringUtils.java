package de.bloodworkxgaming.groovysandboxedlauncher.utils;

public class StringUtils {
    public static String capitalize(String string) {
        return string.substring(0, 1).toUpperCase() + string.substring(1);
    }

    public static String lowercaseFirstLetter(String string) {
        return string.substring(0, 1).toLowerCase() + string.substring(1);
    }
}
