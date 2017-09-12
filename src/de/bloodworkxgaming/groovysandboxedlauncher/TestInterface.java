package de.bloodworkxgaming.groovysandboxedlauncher;

import de.bloodworkxgaming.groovysandboxedlauncher.annotations.GSLWhitelistConstructor;
import de.bloodworkxgaming.groovysandboxedlauncher.annotations.GSLWhitelistMember;

@GSLWhitelistConstructor
public class TestInterface {
    @GSLWhitelistMember
    public int magic = 200;

    @GSLWhitelistMember
    public int getMagic() {
        return 2;
    }

}
