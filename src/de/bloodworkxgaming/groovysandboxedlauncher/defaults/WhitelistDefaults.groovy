package de.bloodworkxgaming.groovysandboxedlauncher.defaults

import de.bloodworkxgaming.groovysandboxedlauncher.Sandbox.WhitelistRegistry
import org.codehaus.groovy.runtime.DefaultGroovyMethods

class WhitelistDefaults {
    static void registerWhitelistMethodDefaults(WhitelistRegistry registry){

        registry.with {
            // ------------- Script natives ------------------
            registerMethod(Script.class, "println")
            registerMethod(Script.class, "print")
            registerMethod(Script.class, "printf")

            registerMethod(GroovyObject.class, "println") //TODO: Handle mixins better, as this is just a freepass to any class that has a print method
            registerMethod(GroovyObject.class, "print")
            registerMethod(GroovyObject.class, "printf")

            // --------- Useful collections ---------
            registerMethod(List.class, "*")
            registerMethod(Map.class, "*")
            registerMethod(Set.class, "*")

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

            registerMethod(Object.class, "toString")
            registerMethod(Object.class, "equals")
            registerMethod(Object.class, "hashCode")

            // ----------- Useful helper classes -------
            registerMethod(Math.class, "*")


        }

    }

}
