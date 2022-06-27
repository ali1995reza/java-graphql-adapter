package grphaqladapter.annotations.impl.directive;

import graphql.introspection.Introspection;
import grphaqladapter.adaptedschemabuilder.assertutil.Assert;
import grphaqladapter.annotations.GraphqlDirectiveAnnotation;
import grphaqladapter.annotations.impl.GraphqlElementAnnotationBuilder;
import grphaqladapter.annotations.interfaces.GraphqlDirectiveFunction;

import java.util.ArrayList;
import java.util.List;

public class GraphqlDirectiveAnnotationBuilder extends GraphqlElementAnnotationBuilder<GraphqlDirectiveAnnotationBuilder, GraphqlDirectiveAnnotation> {

    public static GraphqlDirectiveAnnotationBuilder newBuilder() {
        return new GraphqlDirectiveAnnotationBuilder();
    }

    private List<Introspection.DirectiveLocation> locations = new ArrayList<>();
    private Class<? extends GraphqlDirectiveFunction> functionality;

    public GraphqlDirectiveAnnotationBuilder functionality(Class<? extends GraphqlDirectiveFunction> functionality) {
        this.functionality = functionality;
        return this;
    }

    public Class<? extends GraphqlDirectiveFunction> functionality() {
        return functionality;
    }

    public GraphqlDirectiveAnnotationBuilder addLocation(Introspection.DirectiveLocation location) {
        Assert.isNotNull(location, new NullPointerException("null location provided"));
        if (!locations.contains(location)) {
            this.locations.add(location);
        }
        return this;
    }

    public GraphqlDirectiveAnnotationBuilder addLocations(Introspection.DirectiveLocation... locations) {
        Assert.isNotNull(locations, new NullPointerException("null locations provided"));
        Assert.allNotNull(new NullPointerException("null location provided"), locations);
        for (Introspection.DirectiveLocation location : locations) {
            addLocation(location);
        }
        return this;
    }

    public List<Introspection.DirectiveLocation> locations() {
        return new ArrayList<>(locations);
    }

    public GraphqlDirectiveAnnotation build() {
        Introspection.DirectiveLocation[] locationsArray = new Introspection.DirectiveLocation[locations.size()];
        locations.toArray(locationsArray);
        return new GraphqlDirectiveAnnotationImpl(name(), locationsArray, functionality());
    }

}
