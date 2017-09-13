package de.bloodworkxgaming.groovysandboxedlauncher.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * annotate a class with this annotation so that a object of this class is allowed to exist
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface GSLWhitelistClass {
}
