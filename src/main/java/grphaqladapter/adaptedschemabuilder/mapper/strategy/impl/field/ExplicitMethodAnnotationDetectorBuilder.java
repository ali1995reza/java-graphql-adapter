package grphaqladapter.adaptedschemabuilder.mapper.strategy.impl.field;

import grphaqladapter.adaptedschemabuilder.mapper.strategy.FieldAnnotations;
import grphaqladapter.adaptedschemabuilder.mapper.strategy.MethodAnnotationDetector;
import grphaqladapter.adaptedschemabuilder.utils.Utils;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ExplicitMethodAnnotationDetectorBuilder {

    private final Map<Method, FieldAnnotations> annotationsMap = new HashMap<>();


    private ExplicitMethodAnnotationDetectorBuilder() {

    }

    public final static ExplicitMethodAnnotationDetectorBuilder newBuilder() {
        return new ExplicitMethodAnnotationDetectorBuilder();
    }

    public synchronized ExplicitMethodAnnotationDetectorBuilder setAnnotation(Method method, FieldAnnotations annotations) {
        annotationsMap.put(method, annotations);
        return this;
    }

    public synchronized ExplicitMethodAnnotationDetectorBuilder removeAnnotation(Method method) {
        annotationsMap.remove(method);
        return this;
    }

    public synchronized ExplicitMethodAnnotationDetectorBuilder clear() {
        annotationsMap.clear();
        return this;
    }

    public synchronized MethodAnnotationDetector build() {

        return new MethodAnnotationDetectorImpl(Collections.unmodifiableMap(Utils.copy(annotationsMap)));
    }
}
