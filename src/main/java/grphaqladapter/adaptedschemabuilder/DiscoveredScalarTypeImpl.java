package grphaqladapter.adaptedschemabuilder;

import graphql.schema.GraphQLScalarType;
import grphaqladapter.adaptedschemabuilder.discovered.DiscoveredScalarType;
import grphaqladapter.adaptedschemabuilder.mapped.MappedClass;
import grphaqladapter.adaptedschemabuilder.mapped.impl.MappedClassBuilder;

public class DiscoveredScalarTypeImpl extends DiscoveredTypeImpl<GraphQLScalarType> implements DiscoveredScalarType {


    public DiscoveredScalarTypeImpl(Class cls, String name, GraphQLScalarType graphQLType) {
        super(MappedClassBuilder
                        .newBuilder()
                        .setTypeName(name)
                        .setBaseClass(cls)
                        .setDescription(graphQLType.getDescription())
                        .setMappedType(MappedClass.MappedType.SCALAR)
                        .build()
                , name, graphQLType);
    }

    @Override
    void setUnmodifiable() {

    }
}
