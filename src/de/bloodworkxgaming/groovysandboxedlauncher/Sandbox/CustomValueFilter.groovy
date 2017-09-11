package de.bloodworkxgaming.groovysandboxedlauncher.Sandbox

import groovy.transform.CompileStatic
import org.kohsuke.groovy.sandbox.GroovyInterceptor
import org.kohsuke.groovy.sandbox.GroovyValueFilter

import static de.bloodworkxgaming.groovysandboxedlauncher.Sandbox.GroovySandboxedLauncher.DEBUG

@CompileStatic
@SuppressWarnings("UnnecessaryQualifiedReference")
class CustomValueFilter extends GroovyValueFilter{
    WhitelistRegistry whitelistRegistry

    CustomValueFilter(WhitelistRegistry whitelistRegistry) {
        this.whitelistRegistry = whitelistRegistry
    }

    @Override
    Object filter(Object o) {
        if (DEBUG) println("[OBJECT] ${o?.getClass()?.getName()}")

        if (whitelistRegistry.isObjectExistenceWhitelisted(o?.getClass())){
            return super.filter(o)
        }else {
            throw new SecurityException("Object $o of type ${o?.getClass()?.getName()} is not allowed to exist")
        }
    }

    @Override
    Object onNewInstance(GroovyInterceptor.Invoker invoker, Class receiver, Object... args) throws Throwable {
        if (DEBUG) println("[Constructor] ${receiver.getName()} : ${Arrays.toString(args)}")

        if (whitelistRegistry.isConstructorWhitelisted(receiver)){
            return super.onNewInstance(invoker, receiver, args)
        }else {
            throw new SecurityException("Constructor of $receiver is not allowed to be called")
        }

    }

    @Override
    Object onMethodCall(GroovyInterceptor.Invoker invoker, Object receiver, String method, Object... args) throws Throwable {
        if (DEBUG) println("[METHOD] ${receiver.getClass().getName()}.${method.toString()}(${Arrays.toString(args)})")

        if (whitelistRegistry.isMethodWhitelisted(receiver.getClass(), method)){
            return super.onMethodCall(invoker, receiver, method, args)
        }else {
            throw new SecurityException("method ${receiver.getClass().getName()}.$method is not allowed to be called")
        }
    }

    @Override
    Object onStaticCall(GroovyInterceptor.Invoker invoker, Class receiver, String method, Object... args) throws Throwable {
        if (DEBUG) println("[STATIC METHOD] ${receiver.getName()}.${method.toString()}(${Arrays.toString(args)})")

        if (whitelistRegistry.isMethodWhitelisted(receiver, method)){
            return super.onMethodCall(invoker, receiver, method, args)
        }else {
            throw new SecurityException("static method  ${receiver.getName()}.$method is not allowed to be called")
        }
    }

    @Override
    Object onGetProperty(GroovyInterceptor.Invoker invoker, Object receiver, String property) throws Throwable {
        if (DEBUG) println("[PROPERTY GET] ${receiver.getClass().getName()} : $property")

        if (whitelistRegistry.isFieldWhitelisted(receiver.getClass(), property)){
            return super.onGetProperty(invoker, receiver, property)
        }else {
            throw new SecurityException("get property ${receiver.getClass().getName()}.$property is not allowed to be called")
        }
    }

    @Override
    Object onSetProperty(GroovyInterceptor.Invoker invoker, Object receiver, String property, Object value) throws Throwable {
        if (DEBUG) println("[PROPERTY SET] ${receiver.getClass().getName()} : $property")

        if (whitelistRegistry.isFieldWhitelisted(receiver.getClass(), property)){
            return super.onSetProperty(invoker, receiver, property, value)
        }else {
            throw new SecurityException("set property ${receiver.getClass().getName()}.$property is not allowed to be called")
        }
    }
}
