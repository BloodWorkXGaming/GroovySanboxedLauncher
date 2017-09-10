package de.bloodworkxgaming.groovysandboxedlauncher.Sandbox

/**
 * This class provides a wrapper around the launched scripts.
 * This allows other classes to be mixed in native classes or other classes from different sources
 */
class LaunchWrapper {
    public static List<Class> StringMixins = new ArrayList<>()


    static def init() {
        String.metaClass.mixin(StringMixins)
    }

    static def run(Script script) {
        script.run()
    }

    static def invokeMethod(Script script, String name, Object... args) {
        script.invokeMethod(name, args)
    }
}
