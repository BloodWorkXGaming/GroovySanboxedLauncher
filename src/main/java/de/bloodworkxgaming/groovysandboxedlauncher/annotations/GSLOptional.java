package de.bloodworkxgaming.groovysandboxedlauncher.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotating a parameter with this class will it optional.
 * Optional means it is replacing the parameter that would have been there with null if it is a object or with the given values
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface GSLOptional {
    byte    defaultByteValue()      default 0;
    short   defaultShortValue()     default 0;
    int     defaultIntValue()       default 0;
    long    defaultLongValue()      default 0L;
    float   defaultFloatValue()     default 0.0f;
    double  defaultDoubleValue()    default 0.0d;
    char    defaultCharValue()      default '\u0000';
    boolean defaultBooleanValue()   default false;
}
