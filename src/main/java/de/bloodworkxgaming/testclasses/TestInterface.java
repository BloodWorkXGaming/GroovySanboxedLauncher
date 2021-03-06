package de.bloodworkxgaming.testclasses;

import de.bloodworkxgaming.groovysandboxedlauncher.annotations.GSLOptional;
import de.bloodworkxgaming.groovysandboxedlauncher.annotations.GSLWhitelistConstructor;
import de.bloodworkxgaming.groovysandboxedlauncher.annotations.GSLWhitelistMember;
import groovy.lang.Closure;

@GSLWhitelistConstructor
public class TestInterface {
    // public int magic = 200;
    public static int testInt = 99;
    public static int[] testArray = {29, 20, 19};

    @GSLWhitelistMember
    public int getMagic() {
        return 2;
    }

    @GSLWhitelistMember
    public void para(int b, @GSLOptional float c){
        System.out.println(b + " --- " + c);
    }

    void with(int bla) {
        System.out.println("bla = " + bla);
    }

    void with(Closure closure) {
        closure.run();
    }

}
