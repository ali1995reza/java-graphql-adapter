package grphaqladapter.adaptedschemabuilder.mapper.strategy.impl.field;

import grphaqladapter.adaptedschemabuilder.mapper.strategy.FieldAnnotations;
import grphaqladapter.adaptedschemabuilder.mapper.strategy.MethodAnnotationDetector;
import grphaqladapter.adaptedschemabuilder.utils.Utils;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

class MethodAnnotationDetectorImpl implements MethodAnnotationDetector {

    private final Map<Method , FieldAnnotations> explicitAnnotations;

    MethodAnnotationDetectorImpl(Map<Method, FieldAnnotations> explicitAnnotations) {
        this.explicitAnnotations = Utils.nullifyOrGetDefault(explicitAnnotations , Collections.EMPTY_MAP);
    }


    @Override
    public FieldAnnotations detectAnnotationFor(Method method) {
        return explicitAnnotations.get(method);
    }
}
