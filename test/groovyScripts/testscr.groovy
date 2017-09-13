package groovyScripts

import de.bloodworkxgaming.groovysandboxedlauncher.annotations.GSLWhitelistMember
import de.bloodworkxgaming.groovysandboxedlauncher.utils.FileUtils
import de.bloodworkxgaming.testclasses.TestInterface

//::priority 20
println "hi".setNBT("blabla")
def bla = [29, 20]

println(bla)

def test = 20;
println FileUtils.test("blib");

blub("lalal");

def testFile = new TestClassGroovy("blub")
// println TestInterface.@testInt = 10
println "TestArray: ${TestInterface.@testArray[0]}"

// println testFile.hithere
// println testFile.getMagic()

// println testBlub.blaTest
println TestClassGroovy.getTestInt()


// println String.valueOf(20)

// def f = new File("D:\\Users\\jonas\\Documents\\GitHub\\GroovySanboxedLauncher\\test\\groovyScripts\\testscr.groovy")
// println(f.toString())


def tecla = new TeCla("D:\\Users\\jonas\\Documents\\GitHub\\GroovySanboxedLauncher\\test\\groovyScripts\\testfile")



def blub (String bla){
    println "I print $bla"
}


class TeCla{
    TeCla(){
        println "default"
    }

    TeCla(String s) {
        println "hi i am a constructor $s"
    }
    static int testInt = 20
}