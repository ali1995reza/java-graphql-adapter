package grphaqladapter.annotations.impl.argument;

import grphaqladapter.annotations.GraphqlDirectiveArgumentAnnotation;
import grphaqladapter.annotations.impl.NullableContainerBuilder;
import grphaqladapter.annotations.interfaces.ValueParser;

public class GraphqlDirectiveArgumentAnnotationBuilder extends NullableContainerBuilder<GraphqlDirectiveArgumentAnnotationBuilder, GraphqlDirectiveArgumentAnnotation> {

    public static GraphqlDirectiveArgumentAnnotationBuilder newBuilder() {
        return new GraphqlDirectiveArgumentAnnotationBuilder();
    }

    private Class type;
    private int dimensions = 0;
    private Class<? extends ValueParser> valueParser;

    public GraphqlDirectiveArgumentAnnotationBuilder type(Class type) {
        this.type = type;
        return this;
    }

    public Class type() {
        return type;
    }

    public GraphqlDirectiveArgumentAnnotationBuilder dimensions(int dimensions) {
        this.dimensions = dimensions;
        return this;
    }

    public int dimensions() {
        return dimensions;
    }

    public GraphqlDirectiveArgumentAnnotationBuilder valueParser(Class<? extends ValueParser> valueParser) {
        this.valueParser = valueParser;
        return this;
    }

    public Class<? extends ValueParser> valueParser() {
        return valueParser;
    }

    public GraphqlDirectiveArgumentAnnotation build() {
        return new GraphqlDirectiveArgumentAnnotationImpl(name(), type(), dimensions(), isNullable(), valueParser());
    }
}
