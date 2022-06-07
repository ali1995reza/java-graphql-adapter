package grphaqladapter.adaptedschemabuilder;

import graphql.schema.GraphQLObjectType;
import grphaqladapter.adaptedschemabuilder.discovered.DiscoveredInterfaceType;
import grphaqladapter.adaptedschemabuilder.discovered.DiscoveredObjectType;
import grphaqladapter.adaptedschemabuilder.mapped.MappedClass;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

final class DiscoveredObjectTypeImpl extends DiscoveredTypeImpl<GraphQLObjectType> implements DiscoveredObjectType {


    private List<DiscoveredInterfaceType> interfaces;


    public DiscoveredObjectTypeImpl(MappedClass mappedClass, String name, GraphQLObjectType graphQLType) {
        super(mappedClass, name, graphQLType);
        this.interfaces = new ArrayList<>();
    }

    @Override
    public List<DiscoveredInterfaceType> implementedInterfaces() {
        return interfaces;
    }

    @Override
    void setUnmodifiable() {
        interfaces = Collections.unmodifiableList(interfaces);
    }
}
