package de.bloodworkxgaming.groovysandboxedlauncher.sandbox;

import org.codehaus.groovy.control.customizers.ImportCustomizer;

import java.lang.reflect.Field;

import static de.bloodworkxgaming.groovysandboxedlauncher.sandbox.GroovySandboxedLauncher.DEBUG;

public class ImportModifier {
    private ImportCustomizer importCustomizer = new ImportCustomizer();

    /**
     * For importing one class with a alias
     * eg: import java.lang.Math as M
     *
     * @param className eg: java.lang.Math
     * @param alias     eg: M
     */
    public void addImport(String alias, String className) {
        if (checkClassExists(className)) {
            importCustomizer.addImport(alias, className);
        } else {
            if (DEBUG) System.out.println("Can't import class " + className + " as " + alias);
        }
    }

    /**
     * For importing a static field
     * eg: import static java.lang.Integer.MAX_VALUE;
     *
     * @param className eg: java.lang.Integer
     * @param fieldName eg: MAX_VALUE
     */
    public void addStaticImport(String className, String fieldName) {
        if (checkFieldExists(className, fieldName)) {
            importCustomizer.addStaticImport(className, fieldName);
        } else {
            if (DEBUG) System.out.println("Can't import static field " + fieldName + "of class " + className);
        }
    }

    /**
     * For Importing all static fields of a Class,
     * eg: import static java.lang.Integer.*;
     *
     * @param classNames eg: java.lang.Integer, java.lang.Short, ...
     */
    public void addStaticStars(String... classNames) {
        for (String className : classNames) {
            if (checkClassExists(className)) {
                importCustomizer.addStaticStars(className);
            } else {
                if (DEBUG) System.out.println("Can't import static star " + className);
            }
        }
    }

    /**
     * For importing a static field with an alias
     * eg: import static java.lang.Integer.MAX_VALUE as INT_VALUE;
     *
     * @param className eg: java.lang.Integer
     * @param fieldName eg: MAX_VALUE
     * @param alias     INT_VALUE
     */
    public void addStaticImport(String alias, String className, String fieldName) {
        if (checkFieldExists(className, fieldName)) {
            importCustomizer.addStaticImport(alias, className, fieldName);
        } else {
            if (DEBUG)
                System.out.println("Can't import static field " + fieldName + "of class " + className + " as " + alias);
        }
    }

    /**
     * For normally importing a class
     * eg: import java.lang.reflect.Field;
     *
     * @param imports eg: java.lang.reflect.Field, java.lang.Math, ...
     */
    public void addImports(String... imports) {
        for (String className : imports) {
            if (checkClassExists(className)) {
                importCustomizer.addImports(className);
            } else {
                if (DEBUG) System.out.println("Can't import class " + className);
            }
        }

    }

    /**
     * For importing all sub classes of a package
     * eg: import java.lang.*;
     *
     * @param packageNames only the name without the *
     */
    public void addStarImports(String... packageNames) {
        for (String packageName : packageNames) {
            if (checkPackageExists(packageName)) {
                importCustomizer.addStarImports(packageName);
            } else {
                if (DEBUG) System.out.println("Can't star import package " + packageName);
            }
        }

    }

    public ImportCustomizer getImportCustomizer() {
        return importCustomizer;
    }

    private boolean checkPackageExists(String importName) {
        Package pack = Package.getPackage(importName);
        if (pack != null) {
            return true;
        } else {
            if (DEBUG) System.out.println("Can't retrieve package " + importName);
            return false;
        }
    }

    private boolean checkClassExists(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException e) {
            if (DEBUG) System.out.println("Can't retrieve class " + className);
            return false;
        }
    }

    private boolean checkFieldExists(String className, String fieldName) {
        try {
            Class<?> clazz = Class.forName(className);
            for (Field field : clazz.getFields()) {
                if (field.getName().equals(fieldName)) return true;
            }
            if (DEBUG) System.out.println("Can't retrieve Field name " + fieldName + " of class " + className);
            return false;
        } catch (ClassNotFoundException e) {
            if (DEBUG) System.out.println("Can't retrieve class " + className);
            return false;
        }
    }
}
