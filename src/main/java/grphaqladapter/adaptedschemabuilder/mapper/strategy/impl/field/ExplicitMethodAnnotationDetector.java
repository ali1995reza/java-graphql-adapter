package grphaqladapter.adaptedschemabuilder.mapper.strategy.impl.field;

import grphaqladapter.adaptedschemabuilder.mapped.MappedElementType;
import grphaqladapter.adaptedschemabuilder.mapper.strategy.MethodAnnotationDetector;
import grphaqladapter.adaptedschemabuilder.utils.Utils;
import grphaqladapter.annotations.GraphqlDescriptionAnnotation;
import grphaqladapter.annotations.GraphqlDirectiveArgumentAnnotation;
import grphaqladapter.annotations.GraphqlFieldAnnotation;
import grphaqladapter.annotations.GraphqlInputFieldAnnotation;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;

class ExplicitMethodAnnotationDetector implements MethodAnnotationDetector {

    private final Map<Method, GraphqlFieldAnnotation> explicitFieldAnnotations;
    private final Map<Method, GraphqlInputFieldAnnotation> explicitInputFieldAnnotations;

    ExplicitMethodAnnotationDetector(Map<Method, GraphqlFieldAnnotation> explicitFieldAnnotations, Map<Method, GraphqlInputFieldAnnotation> explicitInputFieldAnnotations) {
        this.explicitFieldAnnotations = Utils.nullifyOrGetDefault(explicitFieldAnnotations, Collections.emptyMap());
        this.explicitInputFieldAnnotations = Utils.nullifyOrGetDefault(explicitInputFieldAnnotations, Collections.emptyMap());
    }

    @Override
    public GraphqlFieldAnnotation detectFieldAnnotation(Method method, Class clazz, MappedElementType mappedElementType) {
        return this.explicitFieldAnnotations.get(method);
    }

    @Override
    public GraphqlInputFieldAnnotation detectInputFieldAnnotation(Method method, Class clazz, MappedElementType mappedElementType) {
        return this.explicitInputFieldAnnotations.get(method);
    }

    @Override
    public GraphqlDirectiveArgumentAnnotation detectDirectiveArgumentAnnotation(Method method, Class clazz, MappedElementType mappedElementType) {
        return null;
    }

    @Override
    public GraphqlDescriptionAnnotation detectDescriptionAnnotation(Method method, Class clazz, MappedElementType mappedElementType) {
        return null;
    }

}
