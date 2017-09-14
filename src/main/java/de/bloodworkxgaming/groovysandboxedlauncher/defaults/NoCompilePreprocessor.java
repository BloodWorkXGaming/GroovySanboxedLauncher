package de.bloodworkxgaming.groovysandboxedlauncher.defaults;

import de.bloodworkxgaming.groovysandboxedlauncher.data.GSLScriptFile;
import de.bloodworkxgaming.groovysandboxedlauncher.preprocessor.PreprocessorActionBase;

/**
 * Scripts with the
 * #nocompile
 * Preprocessor are getting totally ignored and are not being run
 * Syntax command bypasses the not loading of the script
 *
 * @author BloodWorkXGaming
 */
public class NoCompilePreprocessor extends PreprocessorActionBase {
    public static final String PREPROCESSOR_NAME = "nocompile";

    public NoCompilePreprocessor(String fileName, String preprocessorLine, int lineIndex) {
        super(fileName, preprocessorLine, lineIndex);
    }

    @Override
    public void executeActionOnFind(GSLScriptFile scriptFile) {
        scriptFile.setExecutionBlocked(true);
        scriptFile.setScriptCreationBlocked(true);
    }

    @Override
    public String getPreprocessorName() {
        return PREPROCESSOR_NAME;
    }
}
