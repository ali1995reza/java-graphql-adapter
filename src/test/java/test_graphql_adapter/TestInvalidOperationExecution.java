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

import graphql.ExceptionWhileDataFetching;
import graphql.GraphQL;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import test_graphql_adapter.schema.TestSchemaProvider;
import test_graphql_adapter.schema.validators.exceptions.*;
import test_graphql_adapter.utils.ExecutionResultParser;
import test_graphql_adapter.utils.OperationResolver;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestInvalidOperationExecution {

    private final static Logger LOGGER = LoggerFactory.getLogger(TestSchemaOperationsExecution.class);

    private final GraphQL graphQL;

    public TestInvalidOperationExecution() {
        graphQL = GraphQL.newGraphQL(TestSchemaProvider.schema().graphQLSchema()).build();
    }

    @Test
    public void testInvalidComplexParameter() {
        ExecutionResultParser parser = execute("InvalidQuery-2");
        assertThrows(new NotOneOfException("any", "-12345", "-12345", "-123456", "-1234567"), parser);
    }

    @Test
    public void testInvalidComplexParameter2() {
        ExecutionResultParser parser = execute("InvalidQuery-3");
        assertThrows(new NotMatchException("any", "[A-Za-z0-9]+", "n+2"), parser);
    }

    @Test
    public void testInvalidDirectiveArgument() {
        ExecutionResultParser parser = execute("InvalidMutation-1");
        assertThrows(new OneOfException("any", "MD-5", "SHA-256", "MD5"), parser);
    }

    @Test
    public void testInvalidDirectiveArrayArgument() {
        ExecutionResultParser parser = execute("InvalidMutation-2");
        assertThrows(new FooValidationException(), parser);
    }

    @Test
    public void testInvalidDirectiveListArgument() {
        ExecutionResultParser parser = execute("InvalidMutation-3");
        assertThrows(new FooValidationException(), parser);
    }

    @Test
    public void testInvalidParameter() {
        ExecutionResultParser parser = execute("InvalidQuery-1");
        assertThrows(new NegativeException("any", -1), parser);
    }

    private static <T extends Throwable> void assertThrows(T expectedException, ExecutionResultParser parser) {
        assertTrue(parser.hasError());
        assertEquals(parser.getResult().getErrors().size(), 1);
        ExceptionWhileDataFetching exception = (ExceptionWhileDataFetching) parser.getResult().getErrors().get(0);
        assertEquals(expectedException.getClass(), exception.getException().getClass());
        assertEquals(expectedException, exception.getException());
    }

    private ExecutionResultParser execute(String filename) {

        String query = OperationResolver.getOperation(filename);

        LOGGER.info("Executing Query : \r\n" + query);

        ExecutionResultParser parser = ExecutionResultParser.of(graphQL.execute(query));

        LOGGER.info("Execution done with result : \r\n" + parser);

        return parser;
    }
}
