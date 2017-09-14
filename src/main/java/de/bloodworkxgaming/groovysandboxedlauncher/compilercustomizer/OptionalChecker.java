package de.bloodworkxgaming.groovysandboxedlauncher.compilercustomizer;

import de.bloodworkxgaming.groovysandboxedlauncher.sandbox.AnnotationManager;
import org.codehaus.groovy.ast.AnnotationNode;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.Parameter;
import org.codehaus.groovy.ast.expr.ArgumentListExpression;
import org.codehaus.groovy.ast.expr.MethodCall;
import org.codehaus.groovy.transform.stc.AbstractTypeCheckingExtension;
import org.codehaus.groovy.transform.stc.StaticTypeCheckingVisitor;

import java.lang.annotation.Annotation;
import java.util.List;

public class OptionalChecker extends AbstractTypeCheckingExtension {
    public OptionalChecker(final StaticTypeCheckingVisitor typeCheckingVisitor) {
        super(typeCheckingVisitor);
    }

    @Override
    public List<MethodNode> handleMissingMethod(ClassNode receiver, String name, ArgumentListExpression argumentList, ClassNode[] argumentTypes, MethodCall call) {
        List<MethodNode> methods = receiver.getMethods(name);

        methodLoop:
        for (MethodNode method : methods) {
            Parameter[] paras = method.getParameters();
            if (paras.length > argumentTypes.length){
                // checks whether the first few parameters are correct
                for (int i = 0; i < argumentTypes.length; i++) {
                    if (!canBeAsignedFrom(paras[i].getType(), argumentTypes[i])) continue methodLoop;
                }

                // whether the rest of the parameters are annotated
                for (int i = argumentTypes.length; i < paras.length; i++) {
                    List<AnnotationNode> annotations = paras[i].getAnnotations();
                    boolean hasMatch = false;
                    for (AnnotationNode annotation : annotations) {
                        for (Class<? extends Annotation> aClass : AnnotationManager.annotationsOptionalParameter) {
                            if (annotation.getClass().equals(aClass)) hasMatch = true;
                        }
                    }
                    if (!hasMatch) continue methodLoop;
                }

                System.out.println("Could make it a optional call if I would know how");
            }
        }

        return super.handleMissingMethod(receiver, name, argumentList, argumentTypes, call);
    }


    private boolean canBeAsignedFrom(ClassNode parameter, ClassNode objectThatIsGettingAssigned) {
        return parameter != null
                && (objectThatIsGettingAssigned == null
                || objectThatIsGettingAssigned.equals(parameter)
                || objectThatIsGettingAssigned.isDerivedFrom(parameter)
                || objectThatIsGettingAssigned.getAllInterfaces().contains(parameter));
    }
}

