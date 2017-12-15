package de.bloodworkxgaming.groovysandboxedlauncher.sandbox

import de.bloodworkxgaming.groovysandboxedlauncher.annotations.GSLOptional
import groovy.transform.CompileStatic

/**
 * Groovy class to assign the value based on the type of the parameter,
 *  not made in java to use the great groovy with closure and switch statements
 */
@CompileStatic
class DefaultValueAssigner {
    static void assignDefaultValue(GSLOptional optional, List<Object> objects, Class<?> paratype) {

        if (optional != null) {
            optional.with {
                switch (paratype) {
                    case int:
                        objects.add(defaultIntValue())
                        break
                    case short:
                        objects.add(defaultShortValue())
                        break
                    case byte:
                        objects.add(defaultByteValue())
                        break
                    case long:
                        objects.add(defaultLongValue())
                        break
                    case double:
                        objects.add(defaultDoubleValue())
                        break
                    case float:
                        objects.add(defaultFloatValue())
                        break
                    case boolean:
                        objects.add(defaultBooleanValue())
                        break
                    case char:
                        objects.add(defaultCharValue())
                        break
                    default:
                        objects.add(null)

                }
            }
        } else {
            switch (paratype) {
                case int:
                case short:
                case byte:
                case long:
                case double:
                case float:
                    objects.add(0)
                    break
                case boolean:
                    objects.add(false)
                    break
                case char:
                    objects.add('\u0000')
                    break
                default:
                    objects.add(null)
            }

        }
    }
}
