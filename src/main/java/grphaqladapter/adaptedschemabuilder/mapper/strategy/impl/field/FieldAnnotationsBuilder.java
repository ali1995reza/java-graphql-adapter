package grphaqladapter.adaptedschemabuilder.mapper.strategy.impl.field;

import grphaqladapter.adaptedschemabuilder.mapper.strategy.FieldAnnotations;
import grphaqladapter.annotations.GraphqlFieldAnnotation;
import grphaqladapter.annotations.GraphqlInputFieldAnnotation;

public final class FieldAnnotationsBuilder {

    public static FieldAnnotationsBuilder newBuilder()
    {
        return new FieldAnnotationsBuilder();
    }




    private GraphqlFieldAnnotation fieldAnnotation;
    private GraphqlInputFieldAnnotation inputFieldAnnotation;

    private FieldAnnotationsBuilder(){}



    public FieldAnnotationsBuilder setFieldAnnotation(GraphqlFieldAnnotation fieldAnnotation) {
        this.fieldAnnotation = fieldAnnotation;
        return this;
    }

    public FieldAnnotationsBuilder setInputFieldAnnotation(GraphqlInputFieldAnnotation inputFieldAnnotation) {
        this.inputFieldAnnotation = inputFieldAnnotation;
        return this;
    }

    public FieldAnnotations build()
    {
        return new FieldAnnotationsImpl(fieldAnnotation , inputFieldAnnotation);
    }
}
