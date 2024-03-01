package net.jmp.demo.annotated.application.main;

/*
 * (#)Main.ApplicationLocator   0.2.0   02/29/2024
 *
 * @author    Jonathan Parker
 * @version   0.2.0
 * @since     0.2.0
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

import java.io.IOException;

import java.lang.annotation.Annotation;

import java.util.Optional;

import net.jmp.demo.annotated.application.annotations.Application;

import org.slf4j.LoggerFactory;

import org.slf4j.ext.XLogger;

final class ApplicationLocator {
    private final XLogger logger = new XLogger(LoggerFactory.getLogger(this.getClass().getName()));

    ApplicationLocator() {
        super();
    }

    Optional<Class<?>> locateApplicationClass() {
        this.logger.entry();

        Class<?> applicationClass = null;

        try {
            final var applicationClassNameWrapper = this.getApplicationClassName();

            if (applicationClassNameWrapper.isPresent()) {
                final var applicationClassName = applicationClassNameWrapper.get();
                final var applicationClassWrapper = this.loadAndGetApplicationClass(applicationClassName);

                if (applicationClassWrapper.isPresent())
                    applicationClass = applicationClassWrapper.get();
                else
                    this.logger.error("Failed to load application class: {}", applicationClassName);
            } else {
                this.logger.warn("No annotated application class was found");
            }
        } catch (final IOException ioe) {
            this.logger.catching(ioe);
        }

        this.logger.exit(applicationClass);

        return Optional.ofNullable(applicationClass);
    }

    private Optional<String> getApplicationClassName() throws IOException {
        this.logger.entry();

        final var classReporter = new ClassReporter();
        final var annotationDetector = new AnnotationDetector(classReporter);

        annotationDetector.detect();

        final var applicationClassName = classReporter.getApplicationClassName();

        this.logger.exit(applicationClassName);

        return Optional.ofNullable(applicationClassName);
    }

    private Optional<Class<?>> loadAndGetApplicationClass(final String className) {
        this.logger.entry(className);

        assert className != null;

        Class<?> applicationClass = null;

        // Loads the class

        try {
            applicationClass = Class.forName(className);
        } catch (final ClassNotFoundException cnfe) {
            this.logger.catching(cnfe);
        }

        this.logger.exit(applicationClass);

        return Optional.ofNullable(applicationClass);
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
}
