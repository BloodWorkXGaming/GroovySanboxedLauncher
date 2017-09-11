package de.bloodworkxgaming.groovysandboxedlauncher.defaults

import de.bloodworkxgaming.groovysandboxedlauncher.Sandbox.WhitelistRegistry

class WhitelistDefaults {
    static void registerWhitelistMethodDefaults(WhitelistRegistry registry){

        registry.with {
            registerMethod(Script.class, "println")
            registerMethod(Script.class, "print")
            registerMethod(Script.class, "printf")

            registerMethod(List.class, "*")
            registerMethod(Math.class, "*")
        }

    }

}
