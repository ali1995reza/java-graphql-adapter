package tests.T1.schema.directives;

import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLTypeReference;
import grphaqladapter.adaptedschemabuilder.mapped.MappedFieldMethod;
import grphaqladapter.adaptedschemabuilder.mapped.MappedTypeClass;
import grphaqladapter.adaptedschemabuilder.utils.DataFetcherAdapter;
import grphaqladapter.annotations.interfaces.GraphqlDirectiveFunction;
import grphaqladapter.annotations.interfaces.SchemaDirectiveHandlingContext;

public class ToStringDirectiveFunction extends GraphqlDirectiveFunction {

    @Override
    public GraphQLFieldDefinition onField(GraphQLFieldDefinition fieldDefinition, MappedTypeClass typeClass, MappedFieldMethod field, SchemaDirectiveHandlingContext context) {
        context.changeDataFetcherBehavior(typeClass.name(), field.name(), dataFetcher-> DataFetcherAdapter.of(dataFetcher, String::valueOf));
        return GraphQLFieldDefinition.newFieldDefinition(fieldDefinition)
                .type(GraphQLTypeReference.typeRef("String"))
                .build();
    }
}
