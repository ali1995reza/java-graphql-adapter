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

package grphaqladapter.adaptedschema.utils;

import graphql.schema.DataFetchingEnvironment;
import grphaqladapter.adaptedschema.AdaptedGraphQLSchema;
import grphaqladapter.adaptedschema.assertion.Assert;
import grphaqladapter.adaptedschema.system_objects.directive.GraphqlDirectivesHolder;
import grphaqladapter.codegenerator.AdaptedGraphQLDataFetcher;

import java.util.function.Function;

public class DataFetcherAdapter<IN, OUT> implements AdaptedGraphQLDataFetcher<OUT> {

    public static <IN, OUT> AdaptedGraphQLDataFetcher<OUT> of(AdaptedGraphQLDataFetcher<IN> wrapped, Function<IN, OUT> valueAdapter) {
        return new DataFetcherAdapter<>(wrapped, valueAdapter);
    }

    private final AdaptedGraphQLDataFetcher<IN> wrapped;
    private final Function<IN, OUT> valueAdapter;

    public DataFetcherAdapter(AdaptedGraphQLDataFetcher<IN> wrapped, Function<IN, OUT> valueAdapter) {
        Assert.isNotNull(wrapped, new NullPointerException("wrapped data fetcher is null"));
        Assert.isNotNull(valueAdapter, new NullPointerException("value handler is null"));
        this.wrapped = wrapped;
        this.valueAdapter = valueAdapter;
    }

    @Override
    public OUT get(AdaptedGraphQLSchema schema, GraphqlDirectivesHolder directivesHolder, Object source, DataFetchingEnvironment environment) throws Exception {
        return valueAdapter.apply(wrapped.get(schema, directivesHolder, source, environment));
    }
}
