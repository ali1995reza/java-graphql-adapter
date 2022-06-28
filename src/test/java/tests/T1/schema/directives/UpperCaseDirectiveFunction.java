package tests.T1.schema.directives;

import graphql.language.OperationDefinition;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLFieldDefinition;
import grphaqladapter.adaptedschemabuilder.mapped.MappedFieldMethod;
import grphaqladapter.adaptedschemabuilder.mapped.MappedTypeClass;
import grphaqladapter.adaptedschemabuilder.utils.DataFetcherAdapter;
import grphaqladapter.annotations.interfaces.GraphqlDirectiveFunction;
import grphaqladapter.annotations.interfaces.SchemaDirectiveHandlingContext;

import java.util.concurrent.CompletableFuture;

public class UpperCaseDirectiveFunction extends GraphqlDirectiveFunction<Object> {

    @Override
    public Object handleFieldDirective(Object value, Object source, MappedFieldMethod field, DataFetchingEnvironment env) {
        return upperCase(value);
    }

    @Override
    public Object handleOperationDirective(Object value, Object source, OperationDefinition operation, MappedFieldMethod field, DataFetchingEnvironment env) {
        return upperCase(value);
    }

    @Override
    public GraphQLFieldDefinition onField(GraphQLFieldDefinition fieldDefinition, MappedTypeClass typeClass, MappedFieldMethod field, SchemaDirectiveHandlingContext context) {
        if (field.type().type() != String.class || field.type().dimensions() > 0) {
            throw new IllegalStateException("UpperCase directive can just apply on String type fields");
        }
        context.changeDataFetcherBehavior(typeClass.name(), field.name(), dataFetcher -> DataFetcherAdapter.of(dataFetcher, this::upperCase));
        return fieldDefinition;
    }

    private CompletableFuture upperAsync(CompletableFuture future) {
        return future.thenApply(this::upperSync);
    }

    private Object upperSync(Object object) {
        if (!(object instanceof String)) {
            return object;
        }
        String str = (String) object;
        char[] data = str.toCharArray();
        for (int i = 0; i < data.length; i++) {
            if (Character.isUpperCase(data[i])) {
                continue;
            }
            data[i] = Character.toUpperCase(data[i]);
        }
        return new String(data);
    }

    private Object upperCase(Object object) {
        if (object == null) {
            return null;
        }
        if (object instanceof CompletableFuture) {
            return upperAsync((CompletableFuture) object);
        } else {
            return upperSync(object);
        }

    }
}
