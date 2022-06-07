package grphaqladapter.adaptedschemabuilder.mapper.strategy;

import grphaqladapter.annotations.GraphqlDescriptionAnnotation;
import grphaqladapter.annotations.GraphqlFieldAnnotation;
import grphaqladapter.annotations.GraphqlInputFieldAnnotation;

public interface FieldAnnotations {

    GraphqlFieldAnnotation fieldAnnotation();

    GraphqlInputFieldAnnotation inputFiledAnnotation();

    GraphqlDescriptionAnnotation descriptionAnnotation();
}
