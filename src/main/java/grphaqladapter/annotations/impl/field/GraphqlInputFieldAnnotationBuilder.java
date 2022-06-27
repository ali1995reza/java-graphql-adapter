package grphaqladapter.annotations.impl.field;

import grphaqladapter.annotations.GraphqlInputFieldAnnotation;
import grphaqladapter.annotations.impl.NullableContainerBuilder;

public class GraphqlInputFieldAnnotationBuilder extends NullableContainerBuilder<GraphqlInputFieldAnnotationBuilder, GraphqlInputFieldAnnotation> {

    public static GraphqlInputFieldAnnotationBuilder newBuilder() {
        return new GraphqlInputFieldAnnotationBuilder();
    }

    private String setter;

    private GraphqlInputFieldAnnotationBuilder() {
    }

    public GraphqlInputFieldAnnotationBuilder setter(String setter) {
        this.setter = setter;
        return this;
    }

    public String setter() {
        return setter;
    }

    public GraphqlInputFieldAnnotation build() {
        return new GraphqlInputFieldAnnotationImpl(name(), isNullable(), setter());
    }
}
