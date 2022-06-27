package grphaqladapter.annotations;

import graphql.introspection.Introspection;
import grphaqladapter.annotations.interfaces.DefaultGraphqlDirectiveFunction;
import grphaqladapter.annotations.interfaces.GraphqlDirectiveFunction;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface GraphqlDirective {

    String name() default "";

    Introspection.DirectiveLocation[] locations();

    Class<? extends GraphqlDirectiveFunction> functionality() default DefaultGraphqlDirectiveFunction.class;
}
