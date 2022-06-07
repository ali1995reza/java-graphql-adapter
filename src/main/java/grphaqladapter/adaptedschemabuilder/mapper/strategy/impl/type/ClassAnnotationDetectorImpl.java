package grphaqladapter.adaptedschemabuilder.mapper.strategy.impl.type;

import grphaqladapter.adaptedschemabuilder.mapper.strategy.ClassAnnotationDetector;
import grphaqladapter.adaptedschemabuilder.mapper.strategy.TypeAnnotations;
import grphaqladapter.adaptedschemabuilder.utils.Utils;

import java.util.Collections;
import java.util.Map;

class ClassAnnotationDetectorImpl implements ClassAnnotationDetector {


    private final Map<Class, TypeAnnotations> explicitAnnotations;

    ClassAnnotationDetectorImpl(Map<Class, TypeAnnotations> explicitAnnotations) {
        this.explicitAnnotations = Utils.nullifyOrGetDefault(explicitAnnotations, Collections.EMPTY_MAP);
    }


    @Override
    public TypeAnnotations detectAnnotationFor(Class cls) {
        return explicitAnnotations.get(cls);
    }
}
