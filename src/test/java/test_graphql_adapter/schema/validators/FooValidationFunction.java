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
import test_graphql_adapter.schema.types.Foo;
import test_graphql_adapter.schema.validators.exceptions.FooValidationException;

import java.util.Arrays;
import java.util.List;

public class FooValidationFunction extends GraphqlNonNullValidationFunction<Object, FooValidation> {
    @Override
    protected void validateNonNull(Object data, FooValidation arguments, ValidatableMappedElement element) throws GraphqlValidationException {
        if (data instanceof Foo) {
            validateFoo((Foo) data);
        } else if (data instanceof List) {
            for (Foo foo : ((List<Foo>) data)) {
                validateFoo(foo);
            }
        } else if (data instanceof Foo[]) {
            for (Foo foo : ((Foo[]) data)) {
                validateFoo(foo);
            }
        }
    }

    private void validateFoo(Foo foo) {
        if(foo == null) {
            return;
        }
        if (Arrays.equals(foo.getIntArray(), new int[]{-100, -1000, -10000, -100000})) {
            throw new FooValidationException();
        }
    }
}
