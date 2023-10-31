/*==========================================================================
Some code taken from Jordan Kidney 
Author: Quenten Welch
Date: October 15th 2023

Desciption: A reflective object inspector
//step 1 create all field, method, constructor inspector methods COMPLETED
//Step 2 address superclass and interfaces COMPLETED
//step 3 call inspectSuite on every object discovered if recursive is set to true, traverse hierachy up to Object. Completed
//CHECK, do i properly handly arrays in all instances? in Method, in Field, in Constructor, Declaring class:, inside inspector:, return type:
========================================================================*/

import java.util.*;
import java.lang.reflect.*;

public class Inspector {
    private Set<Class<?>> fieldClasses;

    public Inspector() {
        fieldClasses = new HashSet<>();
    }

    // -----------------------------------------------------------
    public void inspect(Object obj, boolean recursive) {
        Set<Object> objectsToInspect = new HashSet<>();
        Class ObjClass = obj.getClass();

        System.out.print("inside inspector: ");
        while (ObjClass.isArray()) {
            ObjClass = ObjClass.getComponentType();
            System.out.print("Array of ");
        }
        System.out.println(ObjClass.getName() + " (recursive = " + recursive + ")");

        System.out.println("---- Printing Class Information ----");
        inspectClassInformation(ObjClass);

        System.out.println("---- Printing Method Information ----");
        inspectMethods(ObjClass);

        System.out.println("---- Printing Field Information ----");
        inspectFields(obj, ObjClass, objectsToInspect, fieldClasses);

        System.out.println("---- Printing Constructor Information ----");
        inspectConstructors(ObjClass, fieldClasses);

        // System.out.println("---- Printing Inspect Superclass Information ----");
        // inspectSuperclasses(ObjClass, objectsToInspect, recursive);

        // System.out.println("---- Printing SuperInterfaces ----");
        // inspectSuperinterfaces(ObjClass, objectsToInspect, recursive);

        if (recursive)
            inspectFieldClasses(obj, ObjClass, objectsToInspect, recursive);

    }

    private void inspectSuperclasses(Class<?> ObjClass, Set<Object> objectsToInspect, boolean recursive) {
        Class<?> currentClass = ObjClass.getSuperclass();
        while (currentClass != null) {
            System.out.println("---- Inspecting Superclass: " + currentClass.getName() + " ----");
            System.out.println("---- Printing Class Information ----");
            inspectClassInformation(currentClass);

            System.out.println("---- Printing Method Information ----");
            inspectMethods(currentClass);

            System.out.println("---- Printing Field Information ----");
            inspectFields(null, currentClass, objectsToInspect, fieldClasses);

            System.out.println("---- Printing Constructor Information ----");
            inspectConstructors(currentClass, fieldClasses);

            if (recursive) {
                inspectFieldClasses(null, currentClass, objectsToInspect, recursive);
            }

            inspectSuperinterfaces(currentClass, objectsToInspect, recursive);

            currentClass = currentClass.getSuperclass();
        }
    }

    // Add this method to inspect superinterfaces recursively
    private void inspectSuperinterfaces(Class<?> ObjClass, Set<Object> objectsToInspect, boolean recursive) {
        Class<?>[] interfaces = ObjClass.getInterfaces();
        for (Class<?> anInterface : interfaces) {
            if (!fieldClasses.contains(anInterface)) {
                System.out.println("---- Inspecting Superinterface: " + anInterface.getName() + " ----");
                System.out.println("---- Printing Class Information ----");
                inspectClassInformation(anInterface);

                System.out.println("---- Printing Method Information ----");
                inspectMethods(anInterface);

                System.out.println("---- Printing Field Information ----");
                inspectFields(null, anInterface, objectsToInspect, fieldClasses);

                System.out.println("---- Printing Constructor Information ----");
                inspectConstructors(anInterface, fieldClasses);

                inspectSuperinterfaces(anInterface, objectsToInspect, recursive);

                if (recursive) {
                    inspectFieldClasses(null, anInterface, objectsToInspect, recursive);
                }
            }
        }
    }

    private void inspectClassInformation(Class<?> ObjClass) {
        System.out.println("Declaring Class: " + ObjClass.getName());

        Class<?> superClass = ObjClass.getSuperclass();
        if (superClass != null) {
            System.out.println("Immediate Superclass: " + superClass.getName());
        } else {
            System.out.println("Immediate Superclass: None");
        }

        Class<?>[] interfaces = ObjClass.getInterfaces();
        if (interfaces.length > 0) {
            System.out.print("Implemented Interfaces: ");
            for (Class<?> anInterface : interfaces) {
                System.out.print(anInterface.getName() + " ");
            }
            System.out.println();
        } else {
            System.out.println("Implemented Interfaces: None");
        }
    }

