package de.bloodworkxgaming.groovysandboxedlauncher.preprocessor;

@FunctionalInterface
public interface PreprocessorFactory<R extends IPreprocessor> {
    R createPreprocessor(String fileName, String preprocessorLine, int lineIndex);
}
