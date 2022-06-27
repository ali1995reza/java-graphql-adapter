package tests.T1.schema.directives;

import graphql.schema.GraphQLArgument;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLTypeReference;
import grphaqladapter.adaptedschemabuilder.mapped.MappedFieldMethod;
import grphaqladapter.adaptedschemabuilder.mapped.MappedTypeClass;
import grphaqladapter.annotations.interfaces.GraphqlDirectiveFunction;
import grphaqladapter.annotations.interfaces.SchemaDirectiveHandlingContext;
import tests.T1.schema.PageParameters;

public class AddPageParametersDirectiveFunction extends GraphqlDirectiveFunction {

    @Override
    public GraphQLFieldDefinition onField(GraphQLFieldDefinition fieldDefinition, MappedTypeClass typeClass, MappedFieldMethod field, SchemaDirectiveHandlingContext context) {
        return GraphQLFieldDefinition.newFieldDefinition(fieldDefinition)
                .argument(GraphQLArgument
                        .newArgument()
                        .name("pageParameters")
                        .description("page parameters")
                        .type(GraphQLTypeReference.typeRef(PageParameters.class.getSimpleName()))
                        .build())
                .build();
    }
}
