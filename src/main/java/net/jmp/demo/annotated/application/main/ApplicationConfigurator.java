package net.jmp.demo.annotated.application.main;

/*
 * (#)ApplicationConfigurator.java  0.4.0   03/05/2024
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

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;

import java.util.Optional;
import java.util.Properties;

import net.jmp.demo.annotated.application.annotations.AppConfig;

import org.slf4j.LoggerFactory;

import org.slf4j.ext.XLogger;

final class ApplicationConfigurator {
    private final XLogger logger = new XLogger(LoggerFactory.getLogger(this.getClass().getName()));

    ApplicationConfigurator() {
        super();
    }

    void configureApplication(final Class<?> applicationClass) {
        this.logger.entry(applicationClass);

        assert applicationClass != null;

        this.isApplicationConfigured(applicationClass).ifPresent(configFileName -> {
            final var properties = this.loadProperties(configFileName);

            if (!properties.isEmpty()) {
                this.logger.info("Configuration applied");
            } else {
                this.logger.warn("No properties found in the configuration");
            }
        });

        this.logger.exit();
    }

    private Optional<String> isApplicationConfigured(final Class<?> applicationClass) {
        this.logger.entry(applicationClass);

        assert applicationClass != null;

        String configFileName = null;

        final var annotation = applicationClass.getAnnotation(AppConfig.class);

        if (annotation != null) {
            configFileName = annotation.configFileName();

            this.logger.debug("Will be configured with: {}", configFileName);
        }

        this.logger.exit(configFileName);

        return Optional.ofNullable(configFileName);
    }

    private Properties loadProperties(final String configFileName) {
        this.logger.entry(configFileName);

        assert configFileName != null;

        Properties properties = new Properties();

        if (!configFileName.isBlank()) {
            this.logger.debug("Loading application properties from configuration file: {}", configFileName);

            try (final FileInputStream fis = new FileInputStream(configFileName)) {
                try (final BufferedInputStream bis = new BufferedInputStream(fis)) {
                    properties.load(bis);
                    this.logger.debug("Done loading application properties");
                }
            } catch (final IOException ioe) {
                this.logger.catching(ioe);
            }
        }

        this.logger.exit(properties);

        return properties;
    }
}
