package de.bloodworkxgaming.groovysandboxedlauncher.preprocessor;

import de.bloodworkxgaming.groovysandboxedlauncher.data.GSLScriptFile;

public interface IPreprocessor {
    String getPreprocessorName();

    /**
     * Gets executed directly on find
     */
    void executeActionOnFind(GSLScriptFile scriptFile);
    
    /**
     * Gets executed after all preprocessor actions have been collected
     */
    void executeActionOnFinish(GSLScriptFile scriptFile);
    
    String getPreprocessorLine();
    
    String getFileName();
    
    int getLineIndex();
}
