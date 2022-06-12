package grphaqladapter.adaptedschemabuilder.mapper.strategy.impl.argument;

import grphaqladapter.adaptedschemabuilder.mapper.strategy.ParameterAnnotationDetector;
import grphaqladapter.adaptedschemabuilder.utils.Utils;
import grphaqladapter.annotations.GraphqlArgumentAnnotation;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Collections;
import java.util.Map;

class ParameterAnnotationDetectorImpl implements ParameterAnnotationDetector {

    private final Map<Parameter, GraphqlArgumentAnnotation> parameterMap;
    private final Map<Integer, GraphqlArgumentAnnotation> indexMap;

    ParameterAnnotationDetectorImpl(Map<Parameter, GraphqlArgumentAnnotation> parameterMap, Map<Integer, GraphqlArgumentAnnotation> indexMap) {
        this.parameterMap = Utils.nullifyOrGetDefault(parameterMap, Collections.EMPTY_MAP);
        this.indexMap = Utils.nullifyOrGetDefault(indexMap, Collections.EMPTY_MAP);
    }

    @Override
    public GraphqlArgumentAnnotation detectArgumentAnnotation(Method method, Parameter parameter, int parameterIndex) {

        GraphqlArgumentAnnotation argumentAnnotations = parameterMap.get(parameter);
        if (argumentAnnotations != null)
            return argumentAnnotations;


        return indexMap.get(parameterIndex);
    }
}
