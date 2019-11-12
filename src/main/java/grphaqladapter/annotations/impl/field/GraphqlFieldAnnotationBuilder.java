package grphaqladapter.annotations.impl.field;

import grphaqladapter.annotations.GraphqlFieldAnnotation;

public class GraphqlFieldAnnotationBuilder {


    public final static GraphqlFieldAnnotationBuilder newBuilder()
    {
        return new GraphqlFieldAnnotationBuilder();
    }



    private String fieldName;
    private boolean nullable;
    private boolean inputField;
    private String setter;

    private GraphqlFieldAnnotationBuilder(){}


    public synchronized GraphqlFieldAnnotationBuilder setNullable(boolean nullable) {
        this.nullable = nullable;
        return this;
    }

    public synchronized GraphqlFieldAnnotationBuilder setSetter(String setter) {
        this.setter = setter;
        return this;
    }

    public synchronized GraphqlFieldAnnotationBuilder setFieldName(String fieldName) {
        this.fieldName = fieldName;
        return this;
    }

    public synchronized GraphqlFieldAnnotationBuilder setInputField(boolean inputField) {
        this.inputField = inputField;
        return this;
    }

    public synchronized GraphqlFieldAnnotationBuilder refresh()
    {
        fieldName = null;
        nullable = false;
        inputField = false;
        setter = null;

        return this;
    }

    public synchronized GraphqlFieldAnnotation build()
    {
        return new GraphqlFieldAnnotationImpl(fieldName , nullable , inputField , setter);
    }
}
