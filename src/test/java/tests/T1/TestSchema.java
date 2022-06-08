package tests.T1;

import graphql.GraphQL;
import grphaqladapter.adaptedschemabuilder.AdaptedGraphQLSchema;
import grphaqladapter.adaptedschemabuilder.AdaptedSchemaBuilder;
import grphaqladapter.adaptedschemabuilder.scalar.impl.ScalarEntryBuilder;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tests.ExecutionResultParser;
import tests.QueryResolver;
import tests.T1.schema.IntPeriodScalar;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TestSchema {

    private final static Logger LOGGER = LoggerFactory.getLogger(TestSchema.class);

    private final GraphQL graphQL;

    public TestSchema() {
        AdaptedGraphQLSchema adaptedSchema = AdaptedSchemaBuilder
                .newBuilder()
                .addPackage("tests.T1.schema")
                .addScalar(
                        ScalarEntryBuilder.newBuilder()
                                .setName("Period")
                                .setCoercing(IntPeriodScalar.Coercing)
                                .setType(IntPeriodScalar.class)
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
}
