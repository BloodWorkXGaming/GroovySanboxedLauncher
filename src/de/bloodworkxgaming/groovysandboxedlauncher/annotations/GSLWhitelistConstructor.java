package de.bloodworkxgaming.groovysandboxedlauncher.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotate a class with this so that a constructor Call of this class can be made
 * A object can still exist if obtained otherwise
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface GSLWhitelistConstructor {
}
