package grphaqladapter.adaptedschemabuilder.discovered;

import graphql.schema.GraphQLFieldDefinition;
import grphaqladapter.adaptedschemabuilder.mapped.MappedMethod;

public interface DiscoveredField {


    String fieldName();
    MappedMethod asMappedMethod();
    GraphQLFieldDefinition asGraphQLFieldDefinition();
}
