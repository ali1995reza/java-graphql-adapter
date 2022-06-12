package grphaqladapter.adaptedschemabuilder.exceptions;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public class SchemaExceptionBuilder<T extends Throwable> {

    private final Class<T> exceptionClazz;

    public SchemaExceptionBuilder(Class<T> exceptionClazz) {
        this.exceptionClazz = exceptionClazz;
    }

    public static <T extends Throwable> T exception(Class<T> cls, String message, Class clazz, Method method, Parameter parameter) {
        String exceptionMessage = message + location(clazz, method, parameter);
        try {
            return cls.getConstructor(String.class).newInstance(exceptionMessage);
        } catch (Exception ex) {
            throw new Error(ex);
        }
    }

    public static <T extends Throwable> T exception(Class<T> cls, String message, Class clazz, Method method) {
        return exception(cls, message, clazz, method, null);
    }

    public static <T extends Throwable> T exception(Class<T> cls, String message, Class clazz) {
        return exception(cls, message, clazz, null, null);
    }

    public static <T extends Throwable> T exception(Class<T> cls, String message, Method method) {
        return exception(cls, message, null, method, null);
    }

    private static String location(Class clazz, Method method, Parameter parameter) {
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

    public T exception(String message, Class clazz, Method method, Parameter parameter) {
        return exception(exceptionClazz, message, clazz, method, parameter);
    }

}
