package grphaqladapter.adaptedschemabuilder.mapper;

import grphaqladapter.adaptedschemabuilder.assertutil.Assert;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class MappingStatics {

    public static TypeDetails findTypeDetails(Method method) {
        Type type = method.getGenericReturnType();

        if (CompletableFuture.class.isAssignableFrom(method.getReturnType())) {
            if (type instanceof ParameterizedType) {
                //so handle it please !
                ParameterizedType paraType = (ParameterizedType) type;
                Type inner = paraType.getActualTypeArguments()[0];
                TypeDetails details = findTypeDetails(inner);

                return new TypeDetails(details.type, details.dimension, false);

            } else {
                return new TypeDetails(Object.class, 0, false);
            }
        }
        return findTypeDetails(type);
    }

    public static TypeDetails findTypeDetails(Parameter parameter) {
        return findTypeDetails(parameter.getParameterizedType());
    }

    public static TypeDetails findTypeDetails(Type type) {
        if (type == null)
            return null;

        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            if (parameterizedType.getRawType() != List.class)
                return new TypeDetails((Class) parameterizedType.getRawType(), 0, false);

            Type innerType = parameterizedType.getActualTypeArguments()[0];
            return detectDimsDetails(innerType, 1);
        } else if (type instanceof Class) {
            Class clazz = (Class) type;
            if (clazz == List.class)
                return new TypeDetails(Object.class, 1, false);
            else if(clazz.isArray())
                return forArray(clazz);
            else
                return new TypeDetails(clazz, 0, false);
        }

        throw new IllegalStateException("unknown type class");

    }

    private static TypeDetails detectDimsDetails(Type type, int dims) {
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            if (parameterizedType.getRawType() == List.class) {
                return detectDimsDetails(parameterizedType.getActualTypeArguments()[0], dims + 1);
            } else {
                return new TypeDetails((Class) parameterizedType.getRawType(), dims, false);
            }
        } else if (type instanceof Class) {
            Class clazz = (Class) type;
            if (clazz == List.class)
                return new TypeDetails(Object.class, dims + 1, false);
            else
                return new TypeDetails(clazz, dims, false);
        }

        throw new IllegalStateException("unknown type class");

    }

    private static TypeDetails forArray(Class clazz) {
        int dims = 0;
        while (clazz.isArray()) {
            ++dims;
            clazz = clazz.getComponentType();
        }
        return new TypeDetails(clazz, dims, true);
    }

    public static List<Method> getAllMethods(Class cls) {
        Method[] methods = cls.getMethods();
        Method[] declaredMethods = cls.getDeclaredMethods();
        List<Method> list = new ArrayList<>();

        if (methods != null) {
            for (Method method : methods) {
                if (!method.isSynthetic())
                    list.add(method);
            }
        }

        if (declaredMethods != null) {
            for (Method method : declaredMethods) {
                if (!list.contains(method) && !method.isSynthetic())
                    list.add(method);
            }
        }

        return list;
    }

    public static final class TypeDetails {

        private final Class type;
        private final int dimension;
        private final boolean isArray;

        private TypeDetails(Class type, int dimension, boolean isArray) {
            this.isArray = isArray;
            Assert.isNotNegative(dimension, new IllegalStateException("an array dimension can not be <0"));
            Assert.isNotNull(type, new NullPointerException("provided type is null"));
            this.type = type;
            this.dimension = dimension;
        }


        public Class type() {
            return type;
        }

        public int dimension() {
            return dimension;
        }

        public boolean isArray() {
            return isArray;
        }

        @Override
        public String toString() {
            return "TypeDetails{" +
                    "type=" + type +
                    ", dimension=" + dimension +
                    ", isArray=" + isArray +
                    '}';
        }
    }

}
