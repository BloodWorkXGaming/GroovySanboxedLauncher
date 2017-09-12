package de.bloodworkxgaming.groovysandboxedlauncher.compilercustomizer

import de.bloodworkxgaming.groovysandboxedlauncher.annotations.GSLWhitelistClass
import de.bloodworkxgaming.groovysandboxedlauncher.annotations.GSLWhitelistConstructor
import de.bloodworkxgaming.groovysandboxedlauncher.annotations.GSLWhitelistMember
import org.codehaus.groovy.ast.AnnotationNode
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.classgen.GeneratorContext
import org.codehaus.groovy.control.CompilePhase
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.control.customizers.CompilationCustomizer

import java.lang.annotation.Annotation

class ClassFunctionWhitelistAnnotatorTransformer extends CompilationCustomizer {

    ClassFunctionWhitelistAnnotatorTransformer() {
        super(CompilePhase.CANONICALIZATION)
    }

    @Override
    void call(SourceUnit source, GeneratorContext context, ClassNode classNode) {
        addAnnotationToClass(classNode, GSLWhitelistConstructor)
        addAnnotationToClass(classNode, GSLWhitelistClass)

        // Adds the whitelist annotation to any self created field and method to be able to call it from the own scripts
        classNode?.methods?.each {
            for (anno in it.getAnnotations()) {
                if (anno.getClassNode().getTypeClass() == GSLWhitelistMember) {
                    return
                }
            }
            it.addAnnotation(new AnnotationNode(new ClassNode(GSLWhitelistMember)))
        }

        classNode?.properties?.each {
            for (anno in it.getAnnotations()) {
                if (anno.getClassNode().getTypeClass() == GSLWhitelistMember) {
                    return
                }
            }
            it.addAnnotation(new AnnotationNode(new ClassNode(GSLWhitelistMember)))
        }

        classNode?.fields?.each {
            for (anno in it.getAnnotations()) {
                if (anno.getClassNode().getTypeClass() == GSLWhitelistMember) {
                    return
                }
            }
            it.addAnnotation(new AnnotationNode(new ClassNode(GSLWhitelistMember)))
        }

        classNode?.innerClasses?.each {
            addAnnotationToClass(it, GSLWhitelistConstructor)
            addAnnotationToClass(it, GSLWhitelistClass)
        }
    }

    private static void addAnnotationToClass(ClassNode node, Class<? extends Annotation> annotation){
        for (anno in node.getAnnotations()) {
            if (anno.getClassNode().getTypeClass() == annotation) {
                return
            }
        }

        node.addAnnotation(new AnnotationNode(new ClassNode(annotation)))
    }
}
