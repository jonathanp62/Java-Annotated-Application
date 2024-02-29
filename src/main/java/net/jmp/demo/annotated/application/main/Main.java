package net.jmp.demo.annotated.application.main;

/*
 * (#)Main.java 0.2.0   02/29/2024
 * (#)Main.java 0.1.0   02/27/2024
 *
 * @author    Jonathan Parker
 * @version   0.2.0
 * @since     0.1.0
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

import eu.infomas.annotation.AnnotationDetector;

import java.io.*;

import java.lang.annotation.Annotation;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import java.util.*;

import net.jmp.demo.annotated.application.annotations.*;

import org.slf4j.LoggerFactory;

import org.slf4j.ext.XLogger;

public final class Main {
    private final XLogger logger = new XLogger(LoggerFactory.getLogger(this.getClass().getName()));

    private Main() {
        super();
    }

    private void run() {
        this.logger.entry();

        // ApplicationLocator //

        final var applicationClassNameWrapper = this.getApplicationClassName();

        if (applicationClassNameWrapper.isPresent()) {
            final var applicationClassName = applicationClassNameWrapper.get();

            Class<?> applicationClass = null;

            try {
                applicationClass = Class.forName(applicationClassName);
            } catch (final ClassNotFoundException cnfe) {
                this.logger.catching(cnfe);
            }

            // ApplicationExecutor //

            if (applicationClass != null) {
                final var appInit = this.getAppMethod(applicationClass, AppInit.class);
                final var appExec = this.getAppMethod(applicationClass, AppExec.class);
                final var appTerm = this.getAppMethod(applicationClass, AppTerm.class);

                Object applicationClassInstance = null;

                try {
                    applicationClassInstance = applicationClass.getDeclaredConstructor().newInstance();
                } catch (IllegalAccessException | InstantiationException | InvocationTargetException |
                         NoSuchMethodException e) {
                    this.logger.catching(e);
                }

                if (applicationClassInstance != null) {
                    if (appInit.isPresent())
                        this.invokeAnnotatedMethod(applicationClassInstance, appInit.get());

                    if (appExec.isPresent()) {
                        this.invokeAnnotatedMethod(applicationClassInstance, appExec.get());
                    } else {
                        this.logger.warn("No annotated execution method was found in application: {}", applicationClass.getName());
                    }

                    if (appTerm.isPresent())
                        this.invokeAnnotatedMethod(applicationClassInstance, appTerm.get());
                }
            }
        } else {
            this.logger.warn("No annotated application class was found");
        }

        this.logger.exit();
    }

    private void invokeAnnotatedMethod(final Object appClassInstance, final Method method) {
        this.logger.entry(appClassInstance, method);

        try {
            method.invoke(appClassInstance);
        } catch (IllegalAccessException | InvocationTargetException e) {
            this.logger.catching(e);
        }

        this.logger.exit();
    }

    private Optional<Method> getAppMethod(final Class<?> appClass, final Class<? extends Annotation> annotation) {
        this.logger.entry(appClass, annotation);

        Method result = null;

        final var methods = appClass.getMethods();

        for (final var method : methods) {
            final var appAnnotation = method.getAnnotation(annotation);

            if (appAnnotation != null) {
                result = method;

                this.logger.debug("Annotated method '{}' found: {}", method.getName(), annotation.getName());
            }
        }

        this.logger.exit(result);

        return Optional.ofNullable(result);
    }

    private Optional<String> getApplicationClassName() {
        this.logger.entry();

        final var classReporter = new ClassReporter();
        final var annotationDetector = new AnnotationDetector(classReporter);

        try {
            annotationDetector.detect();
        } catch (final IOException ioe) {
            this.logger.catching(ioe);
        }

        final var applicationClassName = classReporter.getApplicationClassName();

        this.logger.exit(applicationClassName);

        return Optional.ofNullable(applicationClassName);
    }

    class ClassReporter implements AnnotationDetector.TypeReporter {
        private String applicationClassName;

        @SuppressWarnings("unchecked")
        @Override
        public Class<? extends Annotation>[] annotations() {
            return new Class[]{Application.class};
        }

        @Override
        public void reportTypeAnnotation(
                Class<? extends Annotation> annotation,
                String className
        ) {
            if (logger.isDebugEnabled()) {
                logger.debug("Found application class: {}", className);
                logger.debug("Annotated with         : {}", annotation.getName());
            }

            this.applicationClassName = className;
        }

        String getApplicationClassName() {
            return this.applicationClassName;
        }
    }

    public static void main(final String[] arguments) {
        new Main().run();
    }
}
