package groovyScripts

import de.bloodworkxgaming.groovysandboxedlauncher.annotations.GSLWhitelistMember
import de.bloodworkxgaming.groovysandboxedlauncher.utils.FileUtils

println "hi".setNBT("blabla")
def bla = [29, 20]

println(bla)

def test = 20;
println FileUtils.test("blib");

blub("lalal");



// println String.valueOf(20)

// def f = new File("D:\\Users\\jonas\\Documents\\GitHub\\GroovySanboxedLauncher\\test\\groovyScripts\\testscr.groovy")
// println(f.toString())

def blub (String bla){
    println "I print $bla"
}