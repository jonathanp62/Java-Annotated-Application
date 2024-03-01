package net.jmp.demo.annotated.application.main;

/*
 * (#)Main.ApplicationExecutor  0.2.0   02/29/2024
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

import java.lang.annotation.Annotation;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import java.util.Optional;

import net.jmp.demo.annotated.application.annotations.AppExec;
import net.jmp.demo.annotated.application.annotations.AppInit;
import net.jmp.demo.annotated.application.annotations.AppTerm;

import org.slf4j.LoggerFactory;

import org.slf4j.ext.XLogger;

final class ApplicationExecutor {
    private final XLogger logger = new XLogger(LoggerFactory.getLogger(this.getClass().getName()));

    ApplicationExecutor() {
        super();
    }

    void executeApplication(final Class<?> applicationClass) {
        this.logger.entry(applicationClass);

        assert applicationClass != null;

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

        this.logger.exit();
    }

    private Optional<Method> getAppMethod(final Class<?> appClass, final Class<? extends Annotation> annotation) {
        this.logger.entry(appClass, annotation);

        assert appClass != null;
        assert annotation != null;

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

    private void invokeAnnotatedMethod(final Object appClassInstance, final Method method) {
        this.logger.entry(appClassInstance, method);

        assert appClassInstance != null;
        assert method != null;

        try {
            method.invoke(appClassInstance);
        } catch (IllegalAccessException | InvocationTargetException e) {
            this.logger.catching(e);
        }

        this.logger.exit();
    }
}
