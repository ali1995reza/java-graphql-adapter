package grphaqladapter.adaptedschemabuilder.discovered;

import graphql.schema.GraphQLType;
import grphaqladapter.adaptedschemabuilder.mapped.MappedClass;
import grphaqladapter.annotations.GraphqlType;

public interface DiscoveredType<T extends GraphQLType> {

    String typeName();
    T asGraphQLType();
    MappedClass asMappedClass();
}
