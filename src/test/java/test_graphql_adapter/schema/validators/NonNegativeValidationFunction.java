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
import test_graphql_adapter.schema.validators.exceptions.NegativeException;

import java.math.BigDecimal;
import java.math.BigInteger;

public class NonNegativeValidationFunction extends GraphqlNonNullValidationFunction<Number, NonNegative> {
    @Override
    protected void validateNonNull(Number number, NonNegative arguments, ValidatableMappedElement element) throws GraphqlValidationException {
        if (isNegative(number)) {
            throw new NegativeException(element.name() + " shouldn't be negative but was " + number, number);
        }
    }

    private boolean isNegative(Number number) {
        if (number instanceof Double) {
            return ((Double) number) < 0;
        }
        if (number instanceof Float) {
            return ((Float) number) < 0;
        }
        if (number instanceof Integer) {
            return ((Integer) number) < 0;
        }
        if (number instanceof Long) {
            return ((Long) number) < 0;
        }
        if (number instanceof Short) {
            return ((Short) number) < 0;
        }
        if (number instanceof Byte) {
            return ((Byte) number) < 0;
        }
        if (number instanceof BigInteger) {
            return ((BigInteger) number).compareTo(BigInteger.ZERO) < 0;
        }
        if (number instanceof BigDecimal) {
            return ((BigDecimal) number).compareTo(BigDecimal.ZERO) < 0;
        }

        throw new GraphqlValidationException("unknown number type [" + number.getClass() + "]");
    }
}