    private void inspectMethods(Class<?> ObjClass) {
        Method[] methods = ObjClass.getDeclaredMethods();

        for (Method method : methods) {
            String methodName = method.getName();
            Class<?> returnType = method.getReturnType();
            int modifiers = method.getModifiers();

            System.out.println("Method Name: " + methodName);
            System.out.println("Return Type: " + returnType.getName());
            System.out.println("Modifiers: " + Modifier.toString(modifiers));

            printParameterTypes(method);
            printExceptionTypes(method);

            System.out.println();
        }
    }

    private void printParameterTypes(Method method) {
        Class<?>[] parameterTypes = method.getParameterTypes();
        if (parameterTypes.length > 0) {
            System.out.print("Parameter Types: ");
            for (Class<?> parameterType : parameterTypes) {
                if (parameterType.isArray()) {
                    System.out.print(parameterType.getComponentType().getName() + "[] ");
                } else {
                    System.out.print(parameterType.getName() + " ");
                }
            }
            System.out.println();
        } else {
            System.out.println("Parameter Types: None");
        }
    }

    private void printExceptionTypes(Method method) {
        Class<?>[] exceptionTypes = method.getExceptionTypes();
        if (exceptionTypes.length > 0) {
            System.out.print("Exceptions Thrown: ");
            for (Class<?> exceptionType : exceptionTypes) {
                System.out.print(exceptionType.getName() + " ");
            }
            System.out.println();
        } else {
            System.out.println("Exceptions Thrown: None");
        }
    }

    // -----------------------------------------------------------
    private void inspectFieldClasses(Object obj, Class ObjClass,
            Set<Object> objectsToInspect, boolean recursive) {

        if (objectsToInspect.size() > 0) {
            System.out.println("---- Inspecting Field Classes ----");

            for (Object objectClass : objectsToInspect) {
                System.out.println("Currently inspecting Field Class:" + objectClass);
                System.out.println("objects to inspect:" + objectsToInspect.size());
                inspect(objectClass, recursive);
            }
        }
    }

    // -----------------------------------------------------------
    private void inspectFields(Object obj, Class<?> objClass, Set<Object> objectsToInspect,
            Set<Class<?>> fieldClasses) {
        Class<?> currentClass = objClass;

        Field[] fields = currentClass.getDeclaredFields();

        for (Field field : fields) {
            try {
                field.setAccessible(true);
                String fieldName = field.getName();
                Class<?> fieldType = field.getType();
                int modifiers = field.getModifiers();

                System.out.println("Field Name: " + fieldName);
                if (fieldType.isArray()) {
                    System.out.println("Field Type: " + fieldType.getComponentType().getName());
                } else {
                    System.out.println("Field Type: " + fieldType.getName());
                }
                System.out.println("Modifiers: " + Modifier.toString(modifiers));

                try {
                    Object fieldValue = field.get(obj);
                    if (fieldValue != null && fieldType.isArray()) {
                        int length = Array.getLength(fieldValue);
                        System.out.println("Array Length: " + length);
                        for (int i = 0; i < length; i++) {
                            Object arrayElement = Array.get(fieldValue, i);
                            System.out.println("Element " + i + ": " + arrayElement);
                            if (arrayElement != null && !fieldType.getComponentType().isPrimitive()) {
                                objectsToInspect.add(arrayElement);
                            }
                        }
                    } else {
                        System.out.println("Field Value: " + fieldValue);
                    }

                    if (fieldValue != null && !fieldType.isPrimitive()) {
                        objectsToInspect.add(fieldValue);
                    }
                } catch (Exception e) {
                    // Handle exceptions when getting field value appropriately, e.g., log or throw
                }
            } catch (InaccessibleObjectException e) {
                System.out.println("Object not accessible: " + e.getMessage());
            } catch (SecurityException e) {
                System.out.println("Security exception: " + e.getMessage());
            }
        }

    }

    private void inspectConstructors(Class<?> objClass, Set<Class<?>> fieldClasses) {
        Class<?> currentClass = objClass;

        Constructor<?>[] constructors = currentClass.getDeclaredConstructors();

        for (Constructor<?> constructor : constructors) {
            String constructorName = constructor.getName();
            int modifiers = constructor.getModifiers();
            Class<?>[] parameterTypes = constructor.getParameterTypes();

            System.out.println("Constructor Name: " + constructorName);
            System.out.println("Modifiers: " + Modifier.toString(modifiers));

            System.out.print("Parameter Types: ");
            for (Class<?> parameterType : parameterTypes) {
                if (parameterType.isArray()) {
                    System.out.print(parameterType.getComponentType().getName() + "[] ");
                } else {
                    System.out.print(parameterType.getName() + " ");
                }
            }
            System.out.println();
        }

    }
}