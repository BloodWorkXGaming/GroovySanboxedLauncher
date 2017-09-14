package de.bloodworkxgaming.groovysandboxedlauncher.defaults;

import de.bloodworkxgaming.groovysandboxedlauncher.data.GSLScriptFile;
import de.bloodworkxgaming.groovysandboxedlauncher.preprocessor.PreprocessorActionBase;

/**
 * Preprocessor can be used as follows:
 * #loader loadername
 * Example:
 * #loader contenttweaker
 * This will make scripts only being loaded when the loader is specified to load
 * contenttweaker scripts
 * <p>
 * this defaults to being "crafttweaker" which is being called by CraftTweaker
 *
 * @author BloodWorkXGaming
 */
public class LoaderPreprocessor extends PreprocessorActionBase {
    public static final String PREPROCESSOR_NAME = "loader";
    private String loaderName;

    public LoaderPreprocessor(String fileName, String preprocessorLine, int lineIndex) {
        super(fileName, preprocessorLine, lineIndex);

        String s = preprocessorLine.substring(PREPROCESSOR_NAME.length());
        loaderName = s.trim();
    }

    @Override
    public void executeActionOnFind(GSLScriptFile scriptFile) {
        scriptFile.setLoaderName(loaderName);
    }

    @Override
    public String getPreprocessorName() {
        return PREPROCESSOR_NAME;
    }
}
