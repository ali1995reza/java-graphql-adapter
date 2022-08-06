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
import test_graphql_adapter.schema.validators.exceptions.NotOneOfException;

import java.util.Arrays;
import java.util.Objects;

public class NotOneOfValidationFunction extends GraphqlNonNullValidationFunction<Object, NotOneOf> {

    public static boolean equalsToOneOf(String str, String... values) {
        for (String value : values) {
            if (Objects.equals(str, value)) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void validateNonNull(Object data, NotOneOf arguments, ValidatableMappedElement element) throws GraphqlValidationException {
        String value = String.valueOf(data);
        if (equalsToOneOf(value, arguments.value())) {
            throw new NotOneOfException(element.name() + " shouldn't be one of " + Arrays.toString(arguments.value()) + ", but was " + value, value, arguments.value());
        }
    }
}
