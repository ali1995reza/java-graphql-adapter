package grphaqladapter.adaptedschemabuilder.mapper.strategy.impl.field;

import grphaqladapter.adaptedschemabuilder.mapped.MappedElementType;
import grphaqladapter.adaptedschemabuilder.mapper.strategy.MethodAnnotationDetector;
import grphaqladapter.annotations.GraphqlDescriptionAnnotation;
import grphaqladapter.annotations.GraphqlDirectiveArgumentAnnotation;
import grphaqladapter.annotations.GraphqlFieldAnnotation;
import grphaqladapter.annotations.GraphqlInputFieldAnnotation;
import grphaqladapter.annotations.impl.field.GraphqlFieldAnnotationBuilder;
import grphaqladapter.annotations.impl.field.GraphqlInputFieldAnnotationBuilder;

import java.lang.reflect.Method;

public class PojoMethodAnnotationDetector implements MethodAnnotationDetector {

    private final static String GET_PREFIX = "get";
    private final static String SET_PREFIX = "set";
    private final static String IS_PREFIX = "is";
    private final static String EMPTY = "";
    private final static String GET_CLASS_METHOD_NAME = "getClass";


    private final boolean removePrefix;

    public PojoMethodAnnotationDetector(boolean removePrefix) {
        this.removePrefix = removePrefix;
    }

    public PojoMethodAnnotationDetector() {
        this(true);
    }

    private static boolean isNullable(Method method) {
        return !method.getReturnType().isPrimitive();
    }

    private static String firstCharLowerCase(String str) {
        char[] array = str.toCharArray();
        array[0] = Character.toLowerCase(array[0]);
        return new String(array);
    }

    private static boolean skip(Method method) {
        if (!method.getName().startsWith(IS_PREFIX) && !method.getName().startsWith(GET_PREFIX)) {
            return true;
        }
        if (method.getName().equals(GET_CLASS_METHOD_NAME)) {
            return true;
        }

        return false;
    }

    @Override
    public GraphqlFieldAnnotation detectFieldAnnotation(Method method, Class clazz, MappedElementType mappedElementType) {
        if (skip(method)) {
            return null;
        }
        return outputField(method, clazz);
    }

    @Override
    public GraphqlInputFieldAnnotation detectInputFieldAnnotation(Method method, Class clazz, MappedElementType mappedElementType) {
        if (skip(method)) {
            return null;
        }
        return inputField(method, clazz);
    }

    @Override
    public GraphqlDirectiveArgumentAnnotation detectDirectiveArgumentAnnotation(Method method, Class clazz, MappedElementType mappedElementType) {
        return null;
    }

    @Override
    public GraphqlDescriptionAnnotation detectDescriptionAnnotation(Method method, Class clazz, MappedElementType mappedElementType) {
        return null;
    }

    private GraphqlInputFieldAnnotation inputField(Method method, Class clazz) {
        String setter = getSetterName(method, clazz);
        if (setter == null) {
            return null;
        }
        return GraphqlInputFieldAnnotationBuilder.newBuilder()
                .name(getName(method))
                .nullable(isNullable(method))
                .setter(setter)
                .build();
    }

    private GraphqlFieldAnnotation outputField(Method method, Class clazz) {
        return GraphqlFieldAnnotationBuilder.newBuilder()
                .name(getName(method))
                .nullable(isNullable(method))
                .build();
    }

    private String removePrefix(Method method) {
        String name = method.getName();
        if (name.startsWith(GET_PREFIX)) {
            name = name.replaceFirst(GET_PREFIX, EMPTY);
        } else if (name.startsWith(IS_PREFIX)) {
            name = name.replaceFirst(IS_PREFIX, EMPTY);
        }
        return name;
    }

    private String getName(Method method) {
        String name = removePrefix ? firstCharLowerCase(removePrefix(method)) : method.getName();
        return name;
    }

    private String getSetterName(Method method, Class clazz) {
        String name = removePrefix(method);
        Method setter = getMethod(clazz, SET_PREFIX + name, method.getReturnType());
        if (setter == null) {
            return null;
        }
        return setter.getName();
    }

    private Method getMethod(Class clazz, String name, Class... params) {
        try {
            return clazz.getMethod(name, params);
        } catch (NoSuchMethodException ignored) {
        }
        try {
            return clazz.getDeclaredMethod(name, params);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }
}
