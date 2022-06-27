package grphaqladapter.adaptedschemabuilder.mapped.impl.annotation;

import graphql.introspection.Introspection;
import grphaqladapter.adaptedschemabuilder.assertutil.Assert;
import grphaqladapter.adaptedschemabuilder.mapped.MappedAnnotation;
import grphaqladapter.adaptedschemabuilder.mapped.MappedAnnotationMethod;
import grphaqladapter.adaptedschemabuilder.mapped.MappedElementType;
import grphaqladapter.adaptedschemabuilder.mapped.impl.MappedClassBuilder;
import grphaqladapter.annotations.interfaces.GraphqlDirectiveFunction;

import java.util.*;

public class MappedAnnotationBuilder extends MappedClassBuilder<MappedAnnotationBuilder, MappedAnnotation> {

    public static MappedAnnotationBuilder newBuilder() {
        return new MappedAnnotationBuilder();
    }

    private final Map<String, MappedAnnotationMethod> mappedMethods = new HashMap<>();
    private final Set<Introspection.DirectiveLocation> locations = new HashSet<>();
    private Class<? extends GraphqlDirectiveFunction> functionality;

    MappedAnnotationBuilder() {
        super(MappedElementType.DIRECTIVE);
    }

    public MappedAnnotationBuilder addMethod(MappedAnnotationMethod method) {
        Assert.isOneOrMoreFalse(new IllegalStateException("already a method with field typeName [" + method.name() +
                        "] exist - [exist:" + mappedMethods.get(method.name()) + "]"),
                mappedMethods.containsKey(method.name()));

        mappedMethods.put(method.name(), method);
        return this;
    }

    public MappedAnnotationBuilder addLocation(Introspection.DirectiveLocation location) {
        Assert.isFalse(locations.contains(location), new IllegalStateException("location [" + location + "] already exists"));
        locations.add(location);
        return this;
    }

    public MappedAnnotationBuilder addLocations(Introspection.DirectiveLocation... locations) {
        for (Introspection.DirectiveLocation location : locations) {
            addLocation(location);
        }
        return this;
    }

    public MappedAnnotationBuilder deleteLocation(Introspection.DirectiveLocation location) {
        Assert.isTrue(locations.contains(location), new IllegalStateException("location [" + location + "] not present"));
        this.locations.remove(location);
        return this;
    }

    public MappedAnnotationBuilder clearLocations() {
        this.locations.clear();
        return this;
    }

    public MappedAnnotationBuilder functionality(Class<? extends GraphqlDirectiveFunction> functionality) {
        this.functionality = functionality;
        return this;
    }

    public Class<? extends GraphqlDirectiveFunction> functionality() {
        return functionality;
    }

    public Map<String, MappedAnnotationMethod> mappedMethods() {
        return Collections.unmodifiableMap(new HashMap<>(mappedMethods));
    }

    public Set<Introspection.DirectiveLocation> locations() {
        return Collections.unmodifiableSet(new HashSet<>(locations));
    }

    @Override
    public MappedAnnotationBuilder refresh() {
        this.clearMethods();
        this.clearLocations();
        this.functionality = null;
        return super.refresh();
    }

    public MappedAnnotationBuilder clearMethods() {
        this.mappedMethods.clear();
        return this;
    }

    public MappedAnnotation build() {
        return new MappedAnnotationImpl(
                name(),
                description(),
                baseClass(),
                mappedMethods(),
                locations(),
                functionality());
    }
}
