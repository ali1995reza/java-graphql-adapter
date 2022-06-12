package grphaqladapter.adaptedschemabuilder.mapper.strategy.impl.field;

import grphaqladapter.adaptedschemabuilder.mapped.MappedClass;
import grphaqladapter.adaptedschemabuilder.mapper.strategy.MethodAnnotationDetector;
import grphaqladapter.annotations.GraphqlDescriptionAnnotation;
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
    public GraphqlFieldAnnotation detectFieldAnnotation(Method method, Class clazz, MappedClass.MappedType mappedType) {
        if (skip(method)) {
            return null;
        }
        return outputField(method, clazz);
    }

    @Override
    public GraphqlInputFieldAnnotation detectInputFieldAnnotation(Method method, Class clazz, MappedClass.MappedType mappedType) {
        if (skip(method)) {
            return null;
        }
        return inputField(method, clazz);
    }

    @Override
    public GraphqlDescriptionAnnotation detectDescriptionAnnotation(Method method, Class clazz, MappedClass.MappedType mappedType) {
        return null;
    }

    private GraphqlInputFieldAnnotation inputField(Method method, Class clazz) {
        String setter = getSetterName(method, clazz);
        if (setter == null) {
            return null;
        }
        return GraphqlInputFieldAnnotationBuilder.newBuilder()
                .setInputFieldName(getName(method))
                .setNullable(isNullable(method))
                .setSetter(setter)
                .build();
    }

    private GraphqlFieldAnnotation outputField(Method method, Class clazz) {
        return GraphqlFieldAnnotationBuilder.newBuilder()
                .setFieldName(getName(method))
                .setNullable(isNullable(method))
                .build();
    }

    private String removePrefix(Method method) {
        String name = method.getName();
        if (name.startsWith(GET_PREFIX)) {
            name = name.replaceFirst(GET_PREFIX, EMPTY);
            name = firstCharLowerCase(name);
        } else if (name.startsWith(IS_PREFIX)) {
            name = name.replaceFirst(IS_PREFIX, EMPTY);
            name = firstCharLowerCase(name);
        }
        return name;
    }

    private String getName(Method method) {
        String name = removePrefix ? removePrefix(method) : method.getName();
        return name;
    }

    private String getSetterName(Method method, Class clazz) {
        String name = removePrefix(method);
        try {
            clazz.getMethod(SET_PREFIX + name, method.getReturnType());
            return SET_PREFIX + name;
        } catch (NoSuchMethodException ignored) {
        }
        try {
            clazz.getDeclaredMethod(SET_PREFIX + name, method.getReturnType());
            return SET_PREFIX + name;
        } catch (NoSuchMethodException e) {
            return null;
        }
    }
}
