package grphaqladapter.adaptedschemabuilder;


import graphql.schema.GraphQLFieldDefinition;
import grphaqladapter.adaptedschemabuilder.discovered.DiscoveredField;
import grphaqladapter.adaptedschemabuilder.mapped.MappedMethod;

final class DiscoveredFieldImpl implements DiscoveredField {


    private final MappedMethod mappedMethod;
    private final String name;
    private final GraphQLFieldDefinition field;

    public DiscoveredFieldImpl(MappedMethod mappedMethod, String name, GraphQLFieldDefinition field) {
        this.mappedMethod = mappedMethod;
        this.name = name;
        this.field = field;
    }


    @Override
    public String fieldName() {
        return name;
    }

    public MappedMethod asMappedMethod() {
        return mappedMethod;
    }

    @Override
    public GraphQLFieldDefinition asGraphQLFieldDefinition() {
        return field;
    }
}
