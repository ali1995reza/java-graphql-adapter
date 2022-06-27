package grphaqladapter.adaptedschemabuilder;

import graphql.schema.GraphQLInputObjectType;
import grphaqladapter.adaptedschemabuilder.discovered.DiscoveredInputType;
import grphaqladapter.adaptedschemabuilder.mapped.MappedInputTypeClass;

final class DiscoveredInputTypeImpl extends DiscoveredTypeImpl<GraphQLInputObjectType, MappedInputTypeClass> implements DiscoveredInputType {


    public DiscoveredInputTypeImpl(MappedInputTypeClass mappedInputTypeClass, String name, GraphQLInputObjectType graphQLType) {
        super(mappedInputTypeClass, name, graphQLType);
    }


    @Override
    void setUnmodifiable() {

    }

}
