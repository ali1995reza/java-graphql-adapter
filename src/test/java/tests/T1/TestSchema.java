package tests.T1;

import graphql.GraphQL;
import grphaqladapter.adaptedschemabuilder.AdaptedGraphQLSchema;
import grphaqladapter.adaptedschemabuilder.AdaptedGraphQLSchemaBuilder;
import grphaqladapter.adaptedschemabuilder.scalar.impl.ScalarEntryBuilder;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tests.ExecutionResultParser;
import tests.QueryResolver;
import tests.T1.schema.Bus;
import tests.T1.schema.CustomObjectConstructor;
import tests.T1.schema.IntPeriodScalar;
import tests.T1.schema.UserType;
import tests.T1.schema.directives.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TestSchema {

    private final static Logger LOGGER = LoggerFactory.getLogger(TestSchema.class);

    private final GraphQL graphQL;

    public TestSchema() {
        AdaptedGraphQLSchema adaptedSchema = AdaptedGraphQLSchemaBuilder
                .newBuilder()
                .addPackage("tests.T1.schema")
                .add(Delay.class)
                .add(UpperCase.class)
                .add(Authentication.class)
                .add(ToString.class)
                .add(MD5.class)
                .add(AddPageParameters.class)
                .objectConstructor(new CustomObjectConstructor())
                .addScalar(
                        ScalarEntryBuilder.newBuilder()
                                .name("Period")
                                .coercing(IntPeriodScalar.Coercing)
                                .type(IntPeriodScalar.class)
                                .build()
                )
                .build();
        graphQL = GraphQL.newGraphQL(adaptedSchema.getSchema()).build();

        LOGGER.info(adaptedSchema.asSchemaDefinitionLanguage());
    }

    private ExecutionResultParser execute(String filename) {

        String query = QueryResolver.getQuery(filename);

        LOGGER.info("Executing Query : \r\n" + query);

        ExecutionResultParser parser = ExecutionResultParser.of(graphQL.execute(query));

        LOGGER.info("Execution done with result : \r\n" + parser);

        return parser;
    }

    @Test
    public void testGetList() {
        ExecutionResultParser parser = execute("Query-1");
        List<Integer> list = parser.getData("getList.data");
        Integer item = parser.getData("getList.get");
        assertNotNull(list);
        assertEquals(list.size(), 10);
        assertEquals(item, 2);
    }

    @Test
    public void testBadArgument() {
        assertThrows(IllegalStateException.class, () -> execute("Query-2"));
    }

    @Test
    public void testPartialRequest() {
        ExecutionResultParser parser = execute("Query-3");
        assertNotNull(parser.getData("getList.data"));
        assertNotNull(parser.getData("getList.size"));
        assertNull(parser.getData("getList.get"));
        assertNull(parser.getData("getList.isEmpty"));
    }

    @Test
    public void testMultiDimensionArrays() {
        ExecutionResultParser parser = execute("Query-4");
        List<List<Integer>> resultMatrix = parser.getData("multiplyMatrix");

        assertEquals(resultMatrix.size(), 2);
        for (List<Integer> row : resultMatrix) {
            assertEquals(row.size(), 2);
            for (Integer i : row) {
                assertEquals(i, 4);
            }
        }
    }

    @Test
    public void testGetInterfaceAndInputType() {
        ExecutionResultParser parser = execute("Query-5");
        assertEquals(parser.getData("getUser.name"), "name");
        assertEquals(parser.getData("getUser.type"), UserType.ADMIN.name());
        assertNotNull(parser.getData("getUser.token"));
    }

    @Test
    public void testFragments() {
        ExecutionResultParser parser = execute("Query-6");
        assertEquals(parser.getData("getVehicle.__typename"), Bus.class.getSimpleName());
        assertNotNull(parser.getData("getVehicle.model"));
        assertNotNull(parser.getData("getVehicle.produceYear"));
        assertNotNull(parser.getData("getVehicle.size"));
    }

    @Test
    public void testPreHandleFieldDirective() {
        ExecutionResultParser parser = execute("Query-7");
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
        assertEquals(parser.getData("getDeveloperName"), "ALIREZA AKHOUNDI");
    }

    @Test
    public void testChangeArgumentParams() {
        ExecutionResultParser parser = execute("Query-11");
        assertEquals(parser.getData("getPageDetails.page"), Integer.valueOf(10));
        assertEquals(parser.getData("getPageDetails.size"), Integer.valueOf(20));
    }
}
