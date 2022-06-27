package tests.T1.schema.directives;

import graphql.language.OperationDefinition;
import graphql.schema.DataFetchingEnvironment;
import grphaqladapter.adaptedschemabuilder.mapped.MappedFieldMethod;
import grphaqladapter.annotations.interfaces.GraphqlDirectiveFunction;

public class AuthenticationDirectiveFunction extends GraphqlDirectiveFunction {

    @Override
    public void preHandleOperationDirective(Object source, OperationDefinition operation, MappedFieldMethod field, DataFetchingEnvironment env) {
        env.getGraphQlContext().put("auth", directive().getArgument("token"));
        super.preHandleOperationDirective(source, operation, field, env);
    }
}
