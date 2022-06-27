package grphaqladapter.adaptedschemabuilder;

import graphql.schema.GraphQLEnumType;
import grphaqladapter.adaptedschemabuilder.discovered.DiscoveredEnumType;
import grphaqladapter.adaptedschemabuilder.mapped.MappedEnum;

final class DiscoveredEnumTypeImpl extends DiscoveredTypeImpl<GraphQLEnumType, MappedEnum> implements DiscoveredEnumType {

    public DiscoveredEnumTypeImpl(MappedEnum mappedClass, String name, GraphQLEnumType graphQLType) {
        super(mappedClass, name, graphQLType);
    }

    @Override
    void setUnmodifiable() {

    }
}
