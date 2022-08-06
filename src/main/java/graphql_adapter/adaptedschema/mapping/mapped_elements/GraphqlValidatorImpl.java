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

import static graphql_adapter.adaptedschema.utils.ClassUtils.cast;

public class GraphqlValidatorImpl implements GraphqlValidator {

    private final Class<? extends GraphqlValidatorFunction<?, ?>> validationFunction;
    private final Object argument;

    public GraphqlValidatorImpl(Class<? extends GraphqlValidatorFunction<?, ?>> validationFunction, Object argument) {
        this.validationFunction = validationFunction;
        this.argument = argument;
    }

    @Override
    public <T> T validationArgument() {
        return cast(this.argument);
    }

    @Override
    public Class<? extends GraphqlValidatorFunction<?, ?>> validationFunction() {
        return this.validationFunction;
    }
}
