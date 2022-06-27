package grphaqladapter.annotations.impl;

import grphaqladapter.annotations.GraphqlElementAnnotation;

public abstract class GraphqlElementAnnotationBuilder<T extends GraphqlElementAnnotationBuilder<T, E>, E extends GraphqlElementAnnotation> {

    private String name;

    public T name(String name) {
        this.name = name;
        return (T) this;
    }

    public String name() {
        return name;
    }

    public abstract E build();
}
