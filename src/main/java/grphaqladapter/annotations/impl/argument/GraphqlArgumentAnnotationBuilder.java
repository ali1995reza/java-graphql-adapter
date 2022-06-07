package grphaqladapter.annotations.impl.argument;

import grphaqladapter.annotations.GraphqlArgumentAnnotation;

public class GraphqlArgumentAnnotationBuilder {


    private String argumentName;
    private boolean nullable;

    private GraphqlArgumentAnnotationBuilder() {
    }

    public static GraphqlArgumentAnnotationBuilder newBuilder() {
        return new GraphqlArgumentAnnotationBuilder();
    }

    public synchronized GraphqlArgumentAnnotationBuilder setArgumentName(String argumentName) {
        this.argumentName = argumentName;
        return this;
    }


    public synchronized GraphqlArgumentAnnotationBuilder setNullable(boolean nullable) {
        this.nullable = nullable;
        return this;
    }


    public synchronized GraphqlArgumentAnnotation build() {
        return new GraphqlArgumentAnnotationImpl(argumentName, nullable);
    }
}
