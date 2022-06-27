package grphaqladapter.adaptedschemabuilder.mapper.strategy;

import grphaqladapter.annotations.GraphqlArgumentAnnotation;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public interface ParameterAnnotationDetector {

    default boolean skip(Method method, Parameter parameter, int parameterIndex) {
        return false;
    }

    default boolean isSystemParameter(Method method, Parameter parameter, int parameterIndex) {
        return false;
    }

    GraphqlArgumentAnnotation detectArgumentAnnotation(Method method, Parameter parameter, int parameterIndex);

}
