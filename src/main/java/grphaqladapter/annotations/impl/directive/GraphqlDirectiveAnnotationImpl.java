package grphaqladapter.annotations.impl.directive;

import graphql.introspection.Introspection;
import grphaqladapter.annotations.GraphqlDirectiveAnnotation;
import grphaqladapter.annotations.impl.GraphqlElementAnnotationImpl;
import grphaqladapter.annotations.interfaces.GraphqlDirectiveFunction;

public class GraphqlDirectiveAnnotationImpl extends GraphqlElementAnnotationImpl implements GraphqlDirectiveAnnotation {

    private final Introspection.DirectiveLocation[] locations;
    private final Class<? extends GraphqlDirectiveFunction> functionality;

    GraphqlDirectiveAnnotationImpl(String name, Introspection.DirectiveLocation[] locations, Class<? extends GraphqlDirectiveFunction> functionality) {
        super(name);
        this.locations = locations;
        this.functionality = functionality;
    }

    @Override
    public Introspection.DirectiveLocation[] locations() {
        return locations;
    }

    @Override
    public Class<? extends GraphqlDirectiveFunction> functionality() {
        return functionality;
    }
}
