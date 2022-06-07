package grphaqladapter.adaptedschemabuilder.mapper.strategy.impl.field;

import grphaqladapter.adaptedschemabuilder.mapper.strategy.FieldAnnotations;
import grphaqladapter.annotations.GraphqlDescriptionAnnotation;
import grphaqladapter.annotations.GraphqlFieldAnnotation;
import grphaqladapter.annotations.GraphqlInputFieldAnnotation;

public final class FieldAnnotationsBuilder {

    private GraphqlFieldAnnotation fieldAnnotation;
    private GraphqlInputFieldAnnotation inputFieldAnnotation;
    private GraphqlDescriptionAnnotation descriptionAnnotation;

    private FieldAnnotationsBuilder() {
    }

    public static FieldAnnotationsBuilder newBuilder() {
        return new FieldAnnotationsBuilder();
    }

    public FieldAnnotationsBuilder setFieldAnnotation(GraphqlFieldAnnotation fieldAnnotation) {
        this.fieldAnnotation = fieldAnnotation;
        return this;
    }

    public FieldAnnotationsBuilder setInputFieldAnnotation(GraphqlInputFieldAnnotation inputFieldAnnotation) {
        this.inputFieldAnnotation = inputFieldAnnotation;
        return this;
    }

    public FieldAnnotationsBuilder setDescriptionAnnotation(GraphqlDescriptionAnnotation descriptionAnnotation) {
        this.descriptionAnnotation = descriptionAnnotation;
        return this;
    }

    public FieldAnnotationsBuilder refresh() {
        inputFieldAnnotation = null;
        fieldAnnotation = null;
        descriptionAnnotation = null;
        return this;
    }

    public FieldAnnotations build() {
        return new FieldAnnotationsImpl(fieldAnnotation, inputFieldAnnotation, descriptionAnnotation);
    }
}
