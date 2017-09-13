package de.bloodworkxgaming.groovysandboxedlauncher.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotate a field or a method with this annotation to whitelist it so that the groovy sandbox has access to it.
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface GSLWhitelistMember {
}
