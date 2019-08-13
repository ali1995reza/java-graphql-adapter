package grphaqladapter.adaptedschemabuilder.mapper.strategy;

import grphaqladapter.annotations.GraphqlArgumentAnnotation;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public interface ParameterAnnotationDetector {

    GraphqlArgumentAnnotation detectAnnotationFor(Parameter parameter , int parameterIndex);

}
