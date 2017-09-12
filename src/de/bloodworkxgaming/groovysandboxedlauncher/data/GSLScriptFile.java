package de.bloodworkxgaming.groovysandboxedlauncher.data;

import de.bloodworkxgaming.groovysandboxedlauncher.preprocessor.IPreprocessor;
import de.bloodworkxgaming.groovysandboxedlauncher.sandbox.GroovySandboxedLauncher;
import groovy.lang.Script;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class GSLScriptFile {
    private File file;

    private Script script;
    private List<IPreprocessor> affectingPreprocessors = new ArrayList<>();
    private GroovySandboxedLauncher groovySandboxedLauncher;

    /** Priority which can be assigned to script, within the same priority the load order is affected by filename*/
    private int priority = 0;

    /** Loader name gets provided so that it can only load specifc scripts, not all of them*/
    private String loaderName = "crafttweaker";

    private boolean debugEnabled = false;
    private boolean executionBlocked = false;
    private boolean scriptCreationBlocked = false;
    private String scriptName;

    private boolean isValidGroovyFile;

    public GSLScriptFile(File file, String scriptName) {
        this.file = file;
        this.scriptName = scriptName;
        isValidGroovyFile = file.getName().endsWith(".groovy");
    }


    public void setPriority(int priority) {
        this.priority = priority;
    }

    public void setLoaderName(String loaderName) {
        this.loaderName = loaderName;
    }

    public void setDebugEnabled(boolean debugEnabled) {
        this.debugEnabled = debugEnabled;
    }

    public void setExecutionBlocked(boolean executionBlocked) {
        this.executionBlocked = executionBlocked;
    }

    public boolean isExecutionBlocked() {
        return executionBlocked;
    }

    public boolean isDebugEnabled() {
        return debugEnabled;
    }

    public boolean isScriptCreationBlocked() {
        return scriptCreationBlocked;
    }

    public void setScriptCreationBlocked(boolean scriptCreationBlocked) {
        this.scriptCreationBlocked = scriptCreationBlocked;
    }

    public String getLoaderName() {
        return loaderName;
    }

    public int getPriority() {
        return priority;
    }

    public String getName() {
        return scriptName;
    }

    public File getFile() {
        return file;
    }

    public boolean addAllPreprocessor(Collection<? extends IPreprocessor> c) {
        return affectingPreprocessors.addAll(c);
    }

    public boolean addPreprocessor(IPreprocessor iPreprocessor) {
        return affectingPreprocessors.add(iPreprocessor);
    }

    public List<IPreprocessor> getAffectingPreprocessors() {
        return affectingPreprocessors;
    }

    public boolean isValidGroovyFile() {
        return isValidGroovyFile;
    }

    @Override
    public String toString() {
        return "{[" + priority + ":" + loaderName + "]: " + getName() + "}";
    }

    public Script getScript() {
        return script;
    }

    public void setScript(Script script) {
        this.script = script;
    }
}
