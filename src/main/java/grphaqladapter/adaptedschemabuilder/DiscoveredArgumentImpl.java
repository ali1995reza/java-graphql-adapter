package grphaqladapter.adaptedschemabuilder;

import graphql.schema.GraphQLArgument;
import grphaqladapter.adaptedschemabuilder.discovered.DiscoveredArgument;
import grphaqladapter.adaptedschemabuilder.mapped.MappedParameter;

final class DiscoveredArgumentImpl implements DiscoveredArgument {

    private final MappedParameter parameter;
    private final String name;
    private final GraphQLArgument argument;

    public DiscoveredArgumentImpl(MappedParameter parameter, String name, GraphQLArgument argument) {
        this.parameter = parameter;
        this.name = name;
        this.argument = argument;
    }


    @Override
    public String argumentName() {
        return name;
    }

    public MappedParameter asMappedParameter() {
        return parameter;
    }

    @Override
    public GraphQLArgument asGraphQLArgument() {
        return argument;
    }
}
