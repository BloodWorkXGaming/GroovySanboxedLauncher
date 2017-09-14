package de.bloodworkxgaming.testclasses;

import de.bloodworkxgaming.groovysandboxedlauncher.annotations.GSLOptional;
import de.bloodworkxgaming.groovysandboxedlauncher.annotations.GSLWhitelistConstructor;
import de.bloodworkxgaming.groovysandboxedlauncher.annotations.GSLWhitelistMember;

@GSLWhitelistConstructor
public class TestClass extends TestInterface {
    @GSLWhitelistMember
    public static int testParas(int a, @GSLOptional(defaultIntValue = -29) int b) {
        System.out.println("a = " + a);
        System.out.println("b = " + b);

        return a - b;
    }

    @Override
    public int getMagic() {
        return super.getMagic();
    }
}
