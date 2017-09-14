package de.bloodworkxgaming.groovysandboxedlauncher.compilercustomizer

import org.codehaus.groovy.transform.stc.GroovyTypeCheckingExtensionSupport

class OptionalAnnotationTypeChecker extends GroovyTypeCheckingExtensionSupport.TypeCheckingDSL{
    @Override
    Object run() {

        methodNotFound { receiver, name, argList, argTypes, call ->
            // receiver is the inferred type of the receiver
            // name is the name of the called method
            // argList is the list of arguments the method was called with
            // argTypes is the array of inferred types for each argument
            // call is the method call for which we couldn’t find a target method
            if (receiver==classNodeFor(String)
                    && name=='longueur'
                    && argList.size()==0) {
                handled = true
                return newMethod('longueur', classNodeFor(String))
            }
        }
        return null;
    }
}
