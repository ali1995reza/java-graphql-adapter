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
package test_graphql_adapter.schema.validators.exceptions;

import graphql_adapter.adaptedschema.exceptions.GraphqlValidationException;

import java.util.Arrays;
import java.util.Objects;

public class OneOfException extends GraphqlValidationException {

    private final String value;
    private final String[] validValues;

    public OneOfException(String e, String value, String... validValues) {
        super(e);
        this.value = value;
        this.validValues = validValues;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OneOfException that = (OneOfException) o;
        return Objects.equals(value, that.value) && Arrays.equals(validValues, that.validValues);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(value);
        result = 31 * result + Arrays.hashCode(validValues);
        return result;
    }

    @Override
    public String toString() {
        return "OneOfException{" +
                "value='" + value + '\'' +
                ", validValues=" + Arrays.toString(validValues) +
                '}';
    }

    public String[] validValues() {
        return validValues;
    }

    public String value() {
        return value;
    }
}
