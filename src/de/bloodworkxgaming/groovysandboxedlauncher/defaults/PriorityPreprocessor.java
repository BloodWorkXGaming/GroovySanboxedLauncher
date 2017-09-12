package de.bloodworkxgaming.groovysandboxedlauncher.defaults;

import de.bloodworkxgaming.groovysandboxedlauncher.data.GSLScriptFile;
import de.bloodworkxgaming.groovysandboxedlauncher.preprocessor.PreprocessorActionBase;

/**
 * Preprocessor can be used as follows:
 * #priority number
 * The higher the number, the earlier it is getting executed
 * Scripts with the same priority get sorted alphabetically
 *
 * @author BloodWorkXGaming
 */
public class PriorityPreprocessor extends PreprocessorActionBase {
    public static final String PREPROCESSOR_NAME = "priority";
    private Integer priority;
    
    public PriorityPreprocessor(String fileName, String preprocessorLine, int lineIndex) {
        super(fileName, preprocessorLine, lineIndex);
        
        String s = preprocessorLine.substring(PREPROCESSOR_NAME.length()).trim();
        
        try {
            priority = Integer.parseInt(s);
        }catch(NumberFormatException e){
            priority = null;
        }

    }
    
    @Override
    public void executeActionOnFind(GSLScriptFile scriptFile) {
        if (priority != null) scriptFile.setPriority(priority);
    }
    
    @Override
    public String getPreprocessorName() {
        return PREPROCESSOR_NAME;
    }
}
