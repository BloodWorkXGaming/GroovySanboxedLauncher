import de.bloodworkxgaming.groovysandboxedlauncher.Sandbox.GroovySandboxedLauncher
import de.bloodworkxgaming.groovysandboxedlauncher.defaults.WhitelistDefaults
import org.junit.Test

class TestStarter {
    @Test()
    void testScript() {
        def sandbox = new GroovySandboxedLauncher()

        sandbox.with {
            scriptPathConfig.registerScriptPathRoots("D:\\Users\\jonas\\Documents\\GitHub\\GroovySanboxedLauncher\\test\\groovyScripts")

            WhitelistDefaults.registerWhitelistMethodDefaults(whitelistRegistry)

            whitelistRegistry.invertObjectWhitelist()
            // whitelistRegistry.invertConstructorWhitelist()
            // whitelistRegistry.invertMethodWhitelist()

            importModifier.addStaticStars("java.lang.Math")
            // importModifier.addStarImports("java.lang.Object")


            initSandbox()
            loadScripts()

            runAllScripts()
        }
    }

    public static void testFile(){
        String s = "bllbla";
        s.replace("l", "lalalalalal");
    }
}
