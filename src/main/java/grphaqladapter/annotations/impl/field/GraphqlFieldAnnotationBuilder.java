package grphaqladapter.annotations.impl.field;

import grphaqladapter.annotations.GraphqlFieldAnnotation;

public class GraphqlFieldAnnotationBuilder {

    private String fieldName;
    private boolean nullable;

    private GraphqlFieldAnnotationBuilder() {
    }

    public static GraphqlFieldAnnotationBuilder newBuilder() {
        return new GraphqlFieldAnnotationBuilder();
    }

    public synchronized GraphqlFieldAnnotationBuilder setNullable(boolean nullable) {
        this.nullable = nullable;
        return this;
    }

    public synchronized GraphqlFieldAnnotationBuilder setFieldName(String fieldName) {
        this.fieldName = fieldName;
        return this;
    }

    public synchronized GraphqlFieldAnnotationBuilder refresh() {
        fieldName = null;
        nullable = false;

        return this;
    }

    public synchronized GraphqlFieldAnnotation build() {
        return new GraphqlFieldAnnotationImpl(fieldName, nullable);
    }
}
