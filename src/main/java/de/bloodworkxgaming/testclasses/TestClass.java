package de.bloodworkxgaming.testclasses;

import de.bloodworkxgaming.groovysandboxedlauncher.annotations.GSLWhitelistConstructor;

@GSLWhitelistConstructor
public class TestClass extends TestInterface {
    @Override
    public int getMagic() {
        return super.getMagic();
    }
}
