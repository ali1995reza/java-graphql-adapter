package grphaqladapter.annotations.impl.field;

import grphaqladapter.annotations.GraphqlInputFieldAnnotation;

public class GraphqlInputFieldAnnotationBuilder {


    private String inputFieldName;
    private boolean nullable;
    private String setter;

    private GraphqlInputFieldAnnotationBuilder() {
    }

    public final static GraphqlInputFieldAnnotationBuilder newBuilder() {
        return new GraphqlInputFieldAnnotationBuilder();
    }

    public synchronized GraphqlInputFieldAnnotationBuilder setNullable(boolean nullable) {
        this.nullable = nullable;
        return this;
    }

    public synchronized GraphqlInputFieldAnnotationBuilder setSetter(String setter) {
        this.setter = setter;
        return this;
    }

    public synchronized GraphqlInputFieldAnnotationBuilder setInputFieldName(String inputFieldName) {
        this.inputFieldName = inputFieldName;
        return this;
    }


    public synchronized GraphqlInputFieldAnnotationBuilder refresh() {
        inputFieldName = null;
        nullable = false;
        setter = null;

        return this;
    }

    public synchronized GraphqlInputFieldAnnotation build() {
        return new GraphqlInputFieldAnnotationImpl(inputFieldName, nullable, setter);
    }
}
