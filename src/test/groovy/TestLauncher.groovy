import de.bloodworkxgaming.groovysandboxedlauncher.defaults.WhitelistDefaults
import de.bloodworkxgaming.groovysandboxedlauncher.sandbox.GroovySandboxedLauncher
import de.bloodworkxgaming.testclasses.StringMixin
import de.bloodworkxgaming.testclasses.TestInterface

class TestLauncher {
    public static void main(String[] args){
        def sandbox = new GroovySandboxedLauncher()

        sandbox.with {
            scriptPathConfig.registerScriptPathRoots("D:\\Users\\jonas\\Documents\\GitHub\\GroovySandboxedLauncher\\src\\test\\groovy\\groovyScripts")
            scriptPathConfig.registerScriptPathRoots("D:\\Users\\jonas\\Documents\\GitHub\\GroovySandboxedLauncher\\src\\test\\groovy\\testScripts")

            WhitelistDefaults.registerWhitelistMethodDefaults(whitelistRegistry)
            launchWrapper.registerMixin(String, StringMixin)

            whitelistRegistry.registerField(Script, "recipes")

            whitelistRegistry.invertObjectWhitelist()
            // whitelistRegistry.invertConstructorWhitelist()
            // whitelistRegistry.invertMethodWhitelist()
            // whitelistRegistry.invertFieldWhitelist()

            importModifier.addStaticStars("java.lang.Math")


            getBinding().setVariable("recipes", new TestInterface())


            initSandbox()
            loadScripts()

            runAllScripts()


            String bla = "hi i am a init test string"

            runFunctionAll("init", bla)
        }
    }
}
