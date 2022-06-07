package grphaqladapter.adaptedschemabuilder.mapper.strategy.impl.argument;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public class ParameterAnnotationLookup {

    public final static <T> T findFirstAppears(Method method, int parameterIndex, Class annotationClass) {

        Parameter parameter = method.getParameters()[parameterIndex];

        if (parameter.getAnnotation(annotationClass) != null) {
            return (T) parameter.getAnnotation(annotationClass);
        } else {

            if (method.getDeclaringClass().getSuperclass() != null) {
                try {

                    Method m = method.getDeclaringClass().getSuperclass().getMethod(method.getName(), method.getParameterTypes());
                    Object o = findFirstAppears(m, parameterIndex, annotationClass);
                    if (o != null)
                        return (T) o;
                } catch (NoSuchMethodException e) {
                }
            }
            for (Class cls : method.getDeclaringClass().getInterfaces()) {
                try {
                    Method m = cls.getMethod(method.getName(), method.getParameterTypes());
                    Object o = findFirstAppears(m, parameterIndex, annotationClass);
                    if (o != null)
                        return (T) o;
                } catch (NoSuchMethodException e) {
                }
            }
        }

        return null;
    }
}
