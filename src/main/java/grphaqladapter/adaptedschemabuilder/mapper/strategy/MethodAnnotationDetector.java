package grphaqladapter.adaptedschemabuilder.mapper.strategy;


import grphaqladapter.adaptedschemabuilder.mapped.MappedElementType;
import grphaqladapter.annotations.GraphqlDescriptionAnnotation;
import grphaqladapter.annotations.GraphqlDirectiveArgumentAnnotation;
import grphaqladapter.annotations.GraphqlFieldAnnotation;
import grphaqladapter.annotations.GraphqlInputFieldAnnotation;

import java.lang.reflect.Method;

public interface MethodAnnotationDetector {

    default boolean skipField(Method method, Class clazz, MappedElementType mappedElementType) {
        return false;
    }

    default boolean skipInputField(Method method, Class clazz, MappedElementType mappedElementType) {
        return false;
    }

    default boolean skipDirectiveArgument(Method method, Class clazz, MappedElementType mappedElementType) {
        return false;
    }

    GraphqlFieldAnnotation detectFieldAnnotation(Method method, Class clazz, MappedElementType mappedElementType);

    GraphqlInputFieldAnnotation detectInputFieldAnnotation(Method method, Class clazz, MappedElementType mappedElementType);

    GraphqlDirectiveArgumentAnnotation detectDirectiveArgumentAnnotation(Method method, Class clazz, MappedElementType mappedElementType);

    GraphqlDescriptionAnnotation detectDescriptionAnnotation(Method method, Class clazz, MappedElementType mappedElementType);
}
