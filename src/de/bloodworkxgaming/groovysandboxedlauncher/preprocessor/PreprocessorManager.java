package de.bloodworkxgaming.groovysandboxedlauncher.preprocessor;

import de.bloodworkxgaming.groovysandboxedlauncher.data.GSLScriptFile;
import de.bloodworkxgaming.groovysandboxedlauncher.defaults.DebugPreprocessor;
import de.bloodworkxgaming.groovysandboxedlauncher.defaults.LoaderPreprocessor;
import de.bloodworkxgaming.groovysandboxedlauncher.defaults.NoRunPreprocessor;
import de.bloodworkxgaming.groovysandboxedlauncher.defaults.PriorityPreprocessor;
import de.bloodworkxgaming.groovysandboxedlauncher.events.EventList;
import de.bloodworkxgaming.groovysandboxedlauncher.events.IGSLEvent;
import de.bloodworkxgaming.groovysandboxedlauncher.utils.StringUtils;

import java.io.*;
import java.util.*;

/**
 * @author BloodWorkXGaming
 */
public class PreprocessorManager {
    public static final ScriptFileComparator SCRIPT_FILE_COMPARATOR = new ScriptFileComparator();
    public static final String PREPROCESSOR_START = "//::";
    
    /** List of all event subscribers*/
    public final EventList<GSLScriptFile> SCRIPT_LOAD_EVENT_EVENT_LIST = new EventList<>();
    
    /** This registry is filled with dummy events that are callable */
    private HashMap<String, PreprocessorFactory> registeredPreprocessorActions = new HashMap<>();
    
    // file > action event
    public HashMap<String, List<IPreprocessor>> preprocessorActionsPerFile = new HashMap<>();
    
    public void registerPreprocessorAction(String name, PreprocessorFactory preprocessorFactory){
        registeredPreprocessorActions.put(name, preprocessorFactory);
    }
    
    /**
     * Cleans up before being able to run again
     */
    public void clean(){
        preprocessorActionsPerFile.clear();
    }
    
    /**
     * Checks the given line for preprocessors
     * @param scriptFile file which is being checked
     * @param line Line to check
     * @param lineIndex index of the file in the current line
     * @return returns whether it found a preprocessor or not
     */
    private IPreprocessor checkLine(GSLScriptFile scriptFile, String line, int lineIndex){
        if (line == null) return null;
        String s = line.trim();

        if (s.startsWith(PREPROCESSOR_START)){
            s = s.substring(4).trim();

            String[] splits = s.split(" ");
            if (splits.length > 0){
                PreprocessorFactory preprocessorFactory = registeredPreprocessorActions.get(splits[0]);

                if (preprocessorFactory != null){
                    IPreprocessor preprocessor = preprocessorFactory.createPreprocessor(scriptFile.getName(), s, lineIndex);

                    preprocessor.executeActionOnFind(scriptFile);
                    addPreprocessorToFileMap(scriptFile.getName(), preprocessor);
                    return preprocessor;
                }
            }
        }
        
        return null;
    }
    
    /**
     * Adds the preprocessor Command to the map
     * @param filename Key for map
     * @param preprocessor Value to add to map
     */
    private void addPreprocessorToFileMap(String filename, IPreprocessor preprocessor){
        List<IPreprocessor> list = preprocessorActionsPerFile.getOrDefault(filename, new ArrayList<>());
        list.add(preprocessor);

        preprocessorActionsPerFile.put(filename, list);
    }
    
    /**
     * Checks the given GSLScriptFile for preprocessors
     * @param scriptFile ScriptFile object of file to check,
     *                   contains all important information about streams and names
     */
    public List<IPreprocessor> checkFileForPreprocessors(GSLScriptFile scriptFile) {
        List<IPreprocessor> preprocessorList = new ArrayList<>();
        
        String filename = scriptFile.getName();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(scriptFile.getFile()), "UTF-8"))) {
            int lineIndex = -1;
            for(String line; (line = br.readLine()) != null; ) {

                lineIndex++;
                IPreprocessor preprocessor = checkLine(scriptFile, line, lineIndex);
                if (preprocessor != null){
                    preprocessorList.add(preprocessor);
                }

            }
        } catch (IOException e) {
            System.out.println("Could not read the preprocessors in " + filename);
            e.printStackTrace();
        }

        executePostActions(scriptFile);
        return preprocessorList;
    }

    public void checkFilesForPreprocessors(List<GSLScriptFile> scriptFiles){
        for (GSLScriptFile scriptFile : scriptFiles) {
            scriptFile.addAllPreprocessor(checkFileForPreprocessors(scriptFile));
        }
    }

    /**
     * Actions which are getting called after all preprocessor lines have been collected
     * @param scriptFile scriptFile which is being affected.
     *                   Changing this will affect the way script are getting loaded
     */
    private void executePostActions(GSLScriptFile scriptFile){
        for(Map.Entry<String, List<IPreprocessor>> stringListEntry : preprocessorActionsPerFile.entrySet()) {
            for(IPreprocessor preprocessor : stringListEntry.getValue()) {
                preprocessor.executeActionOnFinish(scriptFile);
            }
        }
    }
    
    public static void registerOwnPreprocessors(PreprocessorManager manager){
        manager.registerPreprocessorAction("debug", DebugPreprocessor::new);
        manager.registerPreprocessorAction("norun", NoRunPreprocessor::new);
        manager.registerPreprocessorAction("loader", LoaderPreprocessor::new);
        manager.registerPreprocessorAction("priority", PriorityPreprocessor::new);
    }

    public void registerLoadEventHandler(IGSLEvent<GSLScriptFile> handler){
        SCRIPT_LOAD_EVENT_EVENT_LIST.registerEvent(handler);
    }
    
    public void postLoadEvent(GSLScriptFile event){
        SCRIPT_LOAD_EVENT_EVENT_LIST.callOnEach(event);
    }
    
    public static class ScriptFileComparator implements Comparator<GSLScriptFile>, Serializable {
        @Override
        public int compare(GSLScriptFile o1, GSLScriptFile o2) {
            int compare = Integer.compare(o2.getPriority(), o1.getPriority());
            return compare == 0 ? o1.getName().compareToIgnoreCase(o2.getName()) : compare;
        }
    }
}
