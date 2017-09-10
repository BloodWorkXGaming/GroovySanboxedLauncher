package de.bloodworkxgaming.groovysandboxedlauncher.data;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ScriptPathConfig {
    private List<File> scriptPathRoots = new ArrayList<>();

    public void registerScriptPathRoots(String file){
        scriptPathRoots.add(new File(file));
    }
    public void registerScriptPathRoots(File file){
        scriptPathRoots.add(file);
    }

    /**
     * Gets the script path of where all the script are located
     */
    public String[] getScriptPathRootStrings(){
        String[] tempArray  = new String[scriptPathRoots.size()];

        for (int i = 0; i < tempArray.length; i++){
            if (scriptPathRoots.get(i) != null) {
                tempArray[i] = scriptPathRoots.get(i).getAbsolutePath();
            }
        }

        return tempArray;
    }

    public List<File> getScriptPathRoots() {
        return scriptPathRoots;
    }
}
