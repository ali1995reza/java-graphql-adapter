package grphaqladapter.adaptedschemabuilder.mapper.strategy.impl.field;

import grphaqladapter.adaptedschemabuilder.mapper.strategy.MethodAnnotationDetector;
import grphaqladapter.adaptedschemabuilder.utils.Utils;
import grphaqladapter.annotations.GraphqlFieldAnnotation;
import grphaqladapter.annotations.GraphqlInputFieldAnnotation;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class ExplicitMethodAnnotationDetectorBuilder {

    private final Map<Method, GraphqlFieldAnnotation> fieldAnnotations = new HashMap<>();
    private final Map<Method, GraphqlInputFieldAnnotation> inputFieldAnnotations = new HashMap<>();
    private ExplicitMethodAnnotationDetectorBuilder explicitMethodAnnotationDetectorBuilder;


    private ExplicitMethodAnnotationDetectorBuilder() {

    }

    public final static ExplicitMethodAnnotationDetectorBuilder newBuilder() {
        return new ExplicitMethodAnnotationDetectorBuilder();
    }

    public synchronized ExplicitMethodAnnotationDetectorBuilder setFieldAnnotation(Method method, GraphqlFieldAnnotation annotation) {
        this.fieldAnnotations.put(method, annotation);
        return this;
    }

    public synchronized ExplicitMethodAnnotationDetectorBuilder setInputFieldAnnotation(Method method, GraphqlInputFieldAnnotation annotation) {
        this.inputFieldAnnotations.put(method, annotation);
        return this;
    }

    public synchronized ExplicitMethodAnnotationDetectorBuilder removeFieldAnnotation(Method method) {
        this.fieldAnnotations.remove(method);
        return this;
    }

    public synchronized ExplicitMethodAnnotationDetectorBuilder removeInputFieldAnnotation(Method method) {
        this.inputFieldAnnotations.remove(method);
        return this;
    }

    public synchronized ExplicitMethodAnnotationDetectorBuilder clear() {
        this.inputFieldAnnotations.clear();
        this.fieldAnnotations.clear();
        return this;
    }

    public synchronized MethodAnnotationDetector build() {

        return new ExplicitMethodAnnotationDetector(Utils.copy(fieldAnnotations), Utils.copy(inputFieldAnnotations));
    }
}
