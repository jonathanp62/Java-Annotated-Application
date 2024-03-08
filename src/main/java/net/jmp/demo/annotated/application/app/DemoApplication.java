package net.jmp.demo.annotated.application.app;

/*
 * (#)DemoApplication.java  0.5.0   03/08/2024
 * (#)DemoApplication.java  0.4.0   03/05/2024
 * (#)DemoApplication.java  0.1.0   02/27/2024
 *
 * @author    Jonathan Parker
 * @version   0.5.0
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

import net.jmp.demo.annotated.application.annotations.*;

import net.jmp.demo.annotated.application.main.ClassManager;

import net.jmp.demo.annotated.application.exceptions.PropertyInjectionException;

import org.slf4j.LoggerFactory;

import org.slf4j.ext.XLogger;

@Application
@AppConfig(configFileName = "config/demo.properties")
public final class DemoApplication {
    private final XLogger logger = new XLogger(LoggerFactory.getLogger(this.getClass().getName()));

    @AppInit
    public void initialize() {
        this.logger.entry();

        try {
            final var initializer = ClassManager.newInstance(DemoInitializer.class);

            initializer.ifPresent(instance -> ((DemoInitializer) instance).initialize());
        } catch (final PropertyInjectionException pie) {
            this.logger.catching(pie);
        }

        this.logger.exit();
    }

    @AppExec
    public void execute() {
        this.logger.entry();

        try {
            final var executor = ClassManager.newInstance(DemoExecutor.class);

            executor.ifPresent(instance -> ((DemoExecutor) instance).execute());
        } catch (final PropertyInjectionException pie) {
            this.logger.catching(pie);
        }

        this.logger.exit();
    }

    @AppTerm
    public void terminate() {
        this.logger.entry();

        try {
            final var terminator = ClassManager.newInstance(DemoTerminator.class);

            terminator.ifPresent(instance -> ((DemoTerminator) instance).terminate());
        } catch (final PropertyInjectionException pie) {
            this.logger.catching(pie);
        }

        this.logger.exit();
    }
}
