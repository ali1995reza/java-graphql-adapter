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

package tests.T1;

import graphql.GraphQL;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tests.ExecutionResultParser;
import tests.QueryResolver;
import tests.T1.schema.Bus;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;
import static tests.T1.TestUtils.base64Hash;
import static tests.T1.TestUtils.isParameterNamePresent;


public class TestSchemaOperationsExecution {

    private final static Logger LOGGER = LoggerFactory.getLogger(TestSchemaOperationsExecution.class);


    private final GraphQL graphQL;


    public TestSchemaOperationsExecution() {

        graphQL = GraphQL.newGraphQL(TestSchemaProvider.schema().getSchema()).build();
        LOGGER.info(TestSchemaProvider.schema().asSchemaDefinitionLanguage());

    }

    private ExecutionResultParser execute(String filename) {

        String query = QueryResolver.getQuery(filename);

        LOGGER.info("Executing Query : \r\n" + query);

        ExecutionResultParser parser = ExecutionResultParser.of(graphQL.execute(query));

        LOGGER.info("Execution done with result : \r\n" + parser);

        return parser;
    }

    public void testSchemaObjectTypes() {

    }

    @Test
    public void testGetList() {
        ExecutionResultParser parser = execute("Query-1");
        assertEquals(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10), parser.getData("getList.data"));
        assertEquals(Integer.valueOf(10), parser.getData("getList.size"));
        assertEquals(Integer.valueOf(2), parser.getData("getList.get"));
        assertEquals(false, parser.getData("getList.isEmpty"));
    }

    @Test
    public void testGetListBySingleBoundArgument() {
        ExecutionResultParser parser = execute("Query-2");
        assertEquals(Arrays.asList(11), parser.getData("getList.data"));
        assertEquals(Integer.valueOf(1), parser.getData("getList.size"));
        assertEquals(Integer.valueOf(11), parser.getData("getList.get"));
        assertEquals(false, parser.getData("getList.isEmpty"));
    }

    @Test
    public void testPartialRequestAndFieldDirective() {
        ExecutionResultParser parser = execute("Query-3");
        assertEquals(Arrays.asList(5, 4, 3, 2, 1), parser.getData("getList.data"));
        assertEquals(Integer.valueOf(5), parser.getData("getList.size"));
        assertNull(parser.getData("getList.get"));
        assertNull(parser.getData("getList.isEmpty"));
    }

    @Test
    public void testMultiDimensionArrays() {
        ExecutionResultParser parser = execute("Query-4");
        assertEquals(Arrays.asList(Arrays.asList(8, 8), Arrays.asList(8, 8)), parser.getData("multiplyMatrices"));
    }

    @Test
    public void testGetInterfaceAndInputType() {
        ExecutionResultParser parser = execute("Query-5");
        assertEquals("name", parser.getData("getUser.name"));
        assertEquals("ADMIN", parser.getData("getUser.type"));
        assertNotNull(parser.getData("getUser.token"));
    }

    @Test
    public void testFragments() {
        ExecutionResultParser parser = execute("Query-6");
        assertEquals(Bus.class.getSimpleName(), parser.getData("getVehicle.__typename"));
        assertNotNull(parser.getData("getVehicle.model"));
        assertNotNull(parser.getData("getVehicle.produceYear"));
        assertNotNull(parser.getData("getVehicle.size"));
    }

    @Test
    public void testPreHandleFieldDirective() {
        ExecutionResultParser parser = execute(isParameterNamePresent() ? "Query-7-2" : "Query-7-1");
        assertNotNull(parser.getData("getBankAccount.id"));
        assertNotNull(parser.getData("getBankAccount.username"));
        assertNotNull(parser.getData("getBankAccount.balance"));
    }

    @Test
    public void testSystemParams() {
        ExecutionResultParser parser = execute("Query-8");
        assertTrue((boolean) parser.getData("isSystemParamsHealthy"));
    }

    @Test
    public void testDirectivesHolder() {
        ExecutionResultParser parser = execute("Query-9");
        assertTrue((boolean) parser.getData("isDirectivesHealthy"));
    }

    @Test
    public void testFieldDirective() {
        ExecutionResultParser parser = execute("Query-10");
        assertEquals("ALIREZA AKHOUNDI", parser.getData("getDeveloperName"));
    }

    @Test
    public void testFieldDoubleDirective() {
        ExecutionResultParser parser = execute("Query-11");
        assertEquals("IDNUOHKA AZERILA", parser.getData("getDeveloperName"));
    }

    @Test
    public void testChangeArgumentParams() {
        ExecutionResultParser parser = execute("Query-12");
        assertEquals(Integer.valueOf(10), parser.getData("getPageDetails.page"));
        assertEquals(Integer.valueOf(20), parser.getData("getPageDetails.size"));
    }

    @Test
    public void testDefaultArgumentValue() {
        ExecutionResultParser parser = execute("Query-13");
        assertEquals("k1:v1:1:k2:v2:2:k3:v3:3", parser.getData("serializeToString"));
    }

    @Test
    public void testNullSpecifiedDefaultArgumentValue() {
        ExecutionResultParser parser = execute("Query-14");
        assertEquals("", parser.getData("serializeToString"));
    }

    @Test
    public void testDefaultInputFieldValue() {
        ExecutionResultParser parser = execute("Query-15");
        assertEquals("mn,mv,99,dn,dv,100,idn,idv,101", parser.getData("serializeToString"));
    }

    @Test
    public void testNullSpecifiedDefaultInputFieldValue() {
        ExecutionResultParser parser = execute("Query-16");
        assertEquals("mn,mv,99", parser.getData("serializeToString"));
    }

    @Test
    public void testOperationDirectiveDefaultArgumentValue() {
        ExecutionResultParser parser = execute("Query-17");
        assertEquals("k1:v1:1:k2:v2:2:k3:v3:3", parser.getData("serializeToStringFromDirective"));
    }

    @Test
    public void testOperationDirectiveNullSpecifiedDefaultArgumentValue() {
        ExecutionResultParser parser = execute("Query-18");
        assertEquals("", parser.getData("serializeToStringFromDirective"));
    }

    @Test
    public void testOperationDirectiveDefaultInputFieldValue() {
        ExecutionResultParser parser = execute("Query-19");
        assertEquals("mn,mv,99,dn,dv,100,idn,idv,101", parser.getData("serializeToStringFromDirective"));
    }

    @Test
    public void testOperationDirectiveNullSpecifiedDefaultInputFieldValue() {
        ExecutionResultParser parser = execute("Query-20");
        assertEquals("mn,mv,99", parser.getData("serializeToStringFromDirective"));
    }

    @Test
    public void testFieldDirectiveDefaultArgumentValue() {
        ExecutionResultParser parser = execute("Query-21");
        assertEquals("k1:v1:1:k2:v2:2:k3:v3:3", parser.getData("serializeToStringFromDirective"));
    }

    @Test
    public void testFieldDirectiveNullSpecifiedDefaultArgumentValue() {
        ExecutionResultParser parser = execute("Query-22");
        assertEquals("", parser.getData("serializeToStringFromDirective"));
    }

    @Test
    public void testFieldDirectiveDefaultInputFieldValue() {
        ExecutionResultParser parser = execute("Query-23");
        assertEquals("mn,mv,98,mn2,mv2,99,dn,dv,100,idn,idv,101", parser.getData("serializeToStringFromDirective"));
    }

    @Test
    public void testFieldDirectiveNullSpecifiedDefaultInputFieldValue() {
        ExecutionResultParser parser = execute("Query-24");
        assertEquals("mn,mv,99", parser.getData("serializeToStringFromDirective"));
    }

    @Test
    public void testListAndArray() {
        ExecutionResultParser parser = execute(isParameterNamePresent() ? "Mutation-1-2" : "Mutation-1-1");
        assertEquals(Arrays.asList(6, 5, 4, 3, 2, 1), parser.getData("listToArray"));
    }

    @Test
    public void testMultipleFieldDirectives() {
        ExecutionResultParser parser = execute("Mutation-2");

        String expected = Base64.getEncoder().encodeToString("some_input".getBytes(StandardCharsets.UTF_8));
        expected = base64Hash(expected, "MD5");
        expected = new StringBuffer().append(expected).reverse().toString();

        assertEquals(expected, parser.getData("encodeToBase64"));
    }

    @Test
    public void test3DArrays() {
        ExecutionResultParser parser = execute(isParameterNamePresent() ? "Mutation-3-2" : "Mutation-3-1");
        assertEquals(
                Arrays.asList(Arrays.asList(Arrays.asList(1, 2, 3), Arrays.asList(4, 5, 6), Arrays.asList(7, 8, 9), Arrays.asList(10, 11, 12)),
                        Arrays.asList(Arrays.asList(13, 14, 15), Arrays.asList(16, 17, 18), Arrays.asList(19, 20, 21), Arrays.asList(22, 23, 24)))
                , parser.getData("combineInto3DMatrix")
        );
    }
}
