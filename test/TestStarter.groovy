import de.bloodworkxgaming.groovysandboxedlauncher.Sandbox.GroovySandboxedLauncher
import de.bloodworkxgaming.groovysandboxedlauncher.defaults.StringMixin
import de.bloodworkxgaming.groovysandboxedlauncher.defaults.WhitelistDefaults
import org.junit.Test

class TestStarter {
    @Test()
    void testScript() {
        def sandbox = new GroovySandboxedLauncher()

        sandbox.with {
            scriptPathConfig.registerScriptPathRoots("D:\\Users\\jonas\\Documents\\GitHub\\GroovySanboxedLauncher\\test\\groovyScripts")

            WhitelistDefaults.registerWhitelistMethodDefaults(whitelistRegistry)
            launchWrapper.registerMixin(String, StringMixin)

            whitelistRegistry.invertObjectWhitelist()
            // whitelistRegistry.invertConstructorWhitelist()
            // whitelistRegistry.invertMethodWhitelist()
            // whitelistRegistry.invertFieldWhitelist()

            importModifier.addStaticStars("java.lang.Math")


            initSandbox()
            loadScripts()

            runAllScripts()
        }
    }
}
