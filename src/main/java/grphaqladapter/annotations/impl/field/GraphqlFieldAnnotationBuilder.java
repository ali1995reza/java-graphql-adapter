package grphaqladapter.annotations.impl.field;

import grphaqladapter.annotations.GraphqlFieldAnnotation;
import grphaqladapter.annotations.impl.NullableContainerBuilder;

public class GraphqlFieldAnnotationBuilder extends NullableContainerBuilder<GraphqlFieldAnnotationBuilder, GraphqlFieldAnnotation> {

    public static GraphqlFieldAnnotationBuilder newBuilder() {
        return new GraphqlFieldAnnotationBuilder();
    }

    private GraphqlFieldAnnotationBuilder() {
    }

    public synchronized GraphqlFieldAnnotation build() {
        return new GraphqlFieldAnnotationImpl(name(), isNullable());
    }
}
