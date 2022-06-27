package tests.T1.schema.directives;

import graphql.introspection.Introspection;
import grphaqladapter.annotations.GraphqlDirective;
import grphaqladapter.annotations.GraphqlDirectiveArgument;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@GraphqlDirective(locations = {Introspection.DirectiveLocation.FIELD, Introspection.DirectiveLocation.QUERY, Introspection.DirectiveLocation.INLINE_FRAGMENT, Introspection.DirectiveLocation.FRAGMENT_DEFINITION, Introspection.DirectiveLocation.FRAGMENT_SPREAD} , functionality = AuthenticationDirectiveFunction.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface Authentication {

    @GraphqlDirectiveArgument
    String token();
}
