package de.bloodworkxgaming.testclasses

/**
 * Created by jonas on 05.06.2017.
 */
class StringMixin {

    static Integer multiply(CharSequence name, Integer y) {
        return name.size() * y
    }


    Object asType(Class clazz) {
        String s = this

        switch (clazz) {
            case int:
                return s.size()
        }
    }

    static int setNBT(CharSequence name, String string) {
        return name.size() + string.size()
    }

    

}