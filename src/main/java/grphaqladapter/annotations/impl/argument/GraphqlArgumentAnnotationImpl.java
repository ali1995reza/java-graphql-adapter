package grphaqladapter.annotations.impl.argument;


import grphaqladapter.annotations.GraphqlArgumentAnnotation;

final class GraphqlArgumentAnnotationImpl implements GraphqlArgumentAnnotation {


    private final String name;
    private final boolean nullable;

    GraphqlArgumentAnnotationImpl(String name, boolean nullable) {

        this.name = name;
        this.nullable = nullable;

    }


    @Override
    public String name() {
        return name;
    }

    @Override
    public boolean nullable() {
        return nullable;
    }

}
