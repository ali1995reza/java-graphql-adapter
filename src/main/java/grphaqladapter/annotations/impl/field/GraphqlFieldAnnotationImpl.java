package grphaqladapter.annotations.impl.field;


import grphaqladapter.annotations.GraphqlFieldAnnotation;

class GraphqlFieldAnnotationImpl implements GraphqlFieldAnnotation {


    private final String fieldName;
    private final boolean nullable;

    public GraphqlFieldAnnotationImpl(String fieldName, boolean nullable) {
        this.fieldName = fieldName;
        this.nullable = nullable;
    }


    @Override
    public String fieldName() {
        return fieldName;
    }

    @Override
    public boolean nullable() {
        return nullable;
    }

}
