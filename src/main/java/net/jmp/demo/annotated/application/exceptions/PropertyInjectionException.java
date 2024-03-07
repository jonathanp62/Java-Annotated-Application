package net.jmp.demo.annotated.application.exceptions;

/*
 * (#)PropertyInjectionException.java   0.5.0   03/07/2024
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

import net.jmp.demo.annotated.application.enumerations.PropertyDataType;
import net.jmp.demo.annotated.application.enumerations.PropertyInjectionExceptionType;

public abstract class PropertyInjectionException extends RuntimeException {
    protected String fieldName;
    protected String propertyName;
    protected PropertyDataType dataType;
    protected String value;
    protected PropertyInjectionExceptionType exceptionType;

    /**
     * The no argument constructor.
     */
    protected PropertyInjectionException() {
        super();
    }

    /**
     * A constructor that takes a message.
     *
     * @param	message	java.lang.String
     */
    protected PropertyInjectionException(final String message) {
        super(message);
    }

    /**
     * A constructor that takes a message
     * and a cause.
     *
     * @param	message	java.lang.String
     */
    protected PropertyInjectionException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public String getFieldName() {
        return this.fieldName;
    }

    public void setFieldName(final String fieldName) {
        this.fieldName = fieldName;
    }

    public String getPropertyName() {
        return this.propertyName;
    }

    public void setPropertyName(final String propertyName) {
        this.propertyName = propertyName;
    }

    public PropertyDataType getDataType() {
        return this.dataType;
    }

    public void setDataType(final PropertyDataType dataType) {
        this.dataType = dataType;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(final String value) {
        this.value = value;
    }

    public PropertyInjectionExceptionType getExceptionType() {
        return this.exceptionType;
    }

    public void setExceptionType(final PropertyInjectionExceptionType exceptionType) {
        this.exceptionType = exceptionType;
    }
}
