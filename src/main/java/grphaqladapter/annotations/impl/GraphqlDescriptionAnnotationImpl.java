package grphaqladapter.annotations.impl;

import grphaqladapter.annotations.GraphqlDescriptionAnnotation;

public class GraphqlDescriptionAnnotationImpl implements GraphqlDescriptionAnnotation {

    private final String value;

    public GraphqlDescriptionAnnotationImpl(String value) {
        this.value = value;
    }

    @Override
    public String value() {
        return value;
    }
}
