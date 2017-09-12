package de.bloodworkxgaming.groovysandboxedlauncher.Sandbox

import de.bloodworkxgaming.groovysandboxedlauncher.utils.StringUtils
import groovy.transform.CompileStatic
import org.kohsuke.groovy.sandbox.GroovyInterceptor
import org.kohsuke.groovy.sandbox.GroovyValueFilter

import static de.bloodworkxgaming.groovysandboxedlauncher.Sandbox.GroovySandboxedLauncher.DEBUG

@CompileStatic
@SuppressWarnings("UnnecessaryQualifiedReference")
class CustomValueFilter extends GroovyValueFilter{
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

        if (whitelistRegistry.isObjectExistenceWhitelisted(o?.getClass()) || AnnotationManager.checkHasClassAnnotation(o.getClass())){
            return super.filter(o)
        }else {
            throw new SecurityException("Object $o of type ${o?.getClass()?.getName()} is not allowed to exist")
        }
    }

    @Override
    Object onNewInstance(GroovyInterceptor.Invoker invoker, Class receiver, Object... args) throws Throwable {
        if (DEBUG) println("[CONSTRUCTOR] ${receiver.getName()} : ${Arrays.toString(args)}")

        if (whitelistRegistry.isConstructorWhitelisted(receiver) || AnnotationManager.checkHasConstructorAnnotation(receiver)){
            return super.onNewInstance(invoker, receiver, args)
        }else {
            throw new SecurityException("Constructor of $receiver is not allowed to be called")
        }

    }

    @Override
    Object onMethodCall(GroovyInterceptor.Invoker invoker, Object receiver, String method, Object... args) throws Throwable {
        if (DEBUG) println("[METHOD] ${receiver.getClass().getName()}.${method.toString()}(${Arrays.toString(args)})")

        if (whitelistRegistry.isMethodWhitelisted(receiver.getClass(), method) || AnnotationManager.checkHasMethodAnnotation(receiver.getClass(), method)){
            return super.onMethodCall(invoker, receiver, method, args)
        }else {
            throw new SecurityException("method ${receiver.getClass().getName()}.$method is not allowed to be called")
        }
    }

    @Override
    Object onStaticCall(GroovyInterceptor.Invoker invoker, Class receiver, String method, Object... args) throws Throwable {
        if (DEBUG) println("[STATIC METHOD] ${receiver.getName()}.${method.toString()}(${Arrays.toString(args)})")

        if (whitelistRegistry.isMethodWhitelisted(receiver, method) || AnnotationManager.checkHasMethodAnnotation(receiver, method)){
            return super.onMethodCall(invoker, receiver, method, args)
        }else {
            throw new SecurityException("static method  ${receiver.getName()}.$method is not allowed to be called")
        }
    }

    @Override
    Object onGetProperty(GroovyInterceptor.Invoker invoker, Object receiver, String property) throws Throwable {
        Class<?> clazz
        if (receiver instanceof Class<?>){
            clazz = receiver as Class<?>
        } else {
            clazz = receiver.getClass()
        }

        if (DEBUG) println("[PROPERTY GET] ${clazz}.$property")


        if (whitelistRegistry.isFieldWhitelisted(clazz, property) || AnnotationManager.checkHasFieldAnnotation(clazz, property) || checkImplicitFieldWhitelisted(clazz, property, true)){
            return super.onGetProperty(invoker, receiver, property)
        }else {
            throw new SecurityException("get property ${receiver.getClass().getName()}.$property is not allowed to be called")
        }
    }

    @Override
    Object onSetProperty(GroovyInterceptor.Invoker invoker, Object receiver, String property, Object value) throws Throwable {
        Class<?> clazz
        if (receiver instanceof Class<?>){
            clazz = receiver as Class<?>
        } else {
            clazz = receiver.getClass()
        }

        if (DEBUG) println("[PROPERTY SET] ${clazz.getName()} : $property")

        if (whitelistRegistry.isFieldWhitelisted(clazz, property) || AnnotationManager.checkHasFieldAnnotation(clazz, property) || checkImplicitFieldWhitelisted(clazz, property, false)){
            return super.onSetProperty(invoker, receiver, property, value)
        }else {
            throw new SecurityException("set property ${receiver.getClass().getName()}.$property is not allowed to be called")
        }
    }

    @Override
    void onSuperConstructor(GroovyInterceptor.Invoker invoker, Class receiver, Object... args) throws Throwable {
        if (DEBUG) println("[SUPER CONSTRUCTOR] ${receiver.getName()} : ${Arrays.toString(args)}")

        if (whitelistRegistry.isConstructorWhitelisted(receiver) || AnnotationManager.checkHasConstructorAnnotation(receiver)){
            super.onSuperConstructor(invoker, receiver, args)
        }else {
            throw new SecurityException("Super Constructor of $receiver is not allowed to be called")
        }
    }

    @Override
    Object onSuperCall(GroovyInterceptor.Invoker invoker, Class senderType, Object receiver, String method, Object... args) throws Throwable {
        if (DEBUG) println("[SUPER METHOD] ${receiver.getClass().getSuperclass().getName()}.${method.toString()}(${Arrays.toString(args)})")
        println "Sendertype $senderType"

        if (whitelistRegistry.isMethodWhitelisted(receiver.getClass().getSuperclass(), method) || AnnotationManager.checkHasMethodAnnotation(receiver.getClass().getSuperclass(), method)){
            return super.onSuperCall(invoker, senderType, receiver, method, args)
        }else {
            throw new SecurityException("super method ${receiver.getClass().getSuperclass().getName()}.$method is not allowed to be called")
        }
    }

    // can maybe fail on inverted whitelist
    private boolean checkImplicitFieldWhitelisted(Class<?> clazz, String fieldName, boolean isGetter){
        String method
        if (isGetter){
            method = "get${StringUtils.capitalize(fieldName)}"
        }else {
            method = "set${StringUtils.capitalize(fieldName)}"
        }

        return enableImplicitPropertySupport && (whitelistRegistry.isMethodWhitelisted(clazz, method) || AnnotationManager.checkHasMethodAnnotation(clazz, method))
    }
}
