package grphaqladapter.adaptedschemabuilder.mapped.impl.annotation;

import graphql.introspection.Introspection;
import grphaqladapter.adaptedschemabuilder.mapped.MappedAnnotation;
import grphaqladapter.adaptedschemabuilder.mapped.MappedAnnotationMethod;
import grphaqladapter.adaptedschemabuilder.mapped.MappedElementType;
import grphaqladapter.adaptedschemabuilder.mapped.impl.MappedClassImpl;
import grphaqladapter.annotations.interfaces.GraphqlDirectiveFunction;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Set;

final class MappedAnnotationImpl extends MappedClassImpl implements MappedAnnotation {

    private final Map<String, MappedAnnotationMethod> mappedMethods;
    private final Set<Introspection.DirectiveLocation> locations;
    private final Class<? extends GraphqlDirectiveFunction> functionality;

    MappedAnnotationImpl(String name, String description, Class<? extends Annotation> baseClass, Map<String, MappedAnnotationMethod> mappedMethods, Set<Introspection.DirectiveLocation> locations, Class<? extends GraphqlDirectiveFunction> functionality) {
        super(name, MappedElementType.DIRECTIVE, description, baseClass);
        this.mappedMethods = mappedMethods;
        this.locations = locations;
        this.functionality = functionality;
    }

    @Override
    public Map<String, MappedAnnotationMethod> mappedMethods() {
        return mappedMethods;
    }

    @Override
    public Set<Introspection.DirectiveLocation> locations() {
        return locations;
    }

    @Override
    public Class<? extends GraphqlDirectiveFunction> functionality() {
        return functionality;
    }
}
