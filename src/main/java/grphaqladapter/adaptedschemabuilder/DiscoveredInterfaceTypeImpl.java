package grphaqladapter.adaptedschemabuilder;

import graphql.schema.GraphQLInterfaceType;
import grphaqladapter.adaptedschemabuilder.discovered.DiscoveredInterfaceType;
import grphaqladapter.adaptedschemabuilder.discovered.DiscoveredObjectType;
import grphaqladapter.adaptedschemabuilder.mapped.MappedInterface;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

final class DiscoveredInterfaceTypeImpl extends DiscoveredTypeImpl<GraphQLInterfaceType, MappedInterface> implements DiscoveredInterfaceType {

    private List<DiscoveredObjectType> implementors;

    public DiscoveredInterfaceTypeImpl(MappedInterface mappedInterface, String name, GraphQLInterfaceType graphQLType) {
        super(mappedInterface, name, graphQLType);
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
