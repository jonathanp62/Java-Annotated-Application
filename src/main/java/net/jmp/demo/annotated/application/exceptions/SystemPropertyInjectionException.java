package net.jmp.demo.annotated.application.exceptions;

/*
 * (#)SystemPropertyInjectionException.java 0.5.0   03/07/2024
 *
 * @author    Jonathan Parker
 * @version   0.5.0
 * @since     0.5.0
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

import net.jmp.demo.annotated.application.enumerations.PropertyInjectionExceptionType;

public final class SystemPropertyInjectionException extends PropertyInjectionException {
    /**
     * The no argument constructor.
     */
    public SystemPropertyInjectionException() {
        super();

        super.setExceptionType(PropertyInjectionExceptionType.SYSTEM);
    }

    /**
     * A constructor that takes a message.
     *
     * @param	message	java.lang.String
     */
    public SystemPropertyInjectionException(final String message) {
        super(message);

        super.setExceptionType(PropertyInjectionExceptionType.SYSTEM);
    }

    /**
     * A constructor that takes a message
     * and a cause.
     *
     * @param	message	java.lang.String
     */
    public SystemPropertyInjectionException(final String message, final Throwable cause) {
        super(message, cause);

        super.setExceptionType(PropertyInjectionExceptionType.SYSTEM);
    }
}
