package grphaqladapter.annotations;

import graphql.introspection.Introspection;
import grphaqladapter.annotations.interfaces.GraphqlDirectiveFunction;

public interface GraphqlDirectiveAnnotation extends GraphqlElementAnnotation {

    Introspection.DirectiveLocation[] locations();

    Class<? extends GraphqlDirectiveFunction> functionality();

}
