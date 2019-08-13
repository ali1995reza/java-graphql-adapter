package grphaqladapter.adaptedschemabuilder.mapper.strategy.impl.field;

import grphaqladapter.adaptedschemabuilder.assertutil.Assert;
import grphaqladapter.adaptedschemabuilder.mapper.strategy.FieldAnnotations;
import grphaqladapter.adaptedschemabuilder.validator.FieldValidator;
import grphaqladapter.annotations.GraphqlFieldAnnotation;
import grphaqladapter.annotations.GraphqlInputFieldAnnotation;

final class FieldAnnotationsImpl implements FieldAnnotations {


    private final GraphqlFieldAnnotation fieldAnnotation;
    private final GraphqlInputFieldAnnotation inputFieldAnnotation;

    public FieldAnnotationsImpl(GraphqlFieldAnnotation fieldAnnotation, GraphqlInputFieldAnnotation inputFieldAnnotation) {
        this.fieldAnnotation = fieldAnnotation;
        this.inputFieldAnnotation = inputFieldAnnotation;

        Assert.ifConditionTrue("at least one of FieldAnnotation or InputFieldAnnotation must present" ,
                fieldAnnotation==null , inputFieldAnnotation==null);

        FieldValidator.validate(fieldAnnotation , true);
        FieldValidator.validate(fieldAnnotation , true);
    }



    @Override
    public GraphqlFieldAnnotation fieldAnnotation() {
        return fieldAnnotation;
    }

    @Override
    public GraphqlInputFieldAnnotation inputFiledAnnotation() {
        return inputFieldAnnotation;
    }
}
