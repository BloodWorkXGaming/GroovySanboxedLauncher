import de.bloodworkxgaming.groovysandboxedlauncher.sandbox.GroovySandboxedLauncher
import de.bloodworkxgaming.testclasses.StringMixin
import de.bloodworkxgaming.groovysandboxedlauncher.defaults.WhitelistDefaults
import de.bloodworkxgaming.testclasses.TestInterface
import org.junit.Test

class TestStarter {
    @Test()
    void testScript() {
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


    @Test
    void testNormalScriptEngine(){
        List<Script> scripts = new ArrayList<>()
        List<File> fileList = new ArrayList<>();
        fileList.add(new File("D:\\Users\\jonas\\Documents\\GitHub\\GroovySandboxedLauncher\\test\\groovyScripts"))

        Binding binding = new Binding();
        GroovyScriptEngine scriptEngine = new GroovyScriptEngine(getScriptPathRootStrings(fileList))

        for (f in fileList){
            for (file in f.listFiles()){
                scripts.add(scriptEngine.createScript(f.getName(), binding))
            }
        }

        for (Script s in scripts){
            s.run()
        }


    }

    static String[] getScriptPathRootStrings(List<File> fileList){
        String[] tempArray  = new String[fileList.size()];

        for (int i = 0; i < tempArray.length; i++){
            if (fileList.get(i) != null) {
                tempArray[i] = fileList.get(i).getAbsolutePath();
            }
        }

        return tempArray;
    }

}
