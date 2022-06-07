package grphaqladapter.adaptedschemabuilder;

import graphql.schema.GraphQLEnumType;
import grphaqladapter.adaptedschemabuilder.discovered.DiscoveredEnumType;
import grphaqladapter.adaptedschemabuilder.mapped.MappedClass;

final class DiscoveredEnumTypeImpl extends DiscoveredTypeImpl<GraphQLEnumType> implements DiscoveredEnumType {

    public DiscoveredEnumTypeImpl(MappedClass mappedClass, String name, GraphQLEnumType graphQLType) {
        super(mappedClass, name, graphQLType);
    }

    @Override
    void setUnmodifiable() {

    }
}
