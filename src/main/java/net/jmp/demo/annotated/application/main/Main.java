package net.jmp.demo.annotated.application.main;

/*
 * (#)Main.java 0.1.0   02/27/2024
 *
 * @author    Jonathan Parker
 * @version   0.1.0
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

import java.io.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import java.util.*;

import java.util.stream.Collectors;

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

        this.preloadClasses();

        final var applicationClassWrapper = this.getApplicationClass();

        if (applicationClassWrapper.isPresent()) {
            final var applicationClass = applicationClassWrapper.get();

            final var appInit = this.getAppMethod(applicationClass, AppInit.class);
            final var appExec= this.getAppMethod(applicationClass, AppExec.class);
            final var appTerm = this.getAppMethod(applicationClass, AppTerm.class);

            appInit.ifPresent(method -> this.invokeAnnotatedMethod(applicationClass, method));

            if (appExec.isPresent()) {
                this.invokeAnnotatedMethod(applicationClass, appExec.get());
            } else {
                this.logger.warn("No annotated execution method was found in application: {}", applicationClass.getName());
            }

            appTerm.ifPresent(method -> this.invokeAnnotatedMethod(applicationClass, method));
        } else {
            this.logger.warn("No annotated application class was found");
        }

        this.logger.exit();
    }

    private void invokeAnnotatedMethod(final Class<?> appClass, final Method method) {
        this.logger.entry(appClass, method);

        try {
            method.invoke(appClass.getDeclaredConstructor().newInstance());
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException | NoSuchMethodException e) {
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

    private void preloadClasses() {
        this.logger.entry();

        final var classesToLoad = List.of(
                "net.jmp.demo.annotated.application.app.DemoApplication"
        );

        // This is done to load the class as the class loader won't find it unless it is loaded

        try {
            for (final var classToLoad : classesToLoad)
                ClassLoader.getSystemClassLoader().loadClass(classToLoad);
        } catch (final ClassNotFoundException cnfe) {
            this.logger.catching(cnfe);
        }

        this.logger.exit();
    }

    private Optional<Class<?>> getApplicationClass() {
        this.logger.entry();

        Class<?> applicationClass = null;

        final var packages = ClassLoader.getSystemClassLoader().getDefinedPackages();

        for (final var pkg : packages) {
            final var classes = this.findAllClassesUsingClassLoader(pkg.getName());

            for (final var clazz : classes) {
                final var appAnnotation = clazz.getAnnotation(Application.class);

                if (appAnnotation != null) {
                    applicationClass = clazz;

                    if (this.logger.isDebugEnabled())
                        this.logger.debug("Found application class: {}", clazz.getName());

                    break;
                }
            }
        }

        this.logger.exit(applicationClass);

        return Optional.ofNullable(applicationClass);
    }

    private Set<Class<?>> findAllClassesUsingClassLoader(final String packageName) {
        this.logger.entry(packageName);

        Set<Class<?>> classes;

        try (final var stream = ClassLoader.getSystemClassLoader()
                .getResourceAsStream(packageName.replaceAll("[.]", "/"))) {

            if (stream != null) {
                try (final var reader = new BufferedReader(new InputStreamReader(stream))) {
                    classes = reader.lines()
                            .filter(line -> line.endsWith(".class"))
                            .map(line -> getClass(line, packageName))
                            .collect(Collectors.toSet());
                }
            } else {
                classes = new HashSet<>();
            }
        } catch (final IOException ioe) {
            this.logger.catching(ioe);

            classes = new HashSet<>();
        }

        this.logger.exit(classes);

        return classes;
    }

    private Class<?> getClass(final String className, final String packageName) {
        this.logger.entry(className, packageName);

        Class<?> clazz;

        try {
            clazz = Class.forName(packageName + "."
                    + className.substring(0, className.lastIndexOf('.')));
        } catch (final ClassNotFoundException cnfe) {
            this.logger.catching(cnfe);

            clazz = null;
        }

        this.logger.exit(clazz);

        return clazz;
    }

    public static void main(final String[] arguments) {
        new Main().run();
    }
}
