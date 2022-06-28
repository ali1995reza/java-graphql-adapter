package tests.T1.schema.directives;

import graphql.introspection.Introspection;
import grphaqladapter.annotations.GraphqlDirective;
import grphaqladapter.annotations.GraphqlDirectiveArgument;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@GraphqlDirective(locations = {Introspection.DirectiveLocation.FIELD_DEFINITION, Introspection.DirectiveLocation.FIELD, Introspection.DirectiveLocation.QUERY}, functionality = DelayDirectiveFunction.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface Delay {
    @GraphqlDirectiveArgument(nullable = false, name = "seconds")
    int value();
}
