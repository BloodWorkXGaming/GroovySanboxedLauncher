package de.bloodworkxgaming.groovysandboxedlauncher.defaults;

import de.bloodworkxgaming.groovysandboxedlauncher.data.GSLScriptFile;
import de.bloodworkxgaming.groovysandboxedlauncher.preprocessor.PreprocessorActionBase;

/**
 * Adding the
 * #debug
 * Preprocessor will make this script generate the .class file which it normally just has under the hood
 *
 * @author BloodWorkXGaming
 */
public class DebugPreprocessor extends PreprocessorActionBase {
    public static final String PREPROCESSOR_NAME = "debug";
    
    public DebugPreprocessor(String fileName, String preprocessorLine, int lineIndex) {
        super(fileName, preprocessorLine, lineIndex);
    }
    
    @Override
    public void executeActionOnFind(GSLScriptFile scriptFile) {
        scriptFile.setDebugEnabled(true);
    }
    
    @Override
    public String getPreprocessorName() {
        return PREPROCESSOR_NAME;
    }
}
