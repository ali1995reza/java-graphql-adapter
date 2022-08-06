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
package graphql_adapter.adaptedschema.mapping.mapped_elements;

import graphql_adapter.adaptedschema.functions.GraphqlValidatorFunction;
import graphql_adapter.adaptedschema.utils.builder.IBuilder;

import static graphql_adapter.adaptedschema.utils.ClassUtils.cast;

public class GraphqlValidatorBuilder implements IBuilder<GraphqlValidatorBuilder, GraphqlValidator> {

    public static GraphqlValidatorBuilder newBuilder() {
        return new GraphqlValidatorBuilder();
    }

    private Class<? extends GraphqlValidatorFunction<?, ?>> validationFunction;
    private Object validationArgument;

    @Override
    public GraphqlValidator build() {
        return new GraphqlValidatorImpl(this.validationFunction, this.validationArgument);
    }

    @Override
    public GraphqlValidatorBuilder copy(GraphqlValidator graphqlValidator) {
        return refresh()
                .validationArgument(graphqlValidator.validationArgument())
                .validationFunction(graphqlValidator.validationFunction());
    }

    @Override
    public GraphqlValidatorBuilder refresh() {
        this.validationFunction = null;
        this.validationArgument = null;
        return this;
    }

    public <T> T validationArgument() {
        return cast(validationArgument);
    }

    public GraphqlValidatorBuilder validationArgument(Object validationArgument) {
        this.validationArgument = validationArgument;
        return this;
    }

    public Class<? extends GraphqlValidatorFunction<?, ?>> validationFunction() {
        return validationFunction;
    }

    public GraphqlValidatorBuilder validationFunction(Class<? extends GraphqlValidatorFunction<?, ?>> validationFunction) {
        this.validationFunction = validationFunction;
        return this;
    }
}
