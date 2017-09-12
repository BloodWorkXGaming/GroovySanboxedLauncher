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


    @Test
    void testNormalScriptEngine(){
        List<Script> scripts = new ArrayList<>()
        List<File> fileList = new ArrayList<>();
        fileList.add(new File("D:\\Users\\jonas\\Documents\\GitHub\\GroovySanboxedLauncher\\test\\groovyScripts"))

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
