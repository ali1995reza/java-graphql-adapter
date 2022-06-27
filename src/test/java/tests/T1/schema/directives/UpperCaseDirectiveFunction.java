package tests.T1.schema.directives;

import graphql.language.OperationDefinition;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLFieldDefinition;
import grphaqladapter.adaptedschemabuilder.mapped.MappedFieldMethod;
import grphaqladapter.adaptedschemabuilder.mapped.MappedTypeClass;
import grphaqladapter.adaptedschemabuilder.utils.DataFetcherAdapter;
import grphaqladapter.annotations.interfaces.GraphqlDirectiveFunction;
import grphaqladapter.annotations.interfaces.SchemaDirectiveHandlingContext;

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
    public Object handleFragmentDirective(Object value, Object source, MappedTypeClass type, MappedFieldMethod field, DataFetchingEnvironment env) {
        return super.handleFragmentDirective(value, source, type, field, env);
    }

    @Override
    public GraphQLFieldDefinition onField(GraphQLFieldDefinition fieldDefinition, MappedTypeClass typeClass, MappedFieldMethod field, SchemaDirectiveHandlingContext context) {
        context.changeDataFetcherBehavior(typeClass.name(), field.name(),dataFetcher-> DataFetcherAdapter.of(dataFetcher, this::upperCase));
        return fieldDefinition;
    }

    private Object upperCase(Object object) {
        if(object == null || !(object instanceof String)) {
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
}
