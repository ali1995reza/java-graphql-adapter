package grphaqladapter.adaptedschemabuilder.mapper.strategy.impl.argument;


import grphaqladapter.adaptedschemabuilder.mapper.strategy.ParameterAnnotationDetector;
import grphaqladapter.adaptedschemabuilder.utils.Utils;
import grphaqladapter.annotations.GraphqlArgumentAnnotation;

import java.lang.reflect.Parameter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ExplicitParameterAnnotationDetectorBuilder {

    public final static ExplicitParameterAnnotationDetectorBuilder newBuilder()
    {
        return new ExplicitParameterAnnotationDetectorBuilder();
    }

    private final HashMap<Parameter , GraphqlArgumentAnnotation> parameterMap = new HashMap<>();
    private final HashMap<Integer , GraphqlArgumentAnnotation> indexMap = new HashMap<>();

    private ExplicitParameterAnnotationDetectorBuilder(){}


    public synchronized ExplicitParameterAnnotationDetectorBuilder setAnnotation(Parameter parameter , GraphqlArgumentAnnotation argumentAnnotations)
    {
        parameterMap.put(parameter , argumentAnnotations);
        return this;
    }

    public synchronized ExplicitParameterAnnotationDetectorBuilder setAnnotation(int index , GraphqlArgumentAnnotation argumentAnnotations)
    {
        indexMap.put(index , argumentAnnotations);
        return this;
    }

    public synchronized ExplicitParameterAnnotationDetectorBuilder removeAnnotation(int index)
    {
        indexMap.remove(index);
        return this;
    }

    public synchronized ExplicitParameterAnnotationDetectorBuilder removeAnnotation(Parameter parameter)
    {
        parameterMap.remove(parameter);
        return this;
    }

    public synchronized ExplicitParameterAnnotationDetectorBuilder clear()
    {
        parameterMap.clear();
        indexMap.clear();
        return this;
    }

    public synchronized ParameterAnnotationDetector build()
    {
        Map<Parameter , GraphqlArgumentAnnotation> paraMap =
                Collections.unmodifiableMap(Utils.copy(parameterMap));
        Map<Integer , GraphqlArgumentAnnotation> indMap =
                Collections.unmodifiableMap(Utils.copy(indexMap));


        return new ParameterAnnotationDetectorImpl(paraMap , indMap);
    }
}
