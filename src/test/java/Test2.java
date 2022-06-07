import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.schema.DataFetchingEnvironment;
import grphaqladapter.adaptedschemabuilder.AdaptedGraphQLSchema;
import grphaqladapter.adaptedschemabuilder.AdaptedSchemaBuilder;
import grphaqladapter.adaptedschemabuilder.builtinscalars.ID;
import grphaqladapter.annotations.*;
import grphaqladapter.parser.PackageParser;
import grphaqladapter.parser.filter.NameFilter;

import java.util.Arrays;
import java.util.List;

public class Test2 {

    @GraphqlInterface
    @GraphqlDescription("represent a character")
    public static interface Character{

        @GraphqlField(nullable = false)
        ID id();
        @GraphqlField(nullable = false)
        String name();
        @GraphqlField
        List<Character> friends();
        @GraphqlField(nullable = false)
        List<Episode> appearsIn();

    }


    @GraphqlType
    public static class Human implements Character{

        @Override
        public ID id() {
            return null;
        }

        @Override
        public String name() {
            return null;
        }

        @Override
        public List<Character> friends() {
            return null;
        }

        @Override
        public List<Episode> appearsIn() {
            return null;
        }

        @GraphqlField
        public String homePlanet()
        {
            return null;
        }
    }


    @GraphqlType
    public static class Droid implements Character{

        @Override
        public ID id() {
            return new ID("Droid ID");
        }

        @Override
        public String name() {
            return "Droid";
        }

        @Override
        public List<Character> friends() {
            return null;
        }

        @Override
        public List<Episode> appearsIn() {
            return Arrays.asList(Episode.EMPIRE , Episode.JEDI);
        }

        @GraphqlField
        public String primaryFunction()
        {
            return "talking ! just talking !";
        }
    }

    @GraphqlEnum
    public static enum Episode{
        NEW_HOPE ,
        EMPIRE ,
        JEDI
    }



    @GraphqlQuery
    public static class QueryType{

        @GraphqlField
        @GraphqlDescription("to get a hero by episode")
        public Character hero(@GraphqlArgument(argumentName = "episode") Episode episode, DataFetchingEnvironment environment){
            return new Droid();
        }

        @GraphqlField
        @GraphqlDescription("to get a Human")
        public Human human(@GraphqlArgument(argumentName = "id")ID id){
            return null;
        }

        @GraphqlField
        public Droid droid(@GraphqlArgument(argumentName = "id")ID id){
            return null;
        }
    }


    public static void main(String[] args)
    {
        AdaptedGraphQLSchema schema = AdaptedSchemaBuilder.newBuilder()
                .addPackage("", NameFilter.startWith("Test2"))
                .build();

        System.out.println(schema.asSchemaDefinitionLanguage());


        System.out.println("==============================================");

        GraphQL graphQL = GraphQL.newGraphQL(schema.getSchema()).build();

        ExecutionResult result =
                graphQL.execute("{\n" +
                        "    hero(episode:JEDI)\n" +
                        "    {\n" +
                        "        name\n" +
                        "    }\n" +
                        "}");

        System.out.println(result);

    }
}
