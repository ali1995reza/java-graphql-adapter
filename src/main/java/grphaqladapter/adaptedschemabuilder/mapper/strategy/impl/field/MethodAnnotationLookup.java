package grphaqladapter.adaptedschemabuilder.mapper.strategy.impl.field;

import java.lang.reflect.Method;

public class MethodAnnotationLookup {

    public final static <T> T findFirstAppears(Method method, Class annotationClass) {
        if (method.getAnnotation(annotationClass) != null) {
            return (T) method.getAnnotation(annotationClass);
        } else {

            if (method.getDeclaringClass().getSuperclass() != null) {
                try {

                    Method m = method.getDeclaringClass().getSuperclass().getMethod(method.getName(), method.getParameterTypes());
                    Object o = findFirstAppears(m, annotationClass);
                    if (o != null)
                        return (T) o;
                } catch (NoSuchMethodException e) {
                }
            }
            for (Class cls : method.getDeclaringClass().getInterfaces()) {
                try {
                    Method m = cls.getMethod(method.getName(), method.getParameterTypes());
                    Object o = findFirstAppears(m, annotationClass);
                    if (o != null)
                        return (T) o;
                } catch (NoSuchMethodException e) {
                }
            }
        }

        return null;
    }
}
