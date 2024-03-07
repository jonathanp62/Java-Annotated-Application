package net.jmp.demo.annotated.application.main;

/*
 * (#)ClassManager.java 0.4.0   03/06/2024
 *
 * @author    Jonathan Parker
 * @version   0.4.0
 * @since     0.4.0
 *
 * MIT License
 *
 * Copyright (c) 2024 Jonathan M. Parker
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import net.jmp.demo.annotated.application.annotations.ApplicationProperty;
import net.jmp.demo.annotated.application.annotations.ManagedClass;

import net.jmp.demo.annotated.application.enumerations.PropertyDataType;

import org.slf4j.LoggerFactory;

import org.slf4j.ext.XLogger;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

import java.util.Optional;

public final class ClassManager {
    private ClassManager() {
        super();
    }

    public static Optional<Object> newInstance(final Class<?> managedClass) {
        final var logger = new XLogger(LoggerFactory.getLogger(ClassManager.class.getName()));

        logger.entry(managedClass);

        Object classInstance = null;

        try {
            final var classConstructor = managedClass.getDeclaredConstructor();

            if (!Modifier.isPublic(classConstructor.getModifiers()))
                classConstructor.setAccessible(true);

            classInstance = classConstructor.newInstance();
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException |
                 NoSuchMethodException e) {
            logger.catching(e);
        }

        if (classInstance != null)
            inject(managedClass, classInstance);

        logger.exit(classInstance);

        return Optional.ofNullable(classInstance);
    }

    private static void inject(final Class<?> managedClass, final Object managedClassInstance) {
        final var logger = new XLogger(LoggerFactory.getLogger(ClassManager.class.getName()));

        assert managedClass != null;
        assert managedClassInstance != null;

        logger.entry(managedClass, managedClassInstance);

        final var managedAnnotation = managedClass.getAnnotation(ManagedClass.class);

        if (managedAnnotation != null && injectAnnotatedFields(logger, managedClass, managedClassInstance))
            logger.debug("Instance of class {} injected", managedClassInstance.getClass().getName());

        logger.exit();
    }

    private static boolean injectAnnotatedFields(final XLogger logger, final Class<?> managedClass, final Object managedClassInstance) {
        assert logger != null;

        logger.entry(managedClass, managedClassInstance);

        assert managedClass != null;
        assert managedClassInstance != null;

        final var managedClassName = managedClass.getName();
        final var properties = ApplicationConfigurator.getProperties();
        final var annotatedFields = ApplicationConfigurator.getAnnotatedFields();

        boolean result = false;

        for (final var annotatedField : annotatedFields) {
            final var className = annotatedField.className();

            if (className.equals(managedClassName)) {
                final var fieldName = annotatedField.fieldName();

                logger.debug("Handling class '{}': field '{}'", className, fieldName);

                try {
                    final var clazz = Class.forName(className);
                    final Field field = clazz.getDeclaredField(fieldName);

                    if (field.isAnnotationPresent(ApplicationProperty.class)) {
                        if (!Modifier.isPublic(field.getModifiers()))
                            field.setAccessible(true);

                        final var applicationProperty = field.getAnnotation(ApplicationProperty.class);

                        final var name = applicationProperty.name();
                        final var type = applicationProperty.type();
                        final var optional = applicationProperty.optional();

                        if (properties.containsKey(name)) {
                            final var propertyValue = properties.getProperty(name);

                            if (propertyValue == null || propertyValue.isBlank()) {
                                if (!optional)
                                    logger.warn("Blank application property value found: {}", propertyValue);
                                else
                                    injectDefaultValue(
                                            logger,
                                            managedClassInstance,
                                            field,
                                            type);
                            }

                            injectPropertyValue(
                                    logger,
                                    managedClassInstance,
                                    field,
                                    type,
                                    propertyValue);
                            
                            result = true;
                        } else {
                            logger.warn("No application property defined for field annotation: {}", name);
                        }
                    } else {
                        throw new IllegalStateException("Field '" + fieldName + "' in class '" + className + "' is not annotated with " + ApplicationProperty.class.getName());
                    }
                } catch (final ClassNotFoundException | NoSuchFieldException e) {
                    logger.catching(e);
                }
            }
        }

        logger.exit(result);

        return result;
    }

    private static void injectDefaultValue(
            final XLogger logger,
            final Object instance,
            final Field field,
            final PropertyDataType dataType) {
        assert logger != null;

        logger.entry(instance, field, dataType);

        assert instance != null;
        assert field != null;
        assert dataType != null;

        try {
            if (dataType == PropertyDataType.STRING)
                field.set(instance, "");
            else if (dataType == PropertyDataType.LONG)
                field.set(instance, 0L);
            else if (dataType == PropertyDataType.INTEGER)
                field.set(instance, 0);
            else if (dataType == PropertyDataType.BOOLEAN)
                field.set(instance, false);
        } catch (IllegalAccessException iae) {
            throw new RuntimeException("Unable to set field: " + field.getName() + " to default value: " + iae.getLocalizedMessage(), iae);
        }

        logger.exit();
    }

    private static void injectPropertyValue(
            final XLogger logger,
            final Object instance,
            final Field field,
            final PropertyDataType dataType,
            final String value) {
        assert logger != null;

        logger.entry(instance, field, dataType,value);

        assert instance != null;
        assert field != null;
        assert dataType != null;
        assert value != null;

        try {
            if (dataType == PropertyDataType.STRING)
                field.set(instance, value);
            else if (dataType == PropertyDataType.LONG)
                field.set(instance, Long.parseLong(value));
            else if (dataType == PropertyDataType.INTEGER)
                field.set(instance, Integer.parseInt(value));
            else if (dataType == PropertyDataType.BOOLEAN) {
                if ("true".equalsIgnoreCase(value) || "yes".equalsIgnoreCase(value))
                    field.set(instance, true);
                else if ("false".equalsIgnoreCase(value) || "no".equalsIgnoreCase(value))
                    field.set(instance, false);
                else
                    throw new RuntimeException("Invalid boolean expression on field: " + field.getName());
            }
        } catch (IllegalAccessException iae) {
            throw new RuntimeException("Unable to set field: " + field.getName() + ": " + iae.getLocalizedMessage(), iae);
        }

        logger.exit();
    }
}
