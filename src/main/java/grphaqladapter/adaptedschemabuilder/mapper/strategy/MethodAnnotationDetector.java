package grphaqladapter.adaptedschemabuilder.mapper.strategy;


import grphaqladapter.adaptedschemabuilder.mapped.MappedClass;
import grphaqladapter.annotations.GraphqlDescriptionAnnotation;
import grphaqladapter.annotations.GraphqlFieldAnnotation;
import grphaqladapter.annotations.GraphqlInputFieldAnnotation;

import java.lang.reflect.Method;

public interface MethodAnnotationDetector {

    default boolean skipField(Method method, Class clazz, MappedClass.MappedType mappedType) {
        return false;
    }

    default boolean skipInputField(Method method, Class clazz, MappedClass.MappedType mappedType) {
        return false;
    }

    GraphqlFieldAnnotation detectFieldAnnotation(Method method, Class clazz, MappedClass.MappedType mappedType);

    GraphqlInputFieldAnnotation detectInputFieldAnnotation(Method method, Class clazz, MappedClass.MappedType mappedType);

    GraphqlDescriptionAnnotation detectDescriptionAnnotation(Method method, Class clazz, MappedClass.MappedType mappedType);

}
