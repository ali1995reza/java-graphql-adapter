import graphql.ExecutionResult;
import graphql.GraphQL;
import grphaqladapter.adaptedschemabuilder.AdaptedGraphQLSchema;
import grphaqladapter.adaptedschemabuilder.AdaptedSchemaBuilder;
import grphaqladapter.adaptedschemabuilder.builtinscalars.ID;
import grphaqladapter.annotations.*;

import javax.xml.ws.FaultAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Test2 {

    @GraphqlInterface
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
        public Character hero(@GraphqlArgument(argumentName = "episode") Episode episode){
            return new Droid();
        }

        @GraphqlField
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
                .add(Character.class
                        , Human.class
                        , Droid.class
                        , Episode.class
                        , QueryType.class)
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
