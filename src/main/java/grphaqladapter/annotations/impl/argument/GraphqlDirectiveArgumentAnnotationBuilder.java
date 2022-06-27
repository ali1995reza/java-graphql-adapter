package grphaqladapter.annotations.impl.argument;

import grphaqladapter.annotations.GraphqlDirectiveArgumentAnnotation;
import grphaqladapter.annotations.interfaces.ValueParser;

public class GraphqlDirectiveArgumentAnnotationBuilder {

    public static GraphqlDirectiveArgumentAnnotationBuilder newBuilder(){
        return new GraphqlDirectiveArgumentAnnotationBuilder();
    }

    private String name;
    private Class type;
    private int dimensions = 0;
    private Class<? extends ValueParser> valueParser;
    private boolean nullable = true;

    public GraphqlDirectiveArgumentAnnotationBuilder name(String name) {
        this.name = name;
        return this;
    }

    public GraphqlDirectiveArgumentAnnotationBuilder type(Class type) {
        this.type = type;
        return this;
    }

    public GraphqlDirectiveArgumentAnnotationBuilder dimensions(int dimensions) {
        this.dimensions = dimensions;
        return this;
    }

    public GraphqlDirectiveArgumentAnnotationBuilder valueParser(Class<? extends ValueParser> valueParser) {
        this.valueParser = valueParser;
        return this;
    }

    public GraphqlDirectiveArgumentAnnotationBuilder nullable(boolean nullable) {
        this.nullable = nullable;
        return this;
    }

    public GraphqlDirectiveArgumentAnnotationBuilder refresh() {
        this.name = null;
        this.type = null;
        this.dimensions = 0;
        this.valueParser = null;
        this.nullable = true;
        return this;
    }

    public GraphqlDirectiveArgumentAnnotation build() {
        return new GraphqlDirectiveArgumentAnnotationImpl(name, type, dimensions, nullable, valueParser);
    }
}
