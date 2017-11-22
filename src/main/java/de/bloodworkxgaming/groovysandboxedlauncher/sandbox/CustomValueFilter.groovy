package de.bloodworkxgaming.groovysandboxedlauncher.sandbox

import de.bloodworkxgaming.groovysandboxedlauncher.utils.StringUtils
import groovy.transform.CompileStatic
import org.kohsuke.groovy.sandbox.GroovyValueFilter
import org.kohsuke.groovy.sandbox.impl.Invoker

import static de.bloodworkxgaming.groovysandboxedlauncher.sandbox.GroovySandboxedLauncher.DEBUG

@CompileStatic
class CustomValueFilter extends GroovyValueFilter {
    public boolean enableImplicitPropertySupport = true

    private WhitelistRegistry whitelistRegistry
    private FunctionKnower functionKnower

    CustomValueFilter(WhitelistRegistry whitelistRegistry, FunctionKnower functionKnower) {
        this.whitelistRegistry = whitelistRegistry
        this.functionKnower = functionKnower
    }

    @Override
    Object filter(Object o) {
        if (DEBUG) println("[OBJECT] ${o?.getClass()?.getName()}")

        if (whitelistRegistry.isObjectExistenceWhitelisted(o?.getClass()) || AnnotationManager.checkHasClassAnnotation(o.getClass())) {
            return super.filter(o)
        } else {
            throw new SecurityException("Object $o of type ${o?.getClass()?.getName()} is not allowed to exist")
        }
    }

    @Override
    Object onNewInstance(Invoker invoker, Class receiver, Object... args) throws Throwable {
        if (DEBUG) println("[CONSTRUCTOR] ${receiver.getName()} : ${Arrays.toString(args)}")

        if (whitelistRegistry.isConstructorWhitelisted(receiver) || AnnotationManager.checkHasConstructorAnnotation(receiver)) {
            return super.onNewInstance(invoker, receiver, args)
        } else {
            throw new SecurityException("Constructor of $receiver is not allowed to be called")
        }

    }

    @Override
    Object onMethodCall(Invoker invoker, Object receiver, String method, Object... args) throws Throwable {
        Class<?> clazz
        if (receiver instanceof Class<?>) {
            clazz = receiver as Class<?>
        } else {
            clazz = receiver.getClass()
        }

        if (DEBUG) println("[METHOD] ${clazz.getName()}.${method.toString()}(${Arrays.toString(args)})")

        // Transforming println calls into the own logger commands
        if ((method == "println" || method == "print") && GroovyObject.isAssignableFrom(clazz) && args.length <= 1) {
            if (DEBUG) GroovySandboxedLauncher.LOGGER.logInfo("Transforming calls from $clazz with params $args")
            Object[] newArgs = new Object[args.length + 1]
            newArgs[0] = clazz.name
            for (int i = 0; i < args.length; i++) {
                newArgs[i + 1] = args[i]
            }
            return super.onMethodCall(invoker, GroovySandboxedLauncher.LOGGER, "logInfo", newArgs)
        }

        // checking for optional parameters and then changing them if needed
        args = OptionalParasManager.checkParas(clazz, method, args)



        if (whitelistRegistry.isMethodWhitelisted(clazz, method, objectToClassArray(args)) || AnnotationManager.checkHasMethodAnnotation(clazz, method) || checkImplicitGetterWhitelisted(clazz, method)) {
            return super.onMethodCall(invoker, receiver, method, args)
        } else {
            throw new SecurityException("method ${clazz.getName()}.$method(${StringUtils.classArrayToStringArray(objectToClassArray(args))}) is not allowed to be called\n"
                    + "params: $args")
        }
    }

    @Override
    Object onStaticCall(Invoker invoker, Class receiver, String method, Object... args) throws Throwable {
        if (DEBUG) println("[STATIC METHOD] ${receiver.getName()}.${method.toString()}(${Arrays.toString(args)})")

        args = OptionalParasManager.checkParas(receiver, method, args)

        if (whitelistRegistry.isMethodWhitelisted(receiver, method) || AnnotationManager.checkHasMethodAnnotation(receiver, method) || checkImplicitGetterWhitelisted(receiver, method)) {
            return super.onMethodCall(invoker, receiver, method, args)
        } else {
            throw new SecurityException("static method  ${receiver.getName()}.$method(${StringUtils.classArrayToStringArray(objectToClassArray(args))}) is not allowed to be called")
        }
    }

    @Override
    Object onGetProperty(Invoker invoker, Object receiver, String property) throws Throwable {
        Class<?> clazz
        if (receiver instanceof Class<?>) {
            clazz = receiver as Class<?>
        } else {
            clazz = receiver.getClass()
        }

        if (DEBUG) println("[PROPERTY GET] ${clazz}.$property")


        if (whitelistRegistry.isFieldWhitelisted(clazz, property) || AnnotationManager.checkHasFieldAnnotation(clazz, property) || checkImplicitFieldWhitelisted(clazz, property, true)) {
            return super.onGetProperty(invoker, receiver, property)
        } else {
            throw new SecurityException("get property ${clazz.getName()}.$property is not allowed to be called")
        }
    }

