/*
 * Copyright 2022 Alireza Akhoundi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package graphql_adapter.adaptedschema.exceptions;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public class SchemaExceptionBuilder<T extends Throwable> {

    public static <T extends Throwable> T exception(Class<T> cls, String message, Class<?> clazz, Method method, Parameter parameter) {
        String exceptionMessage = message + location(clazz, method, parameter);
        try {
            return cls.getConstructor(String.class).newInstance(exceptionMessage);
        } catch (Exception ex) {
            throw new Error(ex);
        }
    }

    public static <T extends Throwable> T exception(Class<T> exceptionClass, String message, Class<?> clazz, Field field) {
        String exceptionMessage = message + location(clazz, field);
        try {
            return exceptionClass.getConstructor(String.class).newInstance(exceptionMessage);
        } catch (Exception ex) {
            throw new Error(ex);
        }
    }

    public static <T extends Throwable> T exception(Class<T> exceptionClass, String message, Class<?> clazz, Method method) {
        return exception(exceptionClass, message, clazz, method, null);
    }

    public static <T extends Throwable> T exception(Class<T> exceptionClass, String message, Class<?> clazz) {
        return exception(exceptionClass, message, clazz, null, null);
    }

    public static <T extends Throwable> T exception(Class<T> exceptionClass, String message, Method method) {
        return exception(exceptionClass, message, null, method, null);
    }

    private final Class<T> exceptionClazz;

    public SchemaExceptionBuilder(Class<T> exceptionClazz) {
        this.exceptionClazz = exceptionClazz;
    }

    public T exception(String message, Class<?> clazz, Method method, Parameter parameter) {
        return exception(exceptionClazz, message, clazz, method, parameter);
    }

    private static String location(Class<?> clazz, Method method, Parameter parameter) {
        if (clazz == null && method == null && parameter == null) {
            return "";
        }
        StringBuilder string = new StringBuilder();
        string.append("\r\nLocation : ")
                .append("\r\n");
        if (clazz != null) {
            string.append("\t")
                    .append("Class : [ " + clazz + " ]")
                    .append("\r\n");
        }
        if (method != null) {
            string.append("\t")
                    .append("Method : [ " + method + " ]")
                    .append("\r\n");
        }
        if (parameter != null) {
            string.append("\t")
                    .append("Parameter : [ " + parameter + " ]")
                    .append("\r\n");
        }
        return string.toString();
    }

    private static String location(Class<?> clazz, Field field) {
        if (clazz == null && field == null) {
            return "";
        }
        StringBuilder string = new StringBuilder();
        string.append("\r\nLocation : ")
                .append("\r\n");
        if (clazz != null) {
            string.append("\t")
                    .append("Class : [ " + clazz + " ]")
                    .append("\r\n");
        }
        if (field != null) {
            string.append("\t")
                    .append("Field : [ " + field + " ]")
                    .append("\r\n");
        }
        return string.toString();
    }
}
