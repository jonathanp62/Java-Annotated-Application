package net.jmp.demo.annotated.application.main;

/*
 * (#)Main.java 0.4.0   03/05/2024
 * (#)Main.java 0.2.0   02/29/2024
 * (#)Main.java 0.1.0   02/27/2024
 *
 * @author    Jonathan Parker
 * @version   0.4.0
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

import java.util.Optional;

import org.slf4j.LoggerFactory;

import org.slf4j.ext.XLogger;

public final class Main {
    private final XLogger logger = new XLogger(LoggerFactory.getLogger(this.getClass().getName()));

    private Main() {
        super();
    }

    private void run() {
        this.logger.entry();

        this.locateApplication().ifPresent(clazz -> {
            this.configureApplication(clazz);
            this.executeApplication(clazz);
        });

        this.logger.exit();
    }

    private Optional<Class<?>> locateApplication() {
        this.logger.entry();

        final var locator = new ApplicationLocator();
        final var application = locator.locateApplicationClass();

        this.logger.exit(application);

        return application;
    }

    private void configureApplication(final Class<?> applicationClass) {
        this.logger.entry(applicationClass);

        assert applicationClass != null;

        new ApplicationConfigurator().configureApplication(applicationClass);

        this.logger.exit();
    }

    private void executeApplication(final Class<?> applicationClass) {
        this.logger.entry(applicationClass);

        assert applicationClass != null;

        new ApplicationExecutor().executeApplication(applicationClass);

        this.logger.exit();
    }

    public static void main(final String[] arguments) {
        new Main().run();
    }
}
