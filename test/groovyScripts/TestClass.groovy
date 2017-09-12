package groovyScripts

import de.bloodworkxgaming.groovysandboxedlauncher.TestInterface
import de.bloodworkxgaming.groovysandboxedlauncher.annotations.GSLWhitelistMember

class TestClassGroovy extends TestInterface{
    TestClassGroovy(){
        // super(20)
    }
    TestClassGroovy(String s) {
        //super(Integer.valueOf(s))
        println "hi i am a constructor $s"
    }
    static int testInt = 20

    /*@Override
    int getMagic() {
        return super.getMagic() + -23
    }*/
}