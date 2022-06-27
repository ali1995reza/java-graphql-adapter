package grphaqladapter.adaptedschemabuilder.mapped;

import graphql.introspection.Introspection;
import grphaqladapter.annotations.interfaces.GraphqlDirectiveFunction;

import java.util.Map;
import java.util.Set;

public interface MappedAnnotation extends MappedClass {

    Map<String, MappedAnnotationMethod> mappedMethods();

    Set<Introspection.DirectiveLocation> locations();

    Class<? extends GraphqlDirectiveFunction> functionality();
}
