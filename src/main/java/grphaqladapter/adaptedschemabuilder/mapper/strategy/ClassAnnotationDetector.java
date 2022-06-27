package grphaqladapter.adaptedschemabuilder.mapper.strategy;


public interface ClassAnnotationDetector {

    default boolean skipType(Class clazz) {
        return false;
    };

    TypeAnnotations detectAnnotationFor(Class clazz);
}
