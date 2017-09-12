package de.bloodworkxgaming.groovysandboxedlauncher.Sandbox

/**
 * This class provides a wrapper around the launched scripts.
 * This allows other classes to be mixed in native classes or other classes from different sources
 */
class LaunchWrapper {
    private Map<Class<?>, List<Class<?>>> mixingMap = new HashMap<>()

    void registerMixin(Class<?> classToMixInto, Class<?> mixinClass) {
        def list = mixingMap.getOrDefault(classToMixInto, new ArrayList<>())
        list.add(mixinClass)

        mixingMap.put(classToMixInto, list)
    }

    def init() {
        mixingMap.forEach { key, value ->
            key.getMetaClass().mixin(value)
        }
    }

    static def run(Script script) {
        script.run()
    }

    static def invokeMethod(Script script, String name, Object... args) {
        script.invokeMethod(name, args)
    }
}
