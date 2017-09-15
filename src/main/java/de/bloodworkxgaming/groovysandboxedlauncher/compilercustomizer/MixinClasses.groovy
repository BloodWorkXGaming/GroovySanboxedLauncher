package de.bloodworkxgaming.groovysandboxedlauncher.compilercustomizer

class MixinClasses implements IMixinProvider{
    @Override
    void mixIn() {
        println "Mixed in $this"
        Integer.metaClass.getDaysFromNow = { ->
            Calendar today = Calendar.instance
            today.add(Calendar.DAY_OF_MONTH, delegate as int)
            today.time
        }

    }
}
