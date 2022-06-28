package tests.T1.schema.directives;

import graphql.language.OperationDefinition;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLFieldDefinition;
import grphaqladapter.adaptedschemabuilder.mapped.MappedFieldMethod;
import grphaqladapter.adaptedschemabuilder.mapped.MappedTypeClass;
import grphaqladapter.adaptedschemabuilder.utils.DataFetcherAdapter;
import grphaqladapter.annotations.interfaces.GraphqlDirectiveFunction;
import grphaqladapter.annotations.interfaces.SchemaDirectiveHandlingContext;

import java.util.concurrent.*;

public class DelayDirectiveFunction extends GraphqlDirectiveFunction<Object> {

    private final static ScheduledExecutorService SERVICE = Executors.newScheduledThreadPool(1);

    private static Executor execute(long s, TimeUnit unit) {
        return r -> SERVICE.schedule(r, s, unit);
    }

    private Executor execute() {
        final int seconds = directive().getArgument("seconds");
        return execute(seconds, TimeUnit.SECONDS);
    }

    private CompletableFuture execute(Object o) {
        return CompletableFuture.supplyAsync(() -> o, execute());
    }

    @Override
    public Object handleFieldDirective(Object value, Object source, MappedFieldMethod field, DataFetchingEnvironment env) {
        return execute(value);
    }

    @Override
    public Object handleOperationDirective(Object value, Object source, OperationDefinition operation, MappedFieldMethod field, DataFetchingEnvironment env) {
        return execute(value);
    }

    @Override
    public GraphQLFieldDefinition onField(GraphQLFieldDefinition fieldDefinition, MappedTypeClass typeClass, MappedFieldMethod field, SchemaDirectiveHandlingContext context) {
        context.changeDataFetcherBehavior(typeClass.name(), field.name(), dataFetcher -> DataFetcherAdapter.of(dataFetcher, this::execute));
        return super.onField(fieldDefinition, typeClass, field, context);
    }
}
