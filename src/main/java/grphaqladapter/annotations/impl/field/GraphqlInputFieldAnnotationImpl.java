package grphaqladapter.annotations.impl.field;

import grphaqladapter.annotations.GraphqlInputFieldAnnotation;

class GraphqlInputFieldAnnotationImpl implements GraphqlInputFieldAnnotation {


    private final String inputFieldName;
    private final boolean nullable;
    private final String setter;

    GraphqlInputFieldAnnotationImpl(String inputFieldName, boolean nullable, String setter) {
        this.inputFieldName = inputFieldName;
        this.nullable = nullable;
        this.setter = setter;
    }


    @Override
    public String inputFieldName() {
        return inputFieldName;
    }

    @Override
    public boolean nullable() {
        return nullable;
    }

    @Override
    public String setter() {
        return setter;
    }
}
