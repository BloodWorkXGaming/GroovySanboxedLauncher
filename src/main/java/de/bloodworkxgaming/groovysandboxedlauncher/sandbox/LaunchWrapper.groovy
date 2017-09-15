package de.bloodworkxgaming.groovysandboxedlauncher.sandbox

import de.bloodworkxgaming.groovysandboxedlauncher.compilercustomizer.IMixinProvider

/**
 * This class provides a wrapper around the launched scripts.
 * This allows other classes to be mixed in native classes or other classes from different sources
 */
class LaunchWrapper {
    private Map<Class<?>, List<Class<?>>> mixingMap = new HashMap<>()
    private List<IMixinProvider> iMixinProviders = new ArrayList<>()

    void registerMixin(Class<?> classToMixInto, Class<?> mixinClass) {
        def list = mixingMap.getOrDefault(classToMixInto, new ArrayList<>())
        list.add(mixinClass)

        mixingMap.put(classToMixInto, list)
    }
    void registerMixinProvider(IMixinProvider mixinProvider) {
        iMixinProviders.add(mixinProvider)
    }

    def init() {
        mixingMap.forEach { key, value ->
            key.getMetaClass().mixin(value)
        }

        for (prov in iMixinProviders){
            prov.mixIn()
        }
    }

    static def run(Script script) {
        script.run()
    }

    static def invokeMethod(Script script, String name, Object... args) {
        script.invokeMethod(name, args)
    }
}
