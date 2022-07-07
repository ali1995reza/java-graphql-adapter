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

package tests.T1.schema.directives;

import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLFieldDefinition;
import grphaqladapter.adaptedschema.functions.GraphqlDirectiveFunction;
import grphaqladapter.adaptedschema.functions.SchemaDirectiveHandlingContext;
import grphaqladapter.adaptedschema.mapping.mapped_elements.classes.MappedObjectTypeClass;
import grphaqladapter.adaptedschema.mapping.mapped_elements.method.MappedFieldMethod;
import grphaqladapter.adaptedschema.system_objects.directive.GraphqlDirectiveDetails;
import grphaqladapter.adaptedschema.utils.DataFetcherAdapter;

import java.lang.reflect.Array;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ReverseDirectiveFunction implements GraphqlDirectiveFunction {


    @Override
    public GraphQLFieldDefinition onField(GraphqlDirectiveDetails directive, GraphQLFieldDefinition fieldDefinition, MappedObjectTypeClass typeClass, MappedFieldMethod field, SchemaDirectiveHandlingContext context) {
        context.changeDataFetcherBehavior(typeClass.name(), field.name(), dataFetcher -> DataFetcherAdapter.of(dataFetcher, value -> reverse(field, value)));
        return GraphqlDirectiveFunction.super.onField(directive, fieldDefinition, typeClass, field, context);
    }

    @Override
    public Object handleFieldDirective(GraphqlDirectiveDetails directive, Object value, Object source, MappedFieldMethod field, DataFetchingEnvironment env) {
        return reverse(field, value);
    }

    private static Object reverse(MappedFieldMethod field, Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof CompletableFuture) {
            return ((CompletableFuture) value).thenApply(v -> reverse(field, v));
        } else if (!field.type().hasDimensions() && field.type().type() == String.class) {
            return new StringBuffer().append((String) value).reverse().toString();
        } else if (field.type().hasDimensions() && field.type().dimensionModel().isList()) {
            List list = (List) value;
            Collections.reverse(list);
            return list;
        } else if (field.type().hasDimensions() && field.type().dimensionModel().isArray()) {
            reverseArray(value);
            return value;
        }
        return value;
    }

    private static void reverseArray(Object array) {
        int size = Array.getLength(array);
        int i, k;
        Object temp;
        for (i = 0; i < size / 2; i++) {
            temp = Array.get(array, i);
            Array.set(array, i, Array.get(array, size - i - 1));
            Array.set(array, size - i - 1, temp);
        }
    }
}
