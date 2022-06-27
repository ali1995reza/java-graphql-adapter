package grphaqladapter.annotations.impl.argument;

import grphaqladapter.annotations.GraphqlArgumentAnnotation;
import grphaqladapter.annotations.impl.NullableContainerBuilder;

public class GraphqlArgumentAnnotationBuilder extends NullableContainerBuilder<GraphqlArgumentAnnotationBuilder, GraphqlArgumentAnnotation> {

    public static GraphqlArgumentAnnotationBuilder newBuilder() {
        return new GraphqlArgumentAnnotationBuilder();
    }

    public synchronized GraphqlArgumentAnnotation build() {
        return new GraphqlArgumentAnnotationImpl(name(), isNullable());
    }
}
