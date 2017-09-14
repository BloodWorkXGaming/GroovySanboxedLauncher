package de.bloodworkxgaming.groovysandboxedlauncher.defaults

import de.bloodworkxgaming.groovysandboxedlauncher.sandbox.WhitelistRegistry
import groovy.transform.CompileStatic

@CompileStatic
class WhitelistDefaults {
    static void registerWhitelistMethodDefaults(WhitelistRegistry registry) {

        registry.with {
            // ------------- Script natives ------------------
            registerMethod(Script.class, "println")
            registerMethod(Script.class, "print")
            registerMethod(Script.class, "printf")

            registerMethod(GroovyObject.class, "println")
            registerMethod(GroovyObject.class, "print")
            registerMethod(GroovyObject.class, "printf")
            registerMethod(Object.class, "with", Closure)

            // --------- Useful collections ---------
            registerWildCardMethod(List.class)
            registerWildCardMethod(Map.class)
            registerWildCardMethod(Set.class)

            registerConstructorCall(ArrayList.class, HashSet.class, HashMap.class)

            // --------- basic types --------------
            registerAllMethodsAndFields(String.class)
            registerAllMethodsAndFields(Boolean.class)
            registerAllMethodsAndFields(Long.class)
            registerAllMethodsAndFields(Integer.class)
            registerAllMethodsAndFields(Short.class)
            registerAllMethodsAndFields(Byte.class)
            registerAllMethodsAndFields(Double.class)
            registerAllMethodsAndFields(Float.class)

            registerWildCardMethodWithoutClass("toString", false)
            registerWildCardMethodWithoutClass("equals", false)
            registerWildCardMethodWithoutClass("hashCode", false)

            // ----------- Useful helper classes -------
            registerAllMethodsAndFields(Math.class)


        }

    }

}
