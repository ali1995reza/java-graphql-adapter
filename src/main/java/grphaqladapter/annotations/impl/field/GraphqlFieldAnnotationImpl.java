package grphaqladapter.annotations.impl.field;



import grphaqladapter.adaptedschemabuilder.validator.FieldValidator;
import grphaqladapter.annotations.GraphqlFieldAnnotation;

class GraphqlFieldAnnotationImpl implements GraphqlFieldAnnotation {


    private final String fieldName;
    private final boolean nullable;
    private final boolean inputField;
    private final String setter;

    public GraphqlFieldAnnotationImpl(String fieldName, boolean nullable, boolean inputField, String setter) {
        this.fieldName = fieldName;
        this.nullable = nullable;
        this.inputField = inputField;
        this.setter = setter;
        FieldValidator.validate(this);
    }


    @Override
    public String fieldName() {
        return fieldName;
    }

    @Override
    public boolean nullable() {
        return nullable;
    }

    @Override
    public boolean inputField() {
        return inputField;
    }

    @Override
    public String setter() {
        return setter;
    }
}
