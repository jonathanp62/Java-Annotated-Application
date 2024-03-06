package net.jmp.demo.annotated.application.main;

/*
 * (#)ApplicationInjector.java  0.4.0   03/05/2024
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

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import java.util.List;
import java.util.Properties;

import net.jmp.demo.annotated.application.annotations.ApplicationProperty;

import net.jmp.demo.annotated.application.records.AnnotatedField;

import org.slf4j.LoggerFactory;

import org.slf4j.ext.XLogger;

final class ApplicationInjector {
    private final XLogger logger = new XLogger(LoggerFactory.getLogger(this.getClass().getName()));

    private final Properties properties;
    private final List<AnnotatedField> annotatedFields;

    ApplicationInjector(final Properties properties, final List<AnnotatedField> annotatedFields) {
        super();

        this.properties = properties;
        this.annotatedFields = annotatedFields;
    }

    boolean injectAnnotatedFields() {
        this.logger.entry();

        boolean result = false;

        for (final var annotatedField : annotatedFields) {
            final var className = annotatedField.className();
            final var fieldName = annotatedField.fieldName();

            this.logger.debug("Handling class '{}': field '{}'", className, fieldName);

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

                    if (this.properties.containsKey(name)) {
                        final var propertyValue = this.properties.getProperty(name);

                        result = true;
                    } else {
                        this.logger.warn("No application property defined for field annotation: {}", name);
                    }
                } else {
                    throw new IllegalStateException("Field '" + fieldName + "' in class '" + className + "' is not annotated with " + ApplicationProperty.class.getName());
                }
            } catch (final ClassNotFoundException | NoSuchFieldException e) {
                this.logger.catching(e);
            }
        }

        this.logger.exit(result);

        return result;
    }
}
