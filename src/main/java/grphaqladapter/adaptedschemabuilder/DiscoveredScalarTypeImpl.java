package grphaqladapter.adaptedschemabuilder;

import graphql.schema.GraphQLScalarType;
import grphaqladapter.adaptedschemabuilder.discovered.DiscoveredScalarType;
import grphaqladapter.adaptedschemabuilder.mapped.MappedScalarClass;

public class DiscoveredScalarTypeImpl extends DiscoveredTypeImpl<GraphQLScalarType, MappedScalarClass> implements DiscoveredScalarType {


    public DiscoveredScalarTypeImpl(MappedScalarClass mappedScalarClass, String name, GraphQLScalarType graphQLType) {
        super(mappedScalarClass, name, graphQLType);
    }

    @Override
    void setUnmodifiable() {

    }
}
