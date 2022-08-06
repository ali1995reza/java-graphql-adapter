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
package test_graphql_adapter.schema.validators;

import graphql_adapter.adaptedschema.exceptions.GraphqlValidationException;
import graphql_adapter.adaptedschema.functions.impl.GraphqlNonNullValidationFunction;
import graphql_adapter.adaptedschema.mapping.mapped_elements.ValidatableMappedElement;
import test_graphql_adapter.schema.validators.exceptions.NotMatchException;

import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

public class MatchValidationFunction extends GraphqlNonNullValidationFunction<String, Match> {

    private final static ConcurrentHashMap<String, Pattern> compiledPatterns = new ConcurrentHashMap<>();

    @Override
    protected void validateNonNull(String data, Match arguments, ValidatableMappedElement element) throws GraphqlValidationException {
        if (!compiledPatterns.computeIfAbsent(arguments.value(), Pattern::compile).matcher(data).matches()) {
            throw new NotMatchException(element.name() + " must be match pattern \"" + arguments.value() + "\" but was \"" + data + "\"", arguments.value(), data);
        }
    }
}
