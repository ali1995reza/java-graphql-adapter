package grphaqladapter.adaptedschemabuilder;

import graphql.schema.GraphQLInputObjectType;
import grphaqladapter.adaptedschemabuilder.discovered.DiscoveredInputType;
import grphaqladapter.adaptedschemabuilder.mapped.MappedClass;

final class DiscoveredInputTypeImpl extends DiscoveredTypeImpl<GraphQLInputObjectType> implements DiscoveredInputType {


    public DiscoveredInputTypeImpl(MappedClass mappedClass, String name, GraphQLInputObjectType graphQLType) {
        super(mappedClass, name, graphQLType);
    }


    @Override
    void setUnmodifiable() {

    }

}
