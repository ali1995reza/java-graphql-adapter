package grphaqladapter.adaptedschemabuilder;

import graphql.schema.GraphQLInterfaceType;
import grphaqladapter.adaptedschemabuilder.discovered.DiscoveredInterfaceType;
import grphaqladapter.adaptedschemabuilder.discovered.DiscoveredObjectType;
import grphaqladapter.adaptedschemabuilder.mapped.MappedClass;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

final class DiscoveredInterfaceTypeImpl extends DiscoveredTypeImpl<GraphQLInterfaceType> implements DiscoveredInterfaceType {


    private List<DiscoveredObjectType> implementors;

    public DiscoveredInterfaceTypeImpl(MappedClass mappedClass, String name, GraphQLInterfaceType graphQLType) {
        super(mappedClass, name, graphQLType);

        this.implementors = new ArrayList<>();
    }


    @Override
    public List<DiscoveredObjectType> implementors() {
        return implementors;
    }


    @Override
    void setUnmodifiable() {
        implementors = Collections.unmodifiableList(implementors);
    }
}
