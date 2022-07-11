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

import java.util.concurrent.CompletableFuture;

public class UpperCaseDirectiveFunction implements GraphqlDirectiveFunction<Object> {

    @Override
    public Object handleFieldDirective(GraphqlDirectiveDetails directive, Object value, Object source, MappedFieldMethod field, DataFetchingEnvironment env) {
        return upperCase(value);
    }

    @Override
    public Object handleOperationDirective(GraphqlDirectiveDetails directive, Object value, Object source, OperationDefinition operation, MappedFieldMethod field, DataFetchingEnvironment env) {
        return upperCase(value);
    }

    @Override
    public GraphQLFieldDefinition onField(GraphqlDirectiveDetails directive, GraphQLFieldDefinition fieldDefinition, MappedObjectTypeClass typeClass, MappedFieldMethod field, SchemaDirectiveHandlingContext context) {
        if (field.type().type() != String.class || field.type().dimensions() > 0) {
            throw new IllegalStateException("UpperCase directive can just apply on String type fields");
        }
        context.changeDataFetcherBehavior(typeClass.name(), field.name(), dataFetcher -> DataFetcherAdapter.of(dataFetcher, this::upperCase));
        return GraphqlDirectiveFunction.super.onField(directive, fieldDefinition, typeClass, field, context);
    }

    private CompletableFuture<?> upperAsync(CompletableFuture<?> future) {
        return future.thenApply(this::upperSync);
    }

    private Object upperCase(Object object) {
        if (object == null) {
            return null;
        }
        if (object instanceof CompletableFuture) {
            return upperAsync((CompletableFuture<?>) object);
        } else {
            return upperSync(object);
        }
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
}
