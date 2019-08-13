package grphaqladapter.adaptedschemabuilder.discovered;

import graphql.schema.GraphQLArgument;
import grphaqladapter.adaptedschemabuilder.mapped.MappedParameter;

public interface DiscoveredArgument {

    String argumentName();
    MappedParameter asMappedParameter();
    GraphQLArgument asGraphQLArgument();
}
