/*
 * Copyright 2022 Alireza Akhoundi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package test_graphql_adapter.schema.directives;

import graphql.language.OperationDefinition;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLFieldDefinition;
import graphql_adapter.adaptedschema.functions.GraphqlDirectiveFunction;
import graphql_adapter.adaptedschema.functions.SchemaDirectiveHandlingContext;
import graphql_adapter.adaptedschema.mapping.mapped_elements.classes.MappedObjectTypeClass;
import graphql_adapter.adaptedschema.mapping.mapped_elements.method.MappedFieldMethod;
import graphql_adapter.adaptedschema.system_objects.directive.GraphqlDirectiveDetails;
import graphql_adapter.adaptedschema.utils.DataFetcherAdapter;

import java.util.concurrent.*;

public class DelayDirectiveFunction implements GraphqlDirectiveFunction<Object> {

    private final static ScheduledExecutorService SERVICE = Executors.newScheduledThreadPool(1);

    @Override
    public Object handleFieldDirective(GraphqlDirectiveDetails directive, Object value, Object source, MappedFieldMethod field, DataFetchingEnvironment env) {
        return execute(value, directive.getArgument("seconds"));
    }

    @Override
    public Object handleOperationDirective(GraphqlDirectiveDetails directive, Object value, Object source, OperationDefinition operation, MappedFieldMethod field, DataFetchingEnvironment env) {
        return execute(value, directive.getArgument("seconds"));
    }

    @Override
    public GraphQLFieldDefinition onField(GraphqlDirectiveDetails directive, GraphQLFieldDefinition fieldDefinition, MappedObjectTypeClass typeClass, MappedFieldMethod field, SchemaDirectiveHandlingContext context) {
        context.changeDataFetcherBehavior(typeClass.name(), field.name(), dataFetcher -> DataFetcherAdapter.of(dataFetcher, value -> execute(value, directive.getArgument("seconds"))));
        return GraphqlDirectiveFunction.super.onField(directive, fieldDefinition, typeClass, field, context);
    }

    private static Executor execute(long seconds) {
        return r -> SERVICE.schedule(r, seconds, TimeUnit.SECONDS);
    }

    private CompletableFuture<?> execute(Object o, int seconds) {
        return CompletableFuture.supplyAsync(() -> o, execute(seconds));
    }
}
