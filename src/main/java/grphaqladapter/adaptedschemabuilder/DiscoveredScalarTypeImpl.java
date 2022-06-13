package grphaqladapter.adaptedschemabuilder;

import graphql.schema.GraphQLScalarType;
import grphaqladapter.adaptedschemabuilder.discovered.DiscoveredScalarType;
import grphaqladapter.adaptedschemabuilder.mapped.MappedClass;

public class DiscoveredScalarTypeImpl extends DiscoveredTypeImpl<GraphQLScalarType> implements DiscoveredScalarType {


    public DiscoveredScalarTypeImpl(MappedClass mappedClass, String name, GraphQLScalarType graphQLType) {
        super(mappedClass, name, graphQLType);
    }

    @Override
    void setUnmodifiable() {

    }
}
