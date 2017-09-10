package de.bloodworkxgaming.groovysandboxedlauncher.Sandbox

import groovy.transform.CompileStatic
import org.kohsuke.groovy.sandbox.GroovyInterceptor
import org.kohsuke.groovy.sandbox.GroovyValueFilter

@CompileStatic
@SuppressWarnings("UnnecessaryQualifiedReference")
class CustomValueFilter extends GroovyValueFilter{
    WhitelistRegistry whitelistRegistry

    CustomValueFilter(WhitelistRegistry whitelistRegistry) {
        this.whitelistRegistry = whitelistRegistry
    }

    @Override
    Object filter(Object o) {
        println("[Object] ${o?.getClass()?.getName()}")

        if (whitelistRegistry.isObjectExistenceWhitelisted(o?.getClass())){
            return super.filter(o)
        }else {
            throw new SecurityException("Object $o of type ${o?.getClass()?.getName()} is not allowed to exist")
        }
    }

    @Override
    Object onNewInstance(GroovyInterceptor.Invoker invoker, Class receiver, Object... args) throws Throwable {
        println("[Constructor] ${receiver.getName()} : ${Arrays.toString(args)}")

        if (whitelistRegistry.isConstructorWhitelisted(receiver)){
            return super.onNewInstance(invoker, receiver, args)
        }else {
            throw new SecurityException("Constructor of $receiver is not allowed to be called")
        }

    }

    @Override
    Object onMethodCall(GroovyInterceptor.Invoker invoker, Object receiver, String method, Object... args) throws Throwable {
        println("[Non Static] ${receiver.getClass().getName()} : ${method.toString()} >  ${Arrays.toString(args)}")

        if (whitelistRegistry.isMethodWhitelisted(receiver.getClass(), method)){
            return super.onMethodCall(invoker, receiver, method, args)
        }else {
            throw new SecurityException("method  ${receiver.getClass().getName()}.$method is not allowed to be called")
        }
    }

    @Override
    Object onStaticCall(GroovyInterceptor.Invoker invoker, Class receiver, String method, Object... args) throws Throwable {
        println("[Static] ${receiver.getName()} : ${method.toString()} >  ${Arrays.toString(args)}")

        if (whitelistRegistry.isMethodWhitelisted(receiver, method)){
            return super.onMethodCall(invoker, receiver, method, args)
        }else {
            throw new SecurityException("static method  ${receiver.getName()}.$method is not allowed to be called")
        }
    }
}
