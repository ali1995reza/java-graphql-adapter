package tests.T1.schema.directives;

import graphql.introspection.Introspection;
import grphaqladapter.annotations.GraphqlDirective;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@GraphqlDirective(locations = Introspection.DirectiveLocation.FIELD_DEFINITION, functionality = AddPageParametersDirectiveFunction.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface AddPageParameters {
}
