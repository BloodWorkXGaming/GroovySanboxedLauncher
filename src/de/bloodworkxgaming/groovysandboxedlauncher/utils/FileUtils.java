package de.bloodworkxgaming.groovysandboxedlauncher.utils;

import de.bloodworkxgaming.groovysandboxedlauncher.annotations.GSLWhitelistMember;

import java.io.File;

public class FileUtils {
    public static String getExtension(File file) {
        int lastDot = file.getName().indexOf(".");
        if (lastDot >= 0) {
            return file.getName().substring(lastDot + 1);
        } else {
            return "";
        }
    }

    public static boolean isExtension(File file, String extension) {
        return getExtension(file).equals(extension);
    }

    @GSLWhitelistMember
    public static String test(String bla) {
        return bla.replace("l", "blub");
    }
}
