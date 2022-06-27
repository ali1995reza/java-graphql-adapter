package grphaqladapter.annotations.impl.directive;

import graphql.introspection.Introspection;
import grphaqladapter.adaptedschemabuilder.assertutil.Assert;
import grphaqladapter.annotations.GraphqlDirectiveAnnotation;
import grphaqladapter.annotations.interfaces.GraphqlDirectiveFunction;

import java.util.ArrayList;
import java.util.List;

public class GraphqlDirectiveAnnotationBuilder {

    public static GraphqlDirectiveAnnotationBuilder newBuilder() {
        return new GraphqlDirectiveAnnotationBuilder();
    }

    private String name;
    private List<Introspection.DirectiveLocation> locations = new ArrayList<>();
    private Class<? extends GraphqlDirectiveFunction> functionality;

    public synchronized GraphqlDirectiveAnnotationBuilder name(String name) {
        this.name = name;
        return this;
    }

    public synchronized GraphqlDirectiveAnnotationBuilder functionality(Class<? extends GraphqlDirectiveFunction> functionality) {
        this.functionality = functionality;
        return this;
    }

    public synchronized GraphqlDirectiveAnnotationBuilder addLocation(Introspection.DirectiveLocation location) {
        Assert.isNotNull(location, new NullPointerException("null location provided"));
        if(!locations.contains(location)) {
            this.locations.add(location);
        }
        return this;
    }

    public synchronized GraphqlDirectiveAnnotationBuilder addLocations(Introspection.DirectiveLocation... locations) {
        Assert.isNotNull(locations, new NullPointerException("null locations provided"));
        Assert.allNotNull(new NullPointerException("null location provided"), locations);
        for (Introspection.DirectiveLocation location : locations) {
            addLocation(location);
        }
        return this;
    }

    public synchronized GraphqlDirectiveAnnotation build() {
        Introspection.DirectiveLocation[] locationsArray = new Introspection.DirectiveLocation[locations.size()];
        locations.toArray(locationsArray);
        return new GraphqlDirectiveAnnotationImpl(name, locationsArray, functionality);
    }

}
