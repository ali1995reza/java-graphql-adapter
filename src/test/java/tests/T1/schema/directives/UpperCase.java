package tests.T1.schema.directives;

import graphql.introspection.Introspection;
import grphaqladapter.annotations.GraphqlDirective;
import grphaqladapter.annotations.GraphqlDirectiveArgument;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@GraphqlDirective(locations = {Introspection.DirectiveLocation.FIELD, Introspection.DirectiveLocation.FIELD_DEFINITION, Introspection.DirectiveLocation.QUERY, Introspection.DirectiveLocation.INLINE_FRAGMENT, Introspection.DirectiveLocation.FRAGMENT_DEFINITION, Introspection.DirectiveLocation.FRAGMENT_SPREAD} , functionality = UpperCaseDirectiveFunction.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface UpperCase {
    @GraphqlDirectiveArgument(nullable = false)
    boolean isActive() default true;
}
