package grphaqladapter.annotations.impl.argument;

import grphaqladapter.annotations.GraphqlDirectiveArgumentAnnotation;
import grphaqladapter.annotations.interfaces.ValueParser;

public class GraphqlDirectiveArgumentAnnotationImpl implements GraphqlDirectiveArgumentAnnotation {


    private final String name;
    private final Class type;
    private final int dimensions;
    private final boolean nullable;
    private final Class<? extends ValueParser> valueParser;

    public GraphqlDirectiveArgumentAnnotationImpl(String name, Class type, int dimensions, boolean nullable, Class<? extends ValueParser> valueParser) {
        this.name = name;
        this.type = type;
        this.dimensions = dimensions;
        this.nullable = nullable;
        this.valueParser = valueParser;
    }

    @Override
    public Class type() {
        return type;
    }

    @Override
    public int dimensions() {
        return dimensions;
    }

    @Override
    public boolean nullable() {
        return nullable;
    }

    @Override
    public Class<? extends ValueParser> valueParser() {
        return valueParser;
    }

    @Override
    public String name() {
        return name;
    }
}
