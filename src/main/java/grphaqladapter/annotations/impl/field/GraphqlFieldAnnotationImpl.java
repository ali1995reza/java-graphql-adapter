package grphaqladapter.annotations.impl.field;


import grphaqladapter.annotations.GraphqlFieldAnnotation;

class GraphqlFieldAnnotationImpl implements GraphqlFieldAnnotation {


    private final String name;
    private final boolean nullable;

    public GraphqlFieldAnnotationImpl(String name, boolean nullable) {
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