    @Override
    Object onSetProperty(Invoker invoker, Object receiver, String property, Object value) throws Throwable {
        Class<?> clazz
        if (receiver instanceof Class<?>) {
            clazz = receiver as Class<?>
        } else {
            clazz = receiver.getClass()
        }

        if (DEBUG) println("[PROPERTY SET] ${clazz.getName()} : $property")

        if (whitelistRegistry.isFieldWhitelisted(clazz, property) || AnnotationManager.checkHasFieldAnnotation(clazz, property) || checkImplicitFieldWhitelisted(clazz, property, false)) {
            return super.onSetProperty(invoker, receiver, property, value)
        } else {
            throw new SecurityException("set property ${receiver.getClass().getName()}.$property is not allowed to be called")
        }
    }

    @Override
    void onSuperConstructor(Invoker invoker, Class receiver, Object... args) throws Throwable {
        if (DEBUG) println("[SUPER CONSTRUCTOR] ${receiver.getName()} : ${Arrays.toString(args)}")

        if (whitelistRegistry.isConstructorWhitelisted(receiver) || AnnotationManager.checkHasConstructorAnnotation(receiver)) {
            super.onSuperConstructor(invoker, receiver, args)
        } else {
            throw new SecurityException("Super Constructor of $receiver is not allowed to be called")
        }
    }

    @Override
    Object onSuperCall(Invoker invoker, Class senderType, Object receiver, String method, Object... args) throws Throwable {
        if (DEBUG) println("[SUPER METHOD] ${receiver.getClass().getSuperclass().getName()}.${method.toString()}(${Arrays.toString(args)})")
        println "Sendertype $senderType"

        if (whitelistRegistry.isMethodWhitelisted(receiver.getClass().getSuperclass(), method, objectToClassArray(args)) || AnnotationManager.checkHasMethodAnnotation(receiver.getClass().getSuperclass(), method)) {
            return super.onSuperCall(invoker, senderType, receiver, method, args)
        } else {
            throw new SecurityException("super method ${receiver.getClass().getSuperclass().getName()}.$method is not allowed to be called")
        }
    }

    @Override
    Object onGetAttribute(Invoker invoker, Object receiver, String attribute) throws Throwable {
        Class<?> clazz
        if (receiver instanceof Class<?>) {
            clazz = receiver as Class<?>
        } else {
            clazz = receiver.getClass()
        }

        if (DEBUG) println("[ATTRIBUTE GET] ${clazz}.$attribute")


        if (whitelistRegistry.isFieldWhitelisted(clazz, attribute) || AnnotationManager.checkHasFieldAnnotation(clazz, attribute)) {
            return super.onGetAttribute(invoker, receiver, attribute)
        } else {
            throw new SecurityException("get attribute ${clazz.getName()}.$attribute is not allowed to be called")
        }
    }

    @Override
    Object onSetAttribute(Invoker invoker, Object receiver, String attribute, Object value) throws Throwable {
        Class<?> clazz
        if (receiver instanceof Class<?>) {
            clazz = receiver as Class<?>
        } else {
            clazz = receiver.getClass()
        }

        if (DEBUG) println("[ATTRIBUTE SET] ${clazz}.$attribute")


        if (whitelistRegistry.isFieldWhitelisted(clazz, attribute) || AnnotationManager.checkHasFieldAnnotation(clazz, attribute)) {
            return super.onSetAttribute(invoker, receiver, attribute, value)
        } else {
            throw new SecurityException("set attribute ${clazz.getName()}.$attribute is not allowed to be called")
        }


    }

    // can maybe fail on inverted whitelist
    private boolean checkImplicitFieldWhitelisted(Class<?> clazz, String fieldName, boolean isGetter) {
        String method
        String methodIs
        if (isGetter) {
            method = "get${StringUtils.capitalize(fieldName)}"
            methodIs = "is${StringUtils.capitalize(fieldName)}"
        } else {
            method = "set${StringUtils.capitalize(fieldName)}"
        }

        return enableImplicitPropertySupport &&
                (whitelistRegistry.isMethodWhitelisted(clazz, method) || AnnotationManager.checkHasMethodAnnotation(clazz, method) ||
                        methodIs != null && ( whitelistRegistry.isMethodWhitelisted(clazz, methodIs) || AnnotationManager.checkHasMethodAnnotation(clazz, methodIs))
                )
    }

    private boolean checkImplicitGetterWhitelisted(Class<?> clazz, String method) {
        String fieldName

        if (method.startsWith("get") || method.startsWith("set")) {
            fieldName = method.substring(3)
        }

        if (method.startsWith("is")) {
            fieldName = method.substring(2)
        }

        return fieldName != null && enableImplicitPropertySupport && (whitelistRegistry.isFieldWhitelisted(clazz, fieldName) || AnnotationManager.checkHasFieldAnnotation(clazz, fieldName)
                || whitelistRegistry.isFieldWhitelisted(clazz, StringUtils.lowercaseFirstLetter(fieldName)) || AnnotationManager.checkHasFieldAnnotation(clazz, StringUtils.lowercaseFirstLetter(fieldName)))

    }

    static Class<?>[] objectToClassArray(Object[] objects) {
        Class<?>[] strings = new Class<?>[objects.length]
        for (int i = 0; i < objects.length; i++) {
            strings[i] = objects[i]?.getClass()
        }

        return strings
    }
}
