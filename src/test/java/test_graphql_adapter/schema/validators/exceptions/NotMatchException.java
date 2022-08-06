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

import java.util.Objects;

public class NotMatchException extends GraphqlValidationException {

    private final String pattern;
    private final String value;

    public NotMatchException(String e, String pattern, String value) {
        super(e);
        this.pattern = pattern;
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NotMatchException that = (NotMatchException) o;
        return Objects.equals(pattern, that.pattern) && Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pattern, value);
    }

    @Override
    public String toString() {
        return "NotMatchException{" +
                "pattern='" + pattern + '\'' +
                ", value='" + value + '\'' +
                '}';
    }

    public String pattern() {
        return pattern;
    }

    public String value() {
        return value;
    }
}
