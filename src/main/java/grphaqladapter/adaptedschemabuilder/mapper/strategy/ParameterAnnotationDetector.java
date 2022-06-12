package grphaqladapter.adaptedschemabuilder.mapper.strategy;

import grphaqladapter.annotations.GraphqlArgumentAnnotation;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public interface ParameterAnnotationDetector {

    GraphqlArgumentAnnotation detectArgumentAnnotation(Method method, Parameter parameter, int parameterIndex);

}
