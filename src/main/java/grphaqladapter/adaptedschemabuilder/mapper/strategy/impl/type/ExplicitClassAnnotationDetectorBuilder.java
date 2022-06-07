package grphaqladapter.adaptedschemabuilder.mapper.strategy.impl.type;

import grphaqladapter.adaptedschemabuilder.mapper.strategy.ClassAnnotationDetector;
import grphaqladapter.adaptedschemabuilder.mapper.strategy.TypeAnnotations;
import grphaqladapter.adaptedschemabuilder.utils.Utils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ExplicitClassAnnotationDetectorBuilder {


    private final Map<Class, TypeAnnotations> annotationsMap = new HashMap<>();

    private ExplicitClassAnnotationDetectorBuilder() {
    }

    public final static ExplicitClassAnnotationDetectorBuilder newBuilder() {
        return new ExplicitClassAnnotationDetectorBuilder();
    }

    public synchronized ExplicitClassAnnotationDetectorBuilder setAnnotation(Class cls, TypeAnnotations annotations) {
        annotationsMap.put(cls, annotations);
        return this;
    }

    public synchronized ExplicitClassAnnotationDetectorBuilder removeAnnotation(Class cls) {
        annotationsMap.remove(cls);
        return this;
    }

    public synchronized ExplicitClassAnnotationDetectorBuilder clear() {
        annotationsMap.clear();
        return this;
    }

    public synchronized ClassAnnotationDetector build() {
        return new ClassAnnotationDetectorImpl(Collections.unmodifiableMap(Utils.copy(annotationsMap)));
    }

}
