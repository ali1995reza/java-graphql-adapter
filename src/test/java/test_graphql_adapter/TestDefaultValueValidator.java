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
package test_graphql_adapter;

import graphql_adapter.adaptedschema.AdaptedGraphQLSchema;
import graphql_adapter.adaptedschema.exceptions.GraphqlValidationException;
import graphql_adapter.annotations.*;
import org.junit.jupiter.api.Test;
import test_graphql_adapter.schema.validators.Match;
import test_graphql_adapter.schema.validators.OneOf;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class TestDefaultValueValidator {

    @Test
    public void testArgumentDefaultValueValidation() {
        assertThrows(GraphqlValidationException.class, () -> buildSchema(InvalidQuery.class));
    }

    @Test
    public void testComplexArgumentDefaultValueValidation() {
        assertThrows(GraphqlValidationException.class, () -> buildSchema(InvalidComplexQuery.class, ComplexArgument.class));
    }

    @Test
    public void testComplexInputFieldDefaultValueValidation() {
        assertThrows(GraphqlValidationException.class, () -> buildSchema(ValidQuery.class, InvalidComplexInput.class));
    }

    @Test
    public void testInputFieldDefaultValueValidation() {
        assertThrows(GraphqlValidationException.class, () -> buildSchema(ValidQuery.class, InvalidInput.class));
    }

    @Test
    public void testValidSchema() {
        buildSchema(ValidQuery.class, ValidInput.class, ValidComplexInput.class);
    }

    private static AdaptedGraphQLSchema buildSchema(Class<?> clazz, Class<?>... classes) {
        return AdaptedGraphQLSchema
                .newSchema()
                .add(clazz, classes)
                .build();
    }

    @GraphqlQuery
    public static class InvalidQuery {

        public boolean getSomething(
                @OneOf({"a", "b"})
                @DefaultValue("something")
                @GraphqlArgument(name = "arg") String arg
        ) {
            return true;
        }
    }

    @GraphqlQuery
    public static class ValidQuery {

        public boolean getBoolean() {
            return true;
        }
    }

    @GraphqlInputType
    public static class InvalidInput {

        private String value;

        @Match("[A-Za-z]+")
        @DefaultValue("a10")
        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    @GraphqlInputType
    public static class ValidInput {

        private String value;

        @Match("[A-Za-z0-9]+")
        @DefaultValue("a10")
        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    @GraphqlInputType
    public static class InvalidComplexInput {
        private String value;
        private InvalidComplexInput[] inners;

        @DefaultValue("[{value:'v1', inners:[{value:'v2', inners:[{value:'d%'}]},null]}]")
        public InvalidComplexInput[] getInners() {
            return inners;
        }

        public void setInners(InvalidComplexInput[] inners) {
            this.inners = inners;
        }

        @Match("[A-Za-z0-9]+")
        @DefaultValue("a10")
        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    @GraphqlInputType
    public static class ValidComplexInput {
        private String value;
        private ValidComplexInput[] inners;

        @DefaultValue("[{value:'v1', inners:[{value:'v2', inners:[{value:'v3'}]},null]}]")
        public ValidComplexInput[] getInners() {
            return inners;
        }

        public void setInners(ValidComplexInput[] inners) {
            this.inners = inners;
        }

        @Match("[A-Za-z0-9]+")
        @DefaultValue("a10")
        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    @GraphqlInputType
    public static class ComplexArgument {

        private String value;
        private List<ComplexArgument> inners;

        public List<ComplexArgument> getInners() {
            return inners;
        }

        public void setInners(List<ComplexArgument> inners) {
            this.inners = inners;
        }

        @Match("[A-Za-z0-9]+")
        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    @GraphqlQuery
    public static class InvalidComplexQuery {

        @GraphqlField
        public @GraphqlNonNull boolean something(
                @DefaultValue("{value:'v1', inners:[{value:'v2', inners:[{value:'d%'}]},null]}")
                @GraphqlArgument(name = "arg") ComplexArgument argument) {
            return true;
        }
    }
}
