package grphaqladapter.adaptedschemabuilder.mapper.strategy.impl.field;

import grphaqladapter.adaptedschemabuilder.assertutil.Assert;
import grphaqladapter.adaptedschemabuilder.mapper.strategy.FieldAnnotations;
import grphaqladapter.adaptedschemabuilder.validator.FieldValidator;
import grphaqladapter.annotations.GraphqlDescriptionAnnotation;
import grphaqladapter.annotations.GraphqlFieldAnnotation;
import grphaqladapter.annotations.GraphqlInputFieldAnnotation;

final class FieldAnnotationsImpl implements FieldAnnotations {


    private final GraphqlFieldAnnotation fieldAnnotation;
    private final GraphqlInputFieldAnnotation inputFieldAnnotation;
    private final GraphqlDescriptionAnnotation descriptionAnnotation;

    public FieldAnnotationsImpl(GraphqlFieldAnnotation fieldAnnotation,
                                GraphqlInputFieldAnnotation inputFieldAnnotation,
                                GraphqlDescriptionAnnotation descriptionAnnotation) {
        this.fieldAnnotation = fieldAnnotation;
        this.inputFieldAnnotation = inputFieldAnnotation;
        this.descriptionAnnotation = descriptionAnnotation;

        Assert.isOneFalse("at least one of FieldAnnotation or InputFieldAnnotation must present",
                fieldAnnotation == null, inputFieldAnnotation == null);

        FieldValidator.validate(fieldAnnotation, true);
        FieldValidator.validate(fieldAnnotation, true);
    }


    @Override
    public GraphqlFieldAnnotation fieldAnnotation() {
        return fieldAnnotation;
    }

    @Override
    public GraphqlInputFieldAnnotation inputFiledAnnotation() {
        return inputFieldAnnotation;
    }

    @Override
    public GraphqlDescriptionAnnotation descriptionAnnotation() {
        return descriptionAnnotation;
    }
}
