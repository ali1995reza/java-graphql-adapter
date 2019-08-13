package grphaqladapter.adaptedschemabuilder.mapper.strategy;

public interface MappingStrategy {

    ClassAnnotationDetector annotationDetector();
    //so check other options!
    boolean mapNewDetectedClasses();
    //and so on !
    //some options needed really !
}
