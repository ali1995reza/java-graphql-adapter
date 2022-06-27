package grphaqladapter.annotations.impl;

import grphaqladapter.annotations.GraphqlElementAnnotation;

public class GraphqlElementAnnotationImpl implements GraphqlElementAnnotation {

    private final String name;

    public GraphqlElementAnnotationImpl(String name) {
        this.name = name;
    }

    @Override
    public String name() {
        return name;
    }
}
