package grphaqladapter.annotations.impl;

import grphaqladapter.annotations.GraphqlElementAnnotation;

public abstract class NullableContainerBuilder<T extends NullableContainerBuilder<T, E>, E extends GraphqlElementAnnotation> extends GraphqlElementAnnotationBuilder<T, E> {

    private boolean nullable;

    public T nullable(boolean nullable) {
        this.nullable = nullable;
        return (T) this;
    }

    public boolean isNullable() {
        return nullable;
    }
}
